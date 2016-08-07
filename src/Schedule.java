import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hanzhi on 1/08/2016.
 */
public class Schedule implements Comparable<Schedule>{
    private Schedule parent;
    private int task;
    private int time;
    private int processor;
    private int idleTime;
    private int estimate;
    private int processorsUsed;
    private List<Integer> doableTasks;

    public Schedule(Schedule parent, int task, int time, int processor, int idleTime, int estimate, int processorsUsed, List<Integer> doableTasks) {
        this.parent = parent;
        this.task = task;
        this.time = time;
        this.processor = processor;
        this.idleTime = idleTime;
        this.estimate = estimate;
        this.processorsUsed = processorsUsed;
        this.doableTasks = doableTasks;

    }

    public int compareTo(Schedule other) {
        return other.estimate-this.estimate;
    }

    public String toString() {

        if (this.parent != null) {
            System.out.println(this.parent);
        }
        return ("Processor: " + processor + "  Time: " + time + "  Task: " + task );
    }

    List<Schedule> generateChildren() {
        List<Schedule> children = new ArrayList<Schedule>();
        Graph taskGraph = Graph.getInstance();
        boolean[] completedTasks = new boolean[taskGraph.getTotalNumTasks()];
        Schedule scheduleCurrentlyInspecting = this;

        while(scheduleCurrentlyInspecting != null) {
            completedTasks[scheduleCurrentlyInspecting.task] = true;
            scheduleCurrentlyInspecting=scheduleCurrentlyInspecting.parent;
        }

        for (int taskYouAreTryingToSchedule: doableTasks) {
            List<Integer> dependencyList = taskGraph.getDependencies(taskYouAreTryingToSchedule);

            //adding a task to all occupied processors
            for (int processorWeAreTryingToScheduleOn = 0;
                 processorWeAreTryingToScheduleOn < Math.min(processorsUsed+1,taskGraph.getNumProcessors());
                 processorWeAreTryingToScheduleOn++) {
                Schedule scheduleYouAreCurrentlyInspecting = this;
                int earliestStartTimeOnThisProcessor = -1; //finish time of last task schduled on this processor
                int earliestStartTimeOfThisTask = -1; //maximum starttime + node cost + communication cost of all dependancies
                int dependenciesRemaining = dependencyList.size();
                while(scheduleYouAreCurrentlyInspecting != null && (dependenciesRemaining != 0 || earliestStartTimeOnThisProcessor == -1)) {

                    if (earliestStartTimeOnThisProcessor == -1 && scheduleYouAreCurrentlyInspecting.processor == processorWeAreTryingToScheduleOn) {
                        earliestStartTimeOnThisProcessor = scheduleYouAreCurrentlyInspecting.time + taskGraph.getNodeCost(scheduleYouAreCurrentlyInspecting.task);

                    }

                    if (dependenciesRemaining != 0) {
                        if (dependencyList.contains(scheduleYouAreCurrentlyInspecting.task)) {
                            dependenciesRemaining--;
                            if (processorWeAreTryingToScheduleOn == scheduleYouAreCurrentlyInspecting.processor) {
                                earliestStartTimeOfThisTask = Math.max(scheduleYouAreCurrentlyInspecting.time + taskGraph.getNodeCost(scheduleYouAreCurrentlyInspecting.task), earliestStartTimeOfThisTask);

                            } else {
                                //add communication time
                                earliestStartTimeOfThisTask = Math.max(scheduleYouAreCurrentlyInspecting.time + taskGraph.getNodeCost(scheduleYouAreCurrentlyInspecting.task) + taskGraph.getEdgeCost(scheduleYouAreCurrentlyInspecting.task, taskYouAreTryingToSchedule), earliestStartTimeOfThisTask);

                            }
                        }
                    }
                    scheduleYouAreCurrentlyInspecting = scheduleYouAreCurrentlyInspecting.parent;
                }
                if(earliestStartTimeOnThisProcessor==-1){
                    earliestStartTimeOnThisProcessor=0;
                }
                int startTime = Math.max(earliestStartTimeOfThisTask, earliestStartTimeOnThisProcessor);
                int updatedIdleTime = idleTime + startTime - earliestStartTimeOnThisProcessor;

                //calculating estimate
                int updatedEstimate = Math.max((updatedIdleTime + taskGraph.getTotalTaskTime())/taskGraph.getNumProcessors(), taskGraph.getBottomLevel(taskYouAreTryingToSchedule) + startTime);
                //not sure if this line is needed, can check later
                int est = Math.max(updatedEstimate, estimate);

                //now calculate new doable task list
                List<Integer> updatedDoableTasks = new ArrayList<Integer>(doableTasks);
                //this line workd but might be slower so we use faster
                //updatedDoableTasks.remove(new Integer(taskYouAreTryingToSchedule));
                updatedDoableTasks.remove(Integer.valueOf(taskYouAreTryingToSchedule));

                for (int child:taskGraph.getChildren(taskYouAreTryingToSchedule)) {
                    boolean isDoable = true;
                    for (int dependency: taskGraph.getDependencies(child)) {
                        if (!completedTasks[dependency] && dependency != taskYouAreTryingToSchedule) {
                            isDoable = false;
                            break;
                        }
                    }
                    if (isDoable) {
                        updatedDoableTasks.add(child);
                    }
                }
                int updatedProccessorsUsed = processorsUsed;
                if (processorWeAreTryingToScheduleOn == processorsUsed) {
                    updatedProccessorsUsed ++;
                }

                children.add(new Schedule(this, taskYouAreTryingToSchedule,
                        startTime, processorWeAreTryingToScheduleOn,
                        updatedIdleTime, est, updatedProccessorsUsed, updatedDoableTasks));
            }
        }
    return children;
    }
}