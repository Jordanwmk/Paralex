
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

/**
 * 
 * Created 07/08/16
 * 
 * @author Ammar Bagasrawala
 *
 */
public class VFrame {
	public static VFrame instance;
	private List<ArrayList<Integer>> listOfAccess;
	private JFrame mainFrame;

	private JTable table;
	public int totalProcessors = 0;
	public int[] procFinishTimes;
	public ArrayList<JTable> procTables = new ArrayList<JTable>();
	public Input input;
	public Graph taskGraph;
	private ArrayList<Integer> listOfTasks;

	private ArrayList<Graph> taskGraphList = new ArrayList<Graph>();

	private String currentHoveredCell = null;
	private String currentColour = null;

	public Schedule currentBestSchedule = null;
	private int scalingFactor = 2;
	private int numCores = 4;

	public VFrame() {}

	public VFrame(int numOfCores, String fileName, int processors) {
		instance = this;
		instance.setup(numCores, fileName, processors);
	}


	private void setup(int numOfCores, String fileName, int processors) {
		try {
			input = new Input(fileName);
			taskGraph = input.getInputG();
		} catch (IOException e) {
			e.printStackTrace();
		}

		numCores = numOfCores;
		totalProcessors = processors;
		setupScalingFactor();
		this.prepareGui();
		this.showFrame();

		int numNodes = taskGraph.getNodeCount();
		procFinishTimes = new int[numNodes];
		listOfTasks = new ArrayList<Integer>(Collections.nCopies(numNodes, 0));
		listOfAccess = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < numOfCores; i++){
			listOfAccess.add(i, listOfTasks);
		}

		taskGraph.addAttribute("ui.stylesheet", "url('src/main/java/graphStyleSheet.css'))");
		taskGraph.addAttribute("ui.quality");
		taskGraph.addAttribute("ui.antialias");

