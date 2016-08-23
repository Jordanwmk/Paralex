

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;


/**
 * 
 * Created 07/08/16
 * @author Ammar Bagasrawala
 *
 */
public class VFrame{
	private static VFrame instance;
	private List<ArrayList<Integer>> listOfAccess;
	private JFrame mainFrame;
	private JTable table;
	private int totalProcessors = 0;
	private int[] procFinishTimes;
	private ArrayList<JTable> procTables = new ArrayList<JTable>();
	private Input input;
	private Graph taskGraph;
	private ArrayList<Integer> listOfTasks;
	private ArrayList<Schedule> currentBestScheduleList = new ArrayList<Schedule>();
	private int scalingFactor = 2;
	
	public VFrame() {}
	
	public VFrame(int numOfCores, String fileName, int processors){
		instance = this;
		instance.setup(numOfCores, fileName, processors);
	}
	
	private void setup(int numOfCores, String fileName,  int processors) {
		try {
			input = new Input(fileName) ;
			taskGraph = input.getInputG();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        totalProcessors = processors;
        setupScalingFactor();
		this.prepareGui();
		this.showFrame();
		int numNodes = taskGraph.getNodeCount();
		procFinishTimes=new int[numNodes];
		listOfTasks = new ArrayList<Integer>(Collections.nCopies(numNodes, 0));
		listOfAccess = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < numOfCores; i++){
			listOfAccess.add(i, listOfTasks);
		}
		taskGraph.addAttribute("ui.stylesheet", "url('src/main/java/graphStyleSheet.css'))");
		//taskGraph.getNode(0).setAttribute("ui.color", 0.2);
		//taskGraph.getNode(1).setAttribute("ui.color", 1);
		
        for (Node node : taskGraph) {
        	node.addAttribute("ui.label", node.getId());
        	//node.addAttribute("ui.style", "fill-color: #80d4ff;");
        }
//        for (Edge edge: taskGraph.getEachEdge()){
//        	edge.addAttribute("ui.style", "fill-color: black;");
//        }
//        

	}

	public void printStuff(){
		for (int i=0 ; i< listOfAccess.size(); i++){
			for (int j=0; j<listOfTasks.size();j++){
				System.out.print(listOfTasks.get(j) + " ");
			}
			System.out.println();
		}
	}
	public void incrementTask(int task) {

		ArrayList<Integer> currentCore = listOfAccess.get(0);

		Integer value = currentCore.get(task); // get value
		currentCore.set(task, value+1);
		setNodeColour(currentCore.get(task),task);
		
	}
	
	public static VFrame getInstance() {
		if (instance == null) {
			instance = new VFrame();
		} 

		return instance;
	}


