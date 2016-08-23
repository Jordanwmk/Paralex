import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class BranchAndBoundAlgorithm implements Algorithm{
    @Override
    public Schedule schedule(Graph taskGraph) {
        ArrayDeque<Schedule> stack=new ArrayDeque<Schedule>();
        stack.add(Schedule.getEmptySchedule(taskGraph));
        Schedule currentBest=null;
        int currentBestTime=Integer.MAX_VALUE;
        Schedule scheduleWeAreCurrentlyAt=null;
        while(!stack.isEmpty()){
            scheduleWeAreCurrentlyAt=stack.pop();
            //if estimate >= current best, then prune the subtree (don't traverse it)
            if(scheduleWeAreCurrentlyAt.getFinishTimeEstimate()<currentBestTime){
                List<Schedule> childNodes=scheduleWeAreCurrentlyAt.generateChildren();
                
                //checking if schedule is bad before adding it to the stack
                //original line of code
//                stack.addAll(childNodes);
                
                for(Schedule s:childNodes) {
                	if (s.getEstimate()<currentBestTime) {
                		stack.push(s);
                	}
                }
                
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
