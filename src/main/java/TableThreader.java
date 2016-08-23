import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;


public class TableThreader extends SwingWorker<Void,List<Schedule>> {

	private Schedule schedule;
	VFrame frame = VFrame.getInstance();
	private TaskGraph taskGraph = null;
	private Schedule currentBest = null;
	private String outputName = null;
	
	public TableThreader(TaskGraph taskGraph, String outputName){
		this.taskGraph = taskGraph;
		this.outputName = outputName;
		
	}
	
	//computational work
	@Override
	protected Void doInBackground() throws Exception {
		Stack<Schedule> stack=new Stack<>();
        stack.add(Schedule.getEmptySchedule(taskGraph));
        Schedule currentBest=null;
        int currentBestTime=Integer.MAX_VALUE;
        Schedule scheduleWeAreCurrentlyAt=null;
     
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
                        currentBestTime=scheduleWeAreCurrentlyAt.getTotalTime();
                        
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
                		Collections.reverse(frame.currentBestScheduleList);
                		publish(frame.currentBestScheduleList);
                		
                		
                    }
                }
            }
        }
        this.currentBest = currentBest;
        
        return null;
	}
	
	//change gui here
	@Override
	protected void process(List<List<Schedule>> schedules){
		
		List<Schedule> schedule = schedules.get(schedules.size()-1);
		
		for (int i = 0; i < schedule.size(); i++) {

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
		new Output().createOutput(this.currentBest,this.outputName);
	}

	public void setSchedule(Schedule schedule){
		this.schedule = schedule;
	}
	
	public void addToBestSchedule(Schedule currentBest) {
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
			
		for (int i = frame.currentBestScheduleList.size()-1; i >= 0; i--) {
			Schedule schedule = frame.currentBestScheduleList.get(i);
			int startTime = schedule.getTime();
			int processor = schedule.getProcessor();
			int task = schedule.getTask();
			int[] nodeCostArray = frame.input.getNodeCosts();
			if (task != -1) {
				int nodeCost = nodeCostArray[task];
				
			}
					
		}
	}
	
	
}
