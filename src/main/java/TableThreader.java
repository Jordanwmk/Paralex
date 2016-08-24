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
	long startTime;
	
	public TableThreader(Algorithm algorithm, VFrame frame, long startTime) {
		this.algorithm = algorithm;
		this.frame = frame;
		simpleTimer = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			
				Schedule currentBest = algorithm.getCurrentBest();
				try {
					String cpuUsage = "CPU Usage: " + SystemQuery.getProcessCpuLoad() + " %";
					String memUsage = "Memory Usage:  " + SystemQuery.getProcessMemLoad() + " MB";
					frame.getCpuLabel().setText(cpuUsage);
					frame.getMemoryLabel().setText(memUsage);

					if (!algorithm.isDone()) {
						long currentTime = (System.currentTimeMillis() - startTime);
						frame.getElapsedTimeLabel().setText("Elapsed Time: " + currentTime + " ms");
					} else {
						frame.getRunningLabel().setText("Finished");
						frame.resetSize();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};

				if(currentBest!=null && (prev==null || !prev.equals(currentBest))){
					int totalTime = currentBest.getTotalTime();
					frame.getTotalScheduleTimeLabel().setText("Total Schedule Time: " + totalTime);
					
					// clear the table
					Arrays.fill(frame.procFinishTimes, 0);
					for (int i = 0; i < frame.totalProcessors; i++) {
						JTable table = frame.procTables.get(i);
						DefaultTableModel model = (DefaultTableModel) table
								.getModel();
						model.setRowCount(0);
					}

					// set new currentBest
					frame.currentBestSchedule = currentBest;

					publish(currentBest);
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
		if (schedule != null) {

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
	}

}