	private void prepareGui () {

		mainFrame = new JFrame("Paralex - Parallel Task Scheduling");
		mainFrame.setSize(1000,700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		mainFrame.setMinimumSize(new Dimension (500, 500));
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JPanel graphKey = new JPanel();		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		graphKey.setBorder(BorderFactory.createTitledBorder("Graph Key"));
		contentPanel.add(graphKey, gbc);
		
		JPanel statusPanel = new JPanel();		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
		contentPanel.add(statusPanel, gbc);
				
		JPanel outerGraphPanel = new JPanel();
		JPanel graphPanel = new JPanel();
		
		outerGraphPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbcGraph = new GridBagConstraints();
		
		gbcGraph.gridx = 0;
		gbcGraph.gridy = 0;
		gbcGraph.weightx = 1.0;
		gbcGraph.weighty = 1.0;
		gbcGraph.fill = GridBagConstraints.BOTH;
		gbcGraph.anchor = GridBagConstraints.NORTH;
		outerGraphPanel.setBorder(BorderFactory.createTitledBorder("Task Graph"));
				
		graphPanel.setLayout(new BorderLayout());
		graphPanel.setPreferredSize(new Dimension(300, 350));
		Viewer viewer = new Viewer(taskGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);
		viewer.enableAutoLayout();
		graphPanel.add((Component) view);	
		
		gbcGraph.gridx = 0;
		gbcGraph.gridy = 1;
		gbcGraph.weightx = 1.0;
		gbcGraph.weighty = 1.0;
		gbcGraph.anchor = GridBagConstraints.CENTER;
		outerGraphPanel.add(graphPanel, gbcGraph);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		contentPanel.add(outerGraphPanel, gbc);
		
		JPanel outerProcessPanel = new JPanel();
		outerProcessPanel.setLayout(new GridBagLayout());
		outerProcessPanel.setBorder(BorderFactory.createTitledBorder("Current Best Schedule"));
		GridBagConstraints gbcProcess = new GridBagConstraints();
		
		
		JPanel processPanel = new JPanel();
		processPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbcProcessPanel = new GridBagConstraints();
		
		gbcProcessPanel.gridx = 0;
		gbcProcessPanel.gridy = 0;
		gbcProcessPanel.weightx = 1.0;
		gbcProcessPanel.weighty = 1.0;
		gbcProcessPanel.fill = GridBagConstraints.BOTH;
				
		for (int i = 0; i < totalProcessors; i++) {
			DefaultTableModel model = new DefaultTableModel();
			model.addColumn("P");
			JTable table = new JTable(model);

			table.setRowSelectionAllowed(false);
			table.setEnabled(false);	// Disabling user edit

			procTables.add(table);

			gbcProcessPanel.gridx = i;
			gbcProcessPanel.gridy = 0;
			gbcProcessPanel.weightx = 1.0;
			gbcProcessPanel.weighty = 1.0;
			gbcProcessPanel.fill = GridBagConstraints.BOTH;
			gbcProcessPanel.insets = new Insets(0,5,0,5);
			
			processPanel.add(new JLabel("Processor " + i, SwingConstants.CENTER), gbcProcessPanel);
			gbcProcessPanel.gridy = 1;
			processPanel.add(table, gbcProcessPanel);
		}
		
		JScrollPane scrollPane = new JScrollPane(processPanel);
		
		gbcProcess.gridx = 0;
		gbcProcess.gridy = 0;
		gbcProcess.weightx = 1.0;
		gbcProcess.weighty = 1.0;
		gbcProcess.fill = GridBagConstraints.BOTH;
		outerProcessPanel.add(scrollPane, gbcProcess);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.EAST;
		contentPanel.add(outerProcessPanel, gbc);
		
		mainFrame.add(contentPanel);
	}

	private void setNodeColour(int activityNumber, int task){
		if (activityNumber == 3000000){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition3000000");

		}else if (activityNumber == 1000000){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition1000000");
		}else if (activityNumber == 300000){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition300000");		
			
		}else if (activityNumber == 50000){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition50000");
		}else if (activityNumber == 10000){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition10000");
		}else if (activityNumber == 5000){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition5000");
		}else if (activityNumber == 1000){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition1000");
		}else if (activityNumber == 500){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition500");
		}else if (activityNumber == 150){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition150");
		}else if (activityNumber == 50){
			//taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition50");
		}
    }
			
	
	private void showFrame () {
		mainFrame.setVisible(true);
	}


	public void addToBestSchedule(Schedule currentBest) {
		currentBestScheduleList.clear();
		Arrays.fill(procFinishTimes, 0);
		
		for (int i = 0; i < totalProcessors; i++) {
			JTable table = procTables.get(i);
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);
		}
		
		while (currentBest.getTask() != -1) {
			//System.out.println(currentBest.getTask());
			currentBestScheduleList.add(currentBest);
			currentBest=currentBest.getParent();
		} 
			
		for (int i = currentBestScheduleList.size()-1; i >= 0; i--) {
			Schedule schedule = currentBestScheduleList.get(i);
			int startTime = schedule.getTime();
			int processor = schedule.getProcessor();
			int task = schedule.getTask();
			int[] nodeCostArray = input.getNodeCosts();
			if (task != -1) {
				int nodeCost = nodeCostArray[task];
				instance.addTaskToProcessor(processor, task, nodeCost, startTime);
			}
					
		}
	}
	
	public void addTaskToProcessor(int proc, int task, int nodeCost, int startTime) {
		JTable table = procTables.get(proc);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int earliestStartOnProc = procFinishTimes[proc];
		int idleTime = startTime - earliestStartOnProc;
		
		if (idleTime > 0) {
			model.addRow(new String[]{"Idle Time"});;
			table.setRowHeight(table.getRowCount()-1, (idleTime * scalingFactor));
		}
		
		model.addRow(new String[]{"" + task});
		table.setRowHeight(table.getRowCount()-1, (nodeCost * scalingFactor));
		procFinishTimes[proc]=startTime+nodeCost;
	}

	
	// Setting up the factor to which the rows in the processor tables need to be resized to 
	public void setupScalingFactor() {
		int[] nodeCostArray = input.getNodeCosts();
		int min = 9999999;
		
		for (int i = 0; i < nodeCostArray.length; i++) {
			if (nodeCostArray[i] < min) {
				min = nodeCostArray[i];
			}
		}
		
		if (min == 1) {
			scalingFactor = 15;
		} else if (min == 2) {
			scalingFactor = 7;
		} else if (min < 4) {
			scalingFactor = 5;
		} else if (min < 10) {
			scalingFactor = 3;
		} else {
			scalingFactor = 2;
		}
	}
}
