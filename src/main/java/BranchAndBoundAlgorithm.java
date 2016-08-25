import java.util.ArrayDeque;
import java.util.List;

public class BranchAndBoundAlgorithm implements Algorithm {
	
	Schedule currentBest = null;

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
		return this.currentBest;
	}
	
	public Schedule getCurrentBest(){
    	return this.currentBest;
    }

	@Override
	public boolean isDone() {
		return false;
	}
}

