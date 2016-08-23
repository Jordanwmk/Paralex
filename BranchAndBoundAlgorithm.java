import java.util.List;
import java.util.Stack;

public class BranchAndBoundAlgorithm implements Algorithm{
    @Override
    public Schedule schedule(Graph taskGraph) {
        Stack<Schedule> stack=new Stack<>();
        stack.add(Schedule.getEmptySchedule(taskGraph));
        Schedule currentBest=null;
        int currentBestTime=Integer.MAX_VALUE;
        Schedule scheduleWeAreCurrentlyAt=null;

        int currentLimit = 0;

        while (!stack.isEmpty()){
            scheduleWeAreCurrentlyAt = stack.pop();
            List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();

//            if (taskGraph.getNumProcessors() < currentLimit + 1 || childNodes.size() < currentLimit + 1){
//                currentLimit = 0;
//            }

            if (childNodes.isEmpty()){
                break;
            }

            stack.add(childNodes.get(currentLimit % taskGraph.getNumProcessors()));
            currentLimit++;
        }

        currentBest=scheduleWeAreCurrentlyAt;
        currentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();
        stack.add(Schedule.getEmptySchedule(taskGraph));

        while(!stack.isEmpty()){
            scheduleWeAreCurrentlyAt=stack.pop();
            //if estimate >= current best, then prune the subtree (don't traverse it)
            if(scheduleWeAreCurrentlyAt.getFinishTimeEstimate()<currentBestTime){
                List<Schedule> childNodes=scheduleWeAreCurrentlyAt.generateChildren();
                stack.addAll(childNodes);
                if(childNodes.isEmpty()){
                    if(scheduleWeAreCurrentlyAt.getTotalTime()<currentBestTime){
                        currentBest=scheduleWeAreCurrentlyAt;
                        currentBestTime=scheduleWeAreCurrentlyAt.getTotalTime();
                    }
                }
            }
        }
        return currentBest;
    }
}
