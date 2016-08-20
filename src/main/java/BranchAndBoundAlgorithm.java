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
        
        
        int[] processorStates = new int[taskGraph.getNumProcessors()];
        int minProcessor =0;
        int minProcessorValue = Integer.MAX_VALUE;
        while(!stack.isEmpty()){
        	scheduleWeAreCurrentlyAt=stack.pop();
        	List<Schedule> childNodes=scheduleWeAreCurrentlyAt.generateChildren();
        	minProcessorValue = Integer.MAX_VALUE;
        	for( int i =0; i< processorStates.length; i++){
        		if (processorStates[i] < minProcessorValue){
        			minProcessor = i;
        			minProcessorValue = processorStates[i];
        		}
        	}
        	
        	if (!childNodes.isEmpty()){
            stack.add(childNodes.get(minProcessor));
            int newTaskWeight = taskGraph.getNodeCost(childNodes.get(minProcessor).getTask());
            processorStates[minProcessor] += newTaskWeight;
        	}
        }
        currentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();
        currentBest=scheduleWeAreCurrentlyAt;
        
        int currentLimit = 0;
        stack=new Stack<>();
        stack.add(Schedule.getEmptySchedule(taskGraph));
        scheduleWeAreCurrentlyAt=null; 
        while (!stack.isEmpty()){
            scheduleWeAreCurrentlyAt = stack.pop();
            List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();

            if (childNodes.isEmpty()){
                break;
            }

            stack.add(childNodes.get(currentLimit % taskGraph.getNumProcessors()));
            currentLimit++;
        }

       // System.out.println(currentBestTime);
      //  System.out.println(scheduleWeAreCurrentlyAt.getTotalTime());
        
        if(scheduleWeAreCurrentlyAt.getTotalTime() < currentBestTime){
        	currentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();
        	currentBest = scheduleWeAreCurrentlyAt;
        }
      //  System.out.println(currentBestTime);
      //  System.out.println(scheduleWeAreCurrentlyAt.getTotalTime());

        
       //actual algorithm 
        stack=new Stack<>();
        stack.add(Schedule.getEmptySchedule(taskGraph));
        scheduleWeAreCurrentlyAt=null;
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
