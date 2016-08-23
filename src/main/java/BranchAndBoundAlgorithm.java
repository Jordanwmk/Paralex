import java.util.List;
import java.util.Stack;

public class BranchAndBoundAlgorithm implements Algorithm{
    @Override
    public Schedule schedule(TaskGraph taskGraph) {
        Stack<Schedule> stack=new Stack<>();
        stack.add(Schedule.getEmptySchedule(taskGraph));
        Schedule currentBest=null;
        int currentBestTime=Integer.MAX_VALUE;
        Schedule scheduleWeAreCurrentlyAt=null;
        int i=0;
        TableThreader tableThreader = new TableThreader();
        
        while(!stack.isEmpty()){
            scheduleWeAreCurrentlyAt=stack.pop();
            VFrame frame = VFrame.getInstance();
            if (scheduleWeAreCurrentlyAt.getTask() != -1){
            	frame.incrementTask(scheduleWeAreCurrentlyAt.getTask());
            }
            //if estimate >= current best, then prune the subtree (don't traverse it)
            if(scheduleWeAreCurrentlyAt.getFinishTimeEstimate()<currentBestTime){
                List<Schedule> childNodes=scheduleWeAreCurrentlyAt.generateChildren();
                stack.addAll(childNodes);
                if(childNodes.isEmpty()){
                    if(scheduleWeAreCurrentlyAt.getTotalTime()<currentBestTime){
                        currentBest=scheduleWeAreCurrentlyAt;
                		frame.addToBestSchedule(currentBest);
                        currentBestTime=scheduleWeAreCurrentlyAt.getTotalTime();
                        tableThreader.setSchedule(currentBest);
                        tableThreader.execute();
                    }
                }
            }
        }
        return currentBest;
    }
}

