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

    //array that stores which processors tasks have been scheduled on
    private int[] taskProcessors;

    //Array that stores the earliest free time a task can be scheduled on each processor
    private int[] processorFinishTimes;

    //CONSTRUCTOR
    public Schedule(Graph taskGraph, int task, int time, int processor, int idleTime, int estimate, int processorsUsed, List<Integer> doableTasks, int[] taskStartTimes, int[] taskProcessors, int[] processorFinishTimes) {
        this.taskGraph = taskGraph;
        //this.task = task;
        //this.time = time;
        //this.processor = processor;
        this.idleTime = idleTime;
        this.estimate = estimate;
        this.processorsUsed = processorsUsed;
        this.doableTasks = doableTasks;
        this.taskStartTimes = taskStartTimes;
        this.taskProcessors = taskProcessors;
        this.processorFinishTimes = processorFinishTimes;

    }

    /**
     * Creates an empty schedule to use as the start of the state tree traversal/generation
     * @return the empty schedules
     */
    public static Schedule getEmptySchedule(Graph taskGraph){
        int[] taskStartTime = new int[taskGraph.getTotalNumTasks()];
        Arrays.fill(taskStartTime, -1);
        int[] taskProcessors = new int[taskGraph.getTotalNumTasks()];
        Arrays.fill(taskProcessors,-1);
        int[] processFinishTime = new int[taskGraph.getNumProcessors()];

        return new Schedule(taskGraph,-1,-1,0,0,0,0, taskGraph.getEntryPoints(),taskStartTime,taskProcessors,processFinishTime);
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

        for (int finishTime:processorFinishTimes) {
            totalTime = Math.max(totalTime, finishTime);
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

                //finish time of last task scheduled on this processor
                //only need to find the last task scheduled on this processor
                int earliestStartTimeOnThisProcessor = processorFinishTimes[processorWeAreTryingToScheduleOn];

                //maximum start time + node cost + communication cost of all dependencies
                int earliestStartTimeOfThisTask = 0;

                //as we are going up the tree looking for dependencies, keep going until either there are no nodes left,
                //or if all the dependencies and the earliest start time on this process is found.
                for(int dependency :dependencyList) {
                    if(taskProcessors[dependency]==processorWeAreTryingToScheduleOn){
                        earliestStartTimeOfThisTask = Math.max(earliestStartTimeOfThisTask, taskStartTimes[dependency] + taskGraph.getNodeCost(dependency));
                    }else{
                        earliestStartTimeOfThisTask = Math.max(earliestStartTimeOfThisTask, taskStartTimes[dependency] + taskGraph.getNodeCost(dependency) + taskGraph.getEdgeCost(dependency,taskYouAreTryingToSchedule));
                    }
                }

                //STARTING FROM HERE, WE ARE JUST CALCULATING THE REMAINING FIELDS WE NEED FOR THE CHILD

                //start time is the latest of when the processor is free, and when its dependencies are filled
                int startTime = Math.max(earliestStartTimeOfThisTask, earliestStartTimeOnThisProcessor);

                //if extra idle time is created, then add it to the idletime counter
                int updatedIdleTime = idleTime + startTime - earliestStartTimeOnThisProcessor;

                //recalculate the estimated time of completion for the partial schedule
                int updatedEstimate = Math.max(
                        (updatedIdleTime + taskGraph.getTotalTaskTime())/taskGraph.getNumProcessors(),
                        taskGraph.getBottomLevel(taskYouAreTryingToSchedule) + startTime);

                //max it with the old estimate
                //not sure if this line is needed, can check later
                updatedEstimate = Math.max(updatedEstimate, estimate);

                //now update doable task list
                List<Integer> updatedDoableTasks = new ArrayList<Integer>(doableTasks);

                //remove the task we just scheduled from the list
                //this line worked but might be slower so we use faster
                //updatedDoableTasks.remove(new Integer(taskYouAreTryingToSchedule));
                updatedDoableTasks.remove(Integer.valueOf(taskYouAreTryingToSchedule));

                
                //need to update the start times of tasks
                int[] updatedTaskStartTimes = new int[taskGraph.getTotalNumTasks()];
                System.arraycopy(taskStartTimes, 0, updatedTaskStartTimes, 0, taskStartTimes.length);
                updatedTaskStartTimes[taskYouAreTryingToSchedule] = startTime;
                
                
                //###CONFUSING SO PAY ATTENTION###
                //now looking at the task graph, NOT the partial schedule tree,
                //all the children of the task we just scheduled have a chance to be doable now
                //so check through all of the childrens dependencies to see if the rest are satisfied
                //if they are, then add them to the doable tasks list as it is now a valid node to be scheduled
                for (int child:taskGraph.getChildren(taskYouAreTryingToSchedule)) {
                    boolean isDoable = true;
                    for (int dependency: taskGraph.getDependencies(child)) {
                        if (updatedTaskStartTimes[dependency] == -1) {
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
                //if the processor we scheduled on was an empty one, then the new value of processors used for the new child is incremented
                int updatedProcessorsUsed = (processorWeAreTryingToScheduleOn == processorsUsed)?processorsUsed + 1: processorsUsed;

                //need to update the processors each task is scheduled on
                int[] updatedTaskProcessors=new int[taskGraph.getTotalNumTasks()];
                System.arraycopy(taskProcessors, 0, updatedTaskProcessors, 0, taskProcessors.length);
                updatedTaskProcessors[taskYouAreTryingToSchedule] = processorWeAreTryingToScheduleOn;

                //need to update when each processor is free
                int[] updatedProcessFinishTimes = new int[taskGraph.getNumProcessors()];
                System.arraycopy(processorFinishTimes, 0, updatedProcessFinishTimes, 0, processorFinishTimes.length);
                updatedProcessFinishTimes[processorWeAreTryingToScheduleOn] = startTime + taskGraph.getNodeCost(taskYouAreTryingToSchedule);

                //add then new child with all the new values we calculated
                children.add(new Schedule(taskGraph, taskYouAreTryingToSchedule,
                        startTime, processorWeAreTryingToScheduleOn,
                        updatedIdleTime, updatedEstimate, updatedProcessorsUsed, updatedDoableTasks, updatedTaskStartTimes, updatedTaskProcessors, updatedProcessFinishTimes));
            }
        }

        //return the final list of all the child nodes that were generated
        return children;
    }

    @Override
    public boolean equals(Object other){
        return other instanceof Schedule &&
                Arrays.equals(taskStartTimes,((Schedule)other).taskStartTimes) &&
                Arrays.equals(taskProcessors,((Schedule)other).taskProcessors);
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

