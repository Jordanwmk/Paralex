import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class TableThreader extends SwingWorker<Void, Schedule> {

	BranchAndBoundAlgorithm algorithm = null;
	VFrame frame;
	Timer simpleTimer = null;
	Schedule prev = null;

	public TableThreader(BranchAndBoundAlgorithm algorithm, VFrame frame) {
		this.algorithm = algorithm;
		this.frame = frame;
		System.out.println("GG123");
		simpleTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Schedule currentBest = algorithm.getCurrentBest();
				Schedule temp = currentBest;
				if (prev == null) {
					prev = temp;
					// while (currentBest.getTask() != -1) {
					// // System.out.println(currentBest.getTask());
					// frame.currentBestScheduleList.add(currentBest);
					// currentBest = currentBest.getParent();
					// }
					publish(frame.currentBestSchedule);

				}

				if (!(prev.equals(currentBest))) {

					// clear the table
					Arrays.fill(frame.procFinishTimes, 0);
					for (int i = 0; i < frame.totalProcessors; i++) {
						JTable table = frame.procTables.get(i);
						DefaultTableModel model = (DefaultTableModel) table
								.getModel();
						model.setRowCount(0);
					}
					System.out.println("GG123");
					// set new currentBest
					frame.currentBestSchedule = currentBest;
					// while (currentBest.getTask() != -1) {
					// // System.out.println(currentBest.getTask());
					// frame.currentBestScheduleList.add(currentBest);
					// currentBest = currentBest.getParent();
					// }

					publish(frame.currentBestSchedule);
					prev = temp;
				}

			}
		});

	}

	// computational work
	@Override
	protected Void doInBackground() throws Exception {

		simpleTimer.start();

		return null;
	}

	// change gui here
	@Override
	protected void process(List<Schedule> schedules) {

		Schedule schedule = schedules.get(schedules.size() - 1);
		System.out.println("cool");
		if (schedule != null) {

			for (int i = 0; i < frame.taskGraph.getNodeCount(); i++) {
				int startTime = frame.currentBestSchedule.getTaskStartTimes()[i];
				int processor = frame.currentBestSchedule.getTaskProcessors()[i];
				int task = i;
				int[] nodeCostArray = frame.input.getNodeCosts();
				// if (task != -1) {
				int nodeCost = nodeCostArray[task];
				frame.addTaskToProcessor(processor, task, nodeCost, startTime);
				// }

			}
		}

	}

	@Override
	protected void done() {
		System.out.println("DONE");
	}

}
