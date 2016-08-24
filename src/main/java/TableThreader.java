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

	Algorithm algorithm = null;
	VFrame frame;
	Timer simpleTimer = null;
	Schedule prev = null;

	public TableThreader(Algorithm algorithm, VFrame frame) {
		this.algorithm = algorithm;
		this.frame = frame;
		simpleTimer = new Timer(5, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				Schedule currentBest = algorithm.getCurrentBest();
//				if (prev == null && currentBest !=null) {
//					prev = currentBest;
//					System.out.println(prev);
//					frame.currentBestSchedule=currentBest;
//					publish(currentBest);
//					System.out.println("First Time publish");
//				}
//
//				else if (!(prev.equals(currentBest)) && currentBest !=null) {
//
//					// clear the table
//					Arrays.fill(frame.procFinishTimes, 0);
//					for (int i = 0; i < frame.totalProcessors; i++) {
//						JTable table = frame.procTables.get(i);
//						DefaultTableModel model = (DefaultTableModel) table
//								.getModel();
//						model.setRowCount(0);
//					}
//					System.out.println("New Node");
//					// set new currentBest
//					frame.currentBestSchedule = currentBest;
//
//					publish(currentBest);
//					System.out.println("publish more htne once");
//					prev = currentBest;
//				}
				
				Schedule currentBest = algorithm.getCurrentBest();
//				System.out.println(currentBest);
				if(currentBest!=null && (prev==null || !prev.equals(currentBest))){
					// clear the table
					Arrays.fill(frame.procFinishTimes, 0);
					for (int i = 0; i < frame.totalProcessors; i++) {
						JTable table = frame.procTables.get(i);
						DefaultTableModel model = (DefaultTableModel) table
								.getModel();
						model.setRowCount(0);
					}
					System.out.println("New Node");
					// set new currentBest
					frame.currentBestSchedule = currentBest;

					publish(currentBest);
					System.out.println("publish more htne once");
					prev = currentBest;
				}
			

			}
		});

	}

	// computational work
	@Override
	protected Void doInBackground() throws Exception {

		simpleTimer.start();
//		while (!(algorithm.isDone())){
//			
//		}

		return null;
	}

	// change gui here
	@Override
	protected void process(List<Schedule> schedules) {

		Schedule schedule = schedules.get(schedules.size() - 1);
		System.out.println("cool");
		if (schedule != null) {
			System.out.println("GOING INSIDE THE THING");

			int[] startTimes=new int[frame.graphStreamGraph.getNodeCount()];
			
			
			System.arraycopy(schedule.getTaskStartTimes(), 0, startTimes, 0, startTimes.length);
			
			
			for(int i=0;i<frame.graphStreamGraph.getNodeCount();i++){
				int earliestStartTime = Integer.MAX_VALUE;
				int earliestStartTimeIndex = -1;
				
				for(int j=0;j<frame.graphStreamGraph.getNodeCount();j++){
					if(startTimes[j]<earliestStartTime){
						earliestStartTime=startTimes[j];
						earliestStartTimeIndex=j;
					}
				}
				
				startTimes[earliestStartTimeIndex]=Integer.MAX_VALUE;
				
				int startTime = frame.currentBestSchedule.getTaskStartTimes()[earliestStartTimeIndex];
				int processor = frame.currentBestSchedule.getTaskProcessors()[earliestStartTimeIndex];
				int task = earliestStartTimeIndex;
				int[] nodeCostArray = frame.input.getNodeCosts();
				
				int nodeCost = nodeCostArray[task];
				frame.addTaskToProcessor(processor, task, nodeCost, startTime);
				
			}
			
		}

	}

	@Override
	protected void done() {
		System.out.println("DONE");
	}

}
