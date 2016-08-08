import java.util.List;
import java.util.Stack;

public class BruteForceAlgorithm implements Algorithm {
    @Override
    public Schedule schedule(Graph taskGraph) {
        Stack<Schedule> stack=new Stack<>();
        stack.add(Schedule.getEmptySchedule());
        Schedule currentBest=null;
        int currentBestTime=Integer.MAX_VALUE;
        Schedule scheduleWeAreCurrentlyAt=null;
        while(!stack.isEmpty()){
            scheduleWeAreCurrentlyAt=stack.pop();
            List<Schedule> childNodes=scheduleWeAreCurrentlyAt.generateChildren();
            stack.addAll(childNodes);
            if(childNodes.isEmpty()){
                if(scheduleWeAreCurrentlyAt.getTotalTime()<currentBestTime){
                    currentBestTime=scheduleWeAreCurrentlyAt.getTotalTime();
                    currentBest=scheduleWeAreCurrentlyAt;
                }
            }
        }
        return currentBest;
    }
}
