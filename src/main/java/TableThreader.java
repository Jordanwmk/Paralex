import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;


public class TableThreader extends SwingWorker<Void,Void> {

	private Schedule schedule;
	VFrame frame = VFrame.getInstance();
	@Override
	protected Void doInBackground() throws Exception {
		frame.addToBestSchedule(schedule);
		
		publish(schedule);
		return null;
	}
	
	//change gui here
	protected void publish(Schedule schedule){
		//gui.update(schedule);
	}
	
	@Override
	
	protected void done(){
		
	}

	public void setSchedule(Schedule schedule){
		this.schedule = schedule;
	}
}
