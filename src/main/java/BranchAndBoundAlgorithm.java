import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class BranchAndBoundAlgorithm implements Algorithm {
	
	Schedule currentBest = null;
	boolean done=false;
	public boolean isDone() {
		return done;
	}

	@Override
	public Schedule schedule(TaskGraph taskGraph) {
		ArrayDeque<Schedule> stack = new ArrayDeque<Schedule>();
		stack.push(Schedule.getEmptySchedule(taskGraph));
		int currentBestTime = Integer.MAX_VALUE;
		Schedule scheduleWeAreCurrentlyAt = null;


		/*
		 * Bens first schedule algorithm (schedule to most free processor int[]
		 * 
		 * processorStates = new int[taskGraph.getNumProcessors()]; int
		 * minProcessor =0; int minProcessorValue = Integer.MAX_VALUE;
		 * while(!stack.isEmpty()){ scheduleWeAreCurrentlyAt=stack.pop();
		 * List<Schedule>
		 * childNodes=scheduleWeAreCurrentlyAt.generateChildren();
		 * minProcessorValue = Integer.MAX_VALUE; for( int i =0; i<
		 * processorStates.length; i++){ if (processorStates[i] <
		 * minProcessorValue){ minProcessor = i; minProcessorValue =
		 * processorStates[i]; } }
		 * 
		 * if (!childNodes.isEmpty()){ stack.add(childNodes.get(minProcessor));
		 * int newTaskWeight =
		 * taskGraph.getNodeCost(childNodes.get(minProcessor).getTask());
		 * processorStates[minProcessor] += newTaskWeight; } } currentBestTime =
		 * scheduleWeAreCurrentlyAt.getTotalTime();
		 * currentBest=scheduleWeAreCurrentlyAt;
		 */
		
		int currentLimit = 0;
		stack = new ArrayDeque<>();
		stack.push(Schedule.getEmptySchedule(taskGraph));
		scheduleWeAreCurrentlyAt = null;
		while (!stack.isEmpty()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			scheduleWeAreCurrentlyAt = stack.pop();
			List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();

			if (childNodes.isEmpty()) {
				break;
			}

			stack.push(childNodes.get(currentLimit % taskGraph.getNumProcessors()));
			currentLimit++;
		}

		currentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();
		this.currentBest = scheduleWeAreCurrentlyAt;

		// actual algorithm
		stack = new ArrayDeque<>();
		stack.push(Schedule.getEmptySchedule(taskGraph));
		scheduleWeAreCurrentlyAt = null;
		while (!stack.isEmpty()) {
			scheduleWeAreCurrentlyAt = stack.pop();
			
			//visuallisation
			VFrame frame = VFrame.getInstance();
            if (scheduleWeAreCurrentlyAt.getTask() != -1){
            	frame.incrementTask(scheduleWeAreCurrentlyAt.getTask());
            }
			
			// if estimate >= current best, then prune the subtree (don't
			// traverse it)
			if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < currentBestTime) {
				List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();

				// checking if schedule is bad before adding it to the stack
				// original line of code
				// stack.addAll(childNodes);

				for (Schedule s : childNodes) {
					if (s.getEstimate() < currentBestTime) {
						stack.push(s);
					}
				}

				if (childNodes.isEmpty()) {
					if (scheduleWeAreCurrentlyAt.getTotalTime() < currentBestTime) {
						this.currentBest = scheduleWeAreCurrentlyAt;
						currentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();
					}
				}
			}
		}
		done=true;
		return this.currentBest;
	}
	
	public Schedule getCurrentBest(){
    	return this.currentBest;
    }
}