		for (Node node : taskGraph) {
			node.addAttribute("ui.label", node.getId());
		}       

	}

	public void printStuff() {
		for (int i = 0; i < listOfAccess.size(); i++) {
			for (int j = 0; j < listOfTasks.size(); j++) {
				System.out.print(listOfTasks.get(j) + " ");
			}
			System.out.println();
		}
	}

	public void incrementTask(int task) {

		ArrayList<Integer> currentCore = listOfAccess.get(0);

		Integer value = currentCore.get(task); // get value
		currentCore.set(task, value + 1);

//		if (currentCore.get(task) % 50 == 0) {
			setNodeColour(currentCore.get(task), task);
//		}

	}

	public static VFrame getInstance() {
		if (instance == null) {
			instance = new VFrame();
		}

		return instance;
	}

	private void prepareGui() {

		mainFrame = new JFrame("Paralex - Parallel Task Scheduling");
		mainFrame.setSize(1200,900);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		mainFrame.setMinimumSize(new Dimension (500, 500));

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel graphKey = new JPanel();		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.2;
		gbc.weighty = 0.2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		graphKey.setBorder(BorderFactory.createTitledBorder("Graph Key"));
		contentPanel.add(graphKey, gbc);

		graphKey.setLayout(new GridBagLayout());
		GridBagConstraints gbcKey = new GridBagConstraints();
		
		JPanel panel50 = new JPanel();
		panel50.setLayout(new BorderLayout());
		JPanel color50 = new JPanel();
		color50.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color50.setBackground(new Color(128,212,255));
		panel50.add(color50, BorderLayout.WEST);
		panel50.add(new JLabel("  0 - 50 visits"), BorderLayout.EAST);
		gbcKey.gridx = 0;
		gbcKey.gridy = 0;
		gbcKey.weightx = 0.2;
		gbcKey.weighty = 0.2;
		gbcKey.anchor = GridBagConstraints.WEST;
		graphKey.add(panel50, gbcKey);
		
		JPanel panel150 = new JPanel();
		panel150.setLayout(new BorderLayout());
		JPanel color150 = new JPanel();
		color150.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color150.setBackground(new Color(51,153,255));
		panel150.add(color150, BorderLayout.WEST);
		panel150.add(new JLabel("  50 - 150 visits"), BorderLayout.EAST);
		gbcKey.gridx = 0;
		gbcKey.gridy = 1;
		graphKey.add(panel150, gbcKey);
		
		JPanel panel500 = new JPanel();
		panel500.setLayout(new BorderLayout());
		JPanel color500 = new JPanel();
		color500.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color500.setBackground(new Color(0,102,255));
		panel500.add(color500, BorderLayout.WEST);
		panel500.add(new JLabel("  150 - 500 visits"), BorderLayout.EAST);
		gbcKey.gridx = 0;
		gbcKey.gridy = 2;
		graphKey.add(panel500, gbcKey);
		
		JPanel panel1000 = new JPanel();
		panel1000.setLayout(new BorderLayout());
		JPanel color1000 = new JPanel();
		color1000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color1000.setBackground(new Color(0,0,255));
		panel1000.add(color1000, BorderLayout.WEST);
		panel1000.add(new JLabel("  500 - 1000 visits"), BorderLayout.EAST);
		gbcKey.gridx = 1;
		gbcKey.gridy = 0;
		graphKey.add(panel1000, gbcKey);
		
		JPanel panel5000 = new JPanel();
		panel5000.setLayout(new BorderLayout());
		JPanel color5000 = new JPanel();
		color5000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color5000.setBackground(new Color(102,0,255));
		panel5000.add(color5000, BorderLayout.WEST);
		panel5000.add(new JLabel("  1000 - 5000 visits"), BorderLayout.EAST);
		gbcKey.gridx = 1;
		gbcKey.gridy = 1;
		graphKey.add(panel5000, gbcKey);
		
		JPanel panel10000 = new JPanel();
		panel10000.setLayout(new BorderLayout());
		JPanel color10000 = new JPanel();
		color10000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color10000.setBackground(new Color(153,51,255));
		panel10000.add(color10000, BorderLayout.WEST);
		panel10000.add(new JLabel("  5000 - 10000 visits"), BorderLayout.EAST);
		gbcKey.gridx = 1;
		gbcKey.gridy = 2;
		graphKey.add(panel10000, gbcKey);
		
		JPanel panel50000 = new JPanel();
		panel50000.setLayout(new BorderLayout());
		JPanel color50000 = new JPanel();
		color50000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color50000.setBackground(new Color(204,0,255));
		panel50000.add(color50000, BorderLayout.WEST);
		panel50000.add(new JLabel("  10000 - 50000 visits"), BorderLayout.EAST);
		gbcKey.gridx = 2;
		gbcKey.gridy = 0;
		graphKey.add(panel50000, gbcKey);
		
		JPanel panel300000 = new JPanel();
		panel300000.setLayout(new BorderLayout());
		JPanel color300000 = new JPanel();
		color300000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color300000.setBackground(new Color(255,0,102));
		panel300000.add(color300000, BorderLayout.WEST);
		panel300000.add(new JLabel("  50,000 - 300,000 visits"), BorderLayout.EAST);
		gbcKey.gridx = 2;
		gbcKey.gridy = 1;
		graphKey.add(panel300000, gbcKey);
		
		JPanel panel1000000 = new JPanel();
		panel1000000.setLayout(new BorderLayout());
		JPanel color1000000 = new JPanel();
		color1000000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color1000000.setBackground(new Color(255,0,0));
		panel1000000.add(color1000000, BorderLayout.WEST);
		panel1000000.add(new JLabel("  300,000 - 1,000,000 visits"), BorderLayout.EAST);
		gbcKey.gridx = 2;
		gbcKey.gridy = 2;
		graphKey.add(panel1000000, gbcKey);
		
		JPanel panel3000000 = new JPanel();
		panel3000000.setLayout(new BorderLayout());
		JPanel color3000000 = new JPanel();
		color3000000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color3000000.setBackground(new Color(128,0,0));
		panel3000000.add(color3000000, BorderLayout.WEST);
		panel3000000.add(new JLabel("  1,000,000 - 3,000,000 visits"), BorderLayout.EAST);
		gbcKey.gridx = 3;
		gbcKey.gridy = 0;
		graphKey.add(panel3000000, gbcKey);
				
		JPanel panel3000000plus = new JPanel();
		panel3000000plus.setLayout(new BorderLayout());
		JPanel color3000000plus = new JPanel();
		color3000000plus.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color3000000plus.setBackground(Color.black);
		panel3000000plus.add(color3000000plus, BorderLayout.WEST);
		panel3000000plus.add(new JLabel("  3,000,000 + visits"), BorderLayout.EAST);
		gbcKey.gridx = 3;
		gbcKey.gridy = 1;
		graphKey.add(panel3000000plus, gbcKey);
		
		JPanel statusPanel = new JPanel();		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
		contentPanel.add(statusPanel, gbc);

		JPanel outerGraphPanel = new JPanel();
		outerGraphPanel.setBackground(Color.black);
		setupGraphPanel(outerGraphPanel);

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
		processPanel.setLayout(new GridLayout(1, totalProcessors));


		for (int i = 0; i < totalProcessors; i++) {
			DefaultTableModel model = new DefaultTableModel(){
				@Override
				public Class<?> getColumnClass(int columnIndex){
					return String[].class;
				}
			};
			
			model.addColumn("P");
			JTable table = new JTable(model);

			table.setRowSelectionAllowed(false);
			table.setEnabled(false); // Disabling user edit
			table.addMouseMotionListener(new MouseMotionListener() {

				public void mouseMoved(java.awt.event.MouseEvent evt) {
					int row = table.rowAtPoint(evt.getPoint());
					int col = table.columnAtPoint(evt.getPoint());
					
					if (row != -1) {
						if (table.getValueAt(row, col) != null) {
							String value = (String) table.getValueAt(row, col);

							// Checks if nothing has been hovered over and you are
							// not hovering over
							// idle time
							if (currentHoveredCell == null && (!(value.equals("Idle Time")))) {
								currentColour = taskGraph.getNode(value).getAttribute("ui.class");
								taskGraph.getNode(value).setAttribute("ui.class","highlighted");
								currentHoveredCell = value;
	
								// Checks if you are hovering over Idle time and you
								// have already assigned something
							} else if (currentHoveredCell != null && (value.equals("Idle Time"))) {
								taskGraph.getNode(currentHoveredCell).setAttribute("ui.class", "" + currentColour);
								currentHoveredCell = null;
								currentColour = null;
							}
							if ((!(value.equals("Idle Time"))) && (!(currentHoveredCell.equals(value)))) {
								taskGraph.getNode(currentHoveredCell).setAttribute("ui.class", "" + currentColour);
								currentColour = taskGraph.getNode(value).getAttribute("ui.class");
								taskGraph.getNode(value).setAttribute("ui.class","highlighted");
								currentHoveredCell = value;
							}
						}
					}

				}

				@Override
				public void mouseDragged(MouseEvent e) {
					// TODO Auto-generated method stub

				}
			});
			table.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseExited(java.awt.event.MouseEvent evt) {
					if (currentHoveredCell != null) {
						taskGraph.getNode(currentHoveredCell).setAttribute(
								"ui.class", currentColour);
						currentHoveredCell = null;
						currentColour = null;
					}
				}
			});

