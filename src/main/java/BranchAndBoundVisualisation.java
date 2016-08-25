import java.util.ArrayDeque;
import java.util.List;
/**
 * This class contains the implementation for visualising the branch and bound 
 * algorithm in serial.
 * @author jwon223
 *
 */
public class BranchAndBoundVisualisation implements Algorithm {

	//Variable to hold the current best schedule that will be painted on the GUI
	Schedule currentBest = null;
	
	//Variable to hold when the algorithm is done
	boolean done = false;
	
	boolean useVisualisation;

	public BranchAndBoundVisualisation(){
		this(false);
	}

	public BranchAndBoundVisualisation(boolean visualise){
		this.useVisualisation=visualise;
	}

	
	@Override
	public Schedule schedule(TaskGraph taskGraph) {
		ArrayDeque<Schedule> stack = new ArrayDeque<Schedule>();
		stack.push(Schedule.getEmptySchedule(taskGraph));
		int currentBestTime = Integer.MAX_VALUE;
		Schedule scheduleWeAreCurrentlyAt = null;

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

			if(useVisualisation){
				//visualisation
				VFrame frame = VFrame.getInstance();
				if (scheduleWeAreCurrentlyAt.getTask() != -1){
					
					//Increment the number of times this task has been looked at
					frame.incrementTask(scheduleWeAreCurrentlyAt.getTask(), 0);
				}
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
	
	public boolean isDone() {
		return done;
	}

}

