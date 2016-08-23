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


public class TableThreader extends SwingWorker<Void,List<Schedule>> {
	
	BranchAndBoundAlgorithm algorithm = null;
	VFrame frame;
	Timer simpleTimer = null;
	
	public TableThreader(BranchAndBoundAlgorithm algorithm, VFrame frame){
		this.algorithm = algorithm;
		this.frame = frame;
		
		simpleTimer = new Timer(1000, new ActionListener(){
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Schedule currentBest = algorithm.getCurrentBest();  	
		    	
		    	frame.currentBestScheduleList.clear();
        		Arrays.fill(frame.procFinishTimes, 0);
        		
        		for (int i = 0; i < frame.totalProcessors; i++) {
        			JTable table = frame.procTables.get(i);
        			DefaultTableModel model = (DefaultTableModel) table.getModel();
        			model.setRowCount(0);
        		}
        		
        		while (currentBest.getTask() != -1) {
        			//System.out.println(currentBest.getTask());
        			frame.currentBestScheduleList.add(currentBest);
        			currentBest=currentBest.getParent();
        		} 

        		
        		publish(frame.currentBestScheduleList);
        		
		    }
		});

	}
	
	//computational work
	@Override
	protected Void doInBackground() throws Exception {

		simpleTimer.start();
        
        return null;
	}
	
	//change gui here
	@Override
	protected void process(List<List<Schedule>> schedules){

		List<Schedule> schedule = schedules.get(schedules.size()-1);

			for (int i = schedule.size() - 1; i > 0; i--) {

				int startTime = schedule.get(i).getTime();
				int processor = schedule.get(i).getProcessor();
				int task = schedule.get(i).getTask();
				int[] nodeCostArray = frame.input.getNodeCosts();
				if (task != -1) {
					int nodeCost = nodeCostArray[task];
					frame.addTaskToProcessor(processor, task, nodeCost, startTime);
				}
		
			}
			
	}
	
	@Override
	protected void done(){
		System.out.println("DONE");
	}
	
	
}
