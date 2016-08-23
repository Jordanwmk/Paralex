import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Schedule implements Comparable<Schedule>{
    private Graph taskGraph;

    //pointer to the nodes parent
    private Schedule parent;

    //the task that is scheduled (eg task = 0 means task A)
    private int task;

    //time that the task starts
    private int time;

    //processor that the task is scheduled on
    //a path from the root to the leaf node will represent a full schedule where every node is the assignment of a task
    private int processor;

    //the total idletime for this partial schedule
    private int idleTime;

    //the estimated finish time for this partial schedule
    private int estimate;

    //the number of occupied processors (processors that have atleast 1 task assigned to it)
    private int processorsUsed;

    //the tasks that can be added to keep the schedule valid
    private List<Integer> doableTasks;

    //array that stores the start times for each of the tasks, -1 if the task has not been scheduled yet
    private int[] taskStartTimes;

    //Array that stores the earliest free time a task can be scheduled on each processor
    private int[] processorFinishTimes;

    //CONSTRUCTOR
    public Schedule(Graph taskGraph, Schedule parent, int task, int time, int processor, int idleTime, int estimate, int processorsUsed, List<Integer> doableTasks, int[] taskStartTimes, int[] processorFinishTimes) {
        this.taskGraph = taskGraph;
        this.parent = parent;
        this.task = task;
        this.time = time;
        this.processor = processor;
        this.idleTime = idleTime;
        this.estimate = estimate;
        this.processorsUsed = processorsUsed;
        this.doableTasks = doableTasks;
        this.taskStartTimes = taskStartTimes;
        this.processorFinishTimes = processorFinishTimes;

    }

    /**
     * Creates an empty schedule to use as the start of the state tree traversal/generation
     * @return the empty schedules
     */
    public static Schedule getEmptySchedule(Graph taskGraph){
        int[] taskStartTime = new int[taskGraph.getTotalNumTasks()];
        Arrays.fill(taskStartTime, -1);
        int[] processFinishTime = new int[taskGraph.getNumProcessors()];
        return new Schedule(taskGraph,null,-1,-1,0,0,0,0, taskGraph.getEntryPoints(),taskStartTime,processFinishTime);
    }

    //needed so that the priority queue can sort the schedules in terms of estimated finish time
    public int compareTo(Schedule other) {
        return this.estimate-other.estimate;
    }

    //not needed in the final implementation but for testing
    public String toString() {

        if (this.parent != null && this.parent.task!=-1) {
            System.out.println(this.parent);
        }

        return ("Processor: " + processor + "  Time: " + time + "  Task: " + task );
    }

    public int getTotalTime(){
        int totalTime=0;
        Schedule scheduleWeAreCurrentlyInspecting=this;
        while(scheduleWeAreCurrentlyInspecting!=null && scheduleWeAreCurrentlyInspecting.task!=-1){
            totalTime=Math.max(totalTime,scheduleWeAreCurrentlyInspecting.time+taskGraph.getNodeCost(scheduleWeAreCurrentlyInspecting.task));
            scheduleWeAreCurrentlyInspecting=scheduleWeAreCurrentlyInspecting.parent;
        }
        return totalTime;
    }

    public int getFinishTimeEstimate(){
        return estimate;
    }

    //method that returns a list of valid children nodes
    public List<Schedule> generateChildren() {
        //list used for storing all the children. will be returned
        List<Schedule> children = new ArrayList<Schedule>();
        //graph singleton that has all the edge costs and node weights, BLs etc.

        //setting the size of the array to the total number of tasks
        //boolean set to true if the task index is completed
        boolean[] completedTasks = new boolean[taskGraph.getTotalNumTasks()];
        //start by looking at the current node
        Schedule scheduleCurrentlyInspecting = this;

        //starting from this node, go up the tree to the root and find all the tasks that have been completed
        //this will update the completedTasks boolean array
        while(scheduleCurrentlyInspecting.task!=-1) {
            completedTasks[scheduleCurrentlyInspecting.task] = true;
            scheduleCurrentlyInspecting=scheduleCurrentlyInspecting.parent;
        }

        //for all the tasks that are valid to add as a child, we will generate a child by adding
        //it to each of the occupied processors, and 1 of the empty processors

        //*another explanation* each doable task is scheduled in each of the occupied processors and 1 of the empty processors
        //we only schedule into 1 of the empty processors to avoid duplicated schedules
        //all the empty processors are identical, so adding to more than one of them will create duplicate schedules
        for (int taskYouAreTryingToSchedule: doableTasks) {
            List<Integer> dependencyList = taskGraph.getDependencies(taskYouAreTryingToSchedule);

            //adding a task to all occupied processors and the one empty processor
            for (int processorWeAreTryingToScheduleOn = 0;

                 //the +1 lets the for loop allows the task to be added to one of the empty processors
                 //the min makes sure that when there are no empty ones left, that it doesnt overflow
                 processorWeAreTryingToScheduleOn < Math.min(processorsUsed+1,taskGraph.getNumProcessors());
                 processorWeAreTryingToScheduleOn++) {

                Schedule scheduleYouAreCurrentlyInspecting = this;

                //finish time of last task scheduled on this processor
                //only need to find the last task scheduled on this processor
                int earliestStartTimeOnThisProcessor = processorFinishTimes[processorWeAreTryingToScheduleOn];

                //maximum start time + node cost + communication cost of all dependencies
                int earliestStartTimeOfThisTask = -1;

                //a counter for how many dependencies the task we are trying to schedule has
                //when its 0, we know we have gone through all its dependencies
                int dependenciesRemaining = dependencyList.size();

                //as we are going up the tree looking for dependencies, keep going until either there are no nodes left,
                //or if all the dependencies and the earliest start time on this process is found.

                for(int task:dependencyList) {
                    earliestStartTimeOnThisProcessor = Math.max(earliestStartTimeOnThisProcessor, taskStartTimes[task] + taskGraph.getNodeCost(task));
                }

                //STARTING FROM HERE, WE ARE JUST CALCULATING THE REMAINING FIELDS WE NEED FOR THE CHILD

                //start time is the latest of when the processor is free, and when its dependencies are filled
                int startTime = Math.max(earliestStartTimeOfThisTask, earliestStartTimeOnThisProcessor);

                //if extra idle time is created, then add it to the idletime counter
                int updatedIdleTime = idleTime + startTime - earliestStartTimeOnThisProcessor;

                //recalculate the estimated time of completion for the partial schedule
                int updatedEstimate = Math.max((updatedIdleTime + taskGraph.getTotalTaskTime())/taskGraph.getNumProcessors(), taskGraph.getBottomLevel(taskYouAreTryingToSchedule) + startTime);

                //max it with the old estimate
                //not sure if this line is needed, can check later
                int est = Math.max(updatedEstimate, estimate);

                //now update doable task list
                List<Integer> updatedDoableTasks = new ArrayList<Integer>(doableTasks);

                //remove the task we just scheduled from the list
                //this line workd but might be slower so we use faster
                //updatedDoableTasks.remove(new Integer(taskYouAreTryingToSchedule));
                updatedDoableTasks.remove(Integer.valueOf(taskYouAreTryingToSchedule));

                //###CONFUSING SO PAY ATTENTION###
                //now looking at the task graph, NOT the partial schedule tree,
                //all the children of the task we just scheduled have a chance to be doable now
                //so check through all of the childrens dependencies to see if the rest are satisfied
                //if they are, then add them to the doable tasks list as it is now a valid node to be scheduled
                for (int child:taskGraph.getChildren(taskYouAreTryingToSchedule)) {
                    boolean isDoable = true;
                    for (int dependency: taskGraph.getDependencies(child)) {
                        if (!completedTasks[dependency] && dependency != taskYouAreTryingToSchedule) {
                            //as soon as a dependency is not met, break and go to the next child
                            isDoable = false;
                            break;
                        }
                    }
                    //when all the dependencies are met, then add it to the doable tasks list
                    if (isDoable) {
                        updatedDoableTasks.add(child);
                    }
                }

                //nothing to do with doabletasks now, just updateing other fields
                //update how many processors are occupied (not empty)
                int updatedProcessorsUsed = processorsUsed;

                //if the processor we scheduled on was an empty one, then the new value of processors used for the new child is incremented
                if (processorWeAreTryingToScheduleOn == processorsUsed) {
                    updatedProcessorsUsed ++;
                }

                //need to update the start times of tasks
                int[] updatedTaskStartTimes = new int[taskGraph.getTotalNumTasks()];
                System.arraycopy(taskStartTimes, 0, updatedTaskStartTimes, 0, taskStartTimes.length);
                updatedTaskStartTimes[taskYouAreTryingToSchedule] = startTime;

                //need to update when each processor is free
                int[] updatedProcessFinishTimes = new int[taskGraph.getTotalNumTasks()];
                System.arraycopy(processorFinishTimes, 0, updatedProcessFinishTimes, 0, taskGraph.getTotalNumTasks());
                updatedProcessFinishTimes[processorWeAreTryingToScheduleOn] = startTime + taskGraph.getNodeCost(taskYouAreTryingToSchedule);


                //add then new child with all the new values we calculated
                children.add(new Schedule(taskGraph,this, taskYouAreTryingToSchedule,
                        startTime, processorWeAreTryingToScheduleOn,
                        updatedIdleTime, est, updatedProcessorsUsed, updatedDoableTasks, updatedTaskStartTimes, updatedProcessFinishTimes));
            }
        }

        //return the final list of all the child nodes that were generated
        return children;
    }


	public Schedule getParent() {
		return parent;
	}

	public int getTask() {
		return task;
	}

	public int getTime() {
		return time;
	}

	public int getProcessor() {
		return processor;
	}

	public int getIdleTime() {
		return idleTime;
	}

	public int getEstimate() {
		return estimate;
	}

	public int getProcessorsUsed() {
		return processorsUsed;
	}

	public List<Integer> getDoableTasks() {
		return doableTasks;
	}
}