//			JPanel tablePanel = new JPanel();
//			tablePanel.setLayout(new BorderLayout());
//			tablePanel.setBorder(BorderFactory.createTitledBorder(
//					BorderFactory.createEtchedBorder(), ("Proc " + (i + 1)),
//					TitledBorder.CENTER, TitledBorder.TOP));
//			tablePanel.add(table);
//			processPanel.add(tablePanel);

			table.setEnabled(false);	// Disabling user edit

			procTables.add(table);

//			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
//			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
			table.setDefaultRenderer(String[].class, new CustomTableRenderer());
			
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Processor "+ i, TitledBorder.CENTER, TitledBorder.TOP));
			panel.add(table);
			
			processPanel.add(panel);

		}

		JScrollPane scrollPane = new JScrollPane(processPanel);

		gbcProcess.gridx = 0;
		gbcProcess.gridy = 0;
		gbcProcess.weightx = 1.0;
		gbcProcess.weighty = 1.0;
		gbcProcess.fill = GridBagConstraints.BOTH;
		gbcProcess.anchor = GridBagConstraints.NORTH;
		gbcProcess.insets = new Insets(2,2,2,2);
		outerProcessPanel.add(scrollPane, gbcProcess);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.EAST;
		contentPanel.add(outerProcessPanel, gbc);

		mainFrame.add(contentPanel);
	}

	// Sets up the graph pane depending on the number of cores running the application
	private void setupGraphPanel(JPanel outerGraphPanel) {

		if (numCores == 4 || numCores == 3) {
			outerGraphPanel.setLayout(new GridLayout(2,2));
		} else if (numCores == 2) {
			outerGraphPanel.setLayout(new GridLayout(1,2));
		} else {
			outerGraphPanel.setLayout(new GridLayout(1,1));
		}

		for (int i = 0; i < numCores; i++) {
			JPanel graphPanel = new JPanel();
			graphPanel.setLayout(new BorderLayout());
			graphPanel.setPreferredSize(new Dimension(300, 350));
			Viewer viewer = new Viewer(taskGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			View view = viewer.addDefaultView(false);
			viewer.enableAutoLayout();
			graphPanel.add((Component) view);
			graphPanel.setBorder(BorderFactory.createTitledBorder("Task Graph For Core " + (i+1)));

			taskGraphList.add(taskGraph);
			outerGraphPanel.add(graphPanel);
		}		
	}


	private void setNodeColour(int activityNumber, int task) {
		if (activityNumber == 3000000) { // 3,000,000 +
			taskGraph.getNode(task).setAttribute("ui.class", "partition3000000");
			
		} else if (activityNumber == 1000000) { // 1,000,000 to 2,999,999
			taskGraph.getNode(task).setAttribute("ui.class", "partition1000000");
			
		} else if (activityNumber == 300000) { // 300,000 to 999,999
			taskGraph.getNode(task).setAttribute("ui.class", "partition300000");
			
		} else if (activityNumber == 50000) { // 50,000 to 299,999
			taskGraph.getNode(task).setAttribute("ui.class", "partition50000");
			
		} else if (activityNumber == 10000) { // 10,000 to 49,999
			taskGraph.getNode(task).setAttribute("ui.class", "partition10000");
			
		} else if (activityNumber == 5000) { // 5,000 to 9,999
			taskGraph.getNode(task).setAttribute("ui.class", "partition5000");
			
		} else if (activityNumber == 1000) { // 1,000 to 4,999 
			taskGraph.getNode(task).setAttribute("ui.class", "partition1000");
			
		} else if (activityNumber == 500) { // 500 to 999
			taskGraph.getNode(task).setAttribute("ui.class", "partition500");
			
		} else if (activityNumber == 150) { // 150 to 499
			taskGraph.getNode(task).setAttribute("ui.class", "partition150");
			
		} else if (activityNumber == 50) { // 50 to 149
			taskGraph.getNode(task).setAttribute("ui.class", "partition50");
		}
	}


	private void showFrame () {
		mainFrame.setVisible(true);
	}


	public void addToBestSchedule(Schedule currentBest) {
		Arrays.fill(procFinishTimes, 0);

		for (int i = 0; i < totalProcessors; i++) {
			JTable table = procTables.get(i);
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);
		}

		currentBestSchedule = currentBest;
//		while (currentBest.getTask() != -1) {
//			// System.out.println(currentBest.getTask());
//			currentBestScheduleList.add(currentBest);
//			currentBest = currentBest.getParent();
//		}

		for (int i = 0; i<taskGraph.getNodeCount() ; i++) {
			int startTime = currentBestSchedule.getTaskStartTimes()[i];
			int processor = currentBestSchedule.getTaskProcessors()[i];
			int task = i;
			int[] nodeCostArray = input.getNodeCosts();
//			if (task != -1) {
			int nodeCost = nodeCostArray[task];
			instance.addTaskToProcessor(processor, task, nodeCost,
					startTime);
//			}

		}
	}

	public void addTaskToProcessor(int proc, int task, int nodeCost, int startTime) {

		JTable table = procTables.get(proc);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int earliestStartOnProc = procFinishTimes[proc];
		int idleTime = startTime - earliestStartOnProc;

		String taskName = taskGraph.getNode(task).getId();

		if (idleTime > 0) {
			model.addRow(new String[] { "Idle Time" });
			;
			table.setRowHeight(table.getRowCount() - 1,
					(idleTime * scalingFactor));
		}


		model.addRow(new String[]{taskName});
		table.setRowHeight(table.getRowCount()-1, (nodeCost * scalingFactor));
		procFinishTimes[proc]=startTime+nodeCost;
	}


	// Setting up the factor to which the rows in the processor tables need to be resized to 
	public void setupScalingFactor() {
		int[] nodeCostArray = input.getNodeCosts();
		int ave = 0;

		for (int i = 0; i < nodeCostArray.length; i++) {
			ave += nodeCostArray[i];
		}

		ave = ave/nodeCostArray.length;

		if (ave <= 5) {
			scalingFactor = 15;
		} else if (ave <= 10) {
			scalingFactor = 10;
		} else if (ave <= 15) {
			scalingFactor = 5;
		} else if (ave <= 30){
			scalingFactor = 3;
		} else {
			scalingFactor = 2;
		}
	}

	public class CustomTableRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			setHorizontalAlignment(SwingConstants.CENTER);
			
			String valueAt = (String)table.getModel().getValueAt(row, column);
			
			if (valueAt.equals("Idle Time")){
				c.setBackground(Color.BLACK);
				c.setForeground(Color.BLACK);
			} else {
				c.setBackground(Color.WHITE);
			}
			
			return c;
		}

	}

}


