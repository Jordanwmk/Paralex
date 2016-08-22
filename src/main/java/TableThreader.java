import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;


public class TableThreader extends SwingWorker<Void,Schedule> {

	private Schedule schedule;
	VFrame frame = VFrame.getInstance();
	@Override
	protected Void doInBackground() throws Exception {
		//frame.addToBestSchedule(schedule);
//		System.out.println("DOOD");
		publish(schedule);
		return null;
	}
	
	//change gui here
	@Override
	protected void process(List<Schedule> schedule){
		
		//gui.update(schedule);
	}
	
	@Override
	protected void done(){
		
	}

	public void setSchedule(Schedule schedule){
		this.schedule = schedule;
	}
}
