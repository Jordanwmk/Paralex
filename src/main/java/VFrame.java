
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
	private Graph taskGraph;
	private ArrayList<Integer> listOfTasks;

	private ArrayList<Graph> taskGraphList = new ArrayList<Graph>();

	private String currentHoveredCell = null;
	private String currentColour = null;

	public ArrayList<Schedule> currentBestScheduleList = new ArrayList<Schedule>();
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

		if (currentCore.get(task) % 50 == 0) {
			setNodeColour(currentCore.get(task), task);
		}

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
		gbc.weightx = 0.8;
		gbc.weighty = 0.8;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		graphKey.setBorder(BorderFactory.createTitledBorder("Graph Key"));
		contentPanel.add(graphKey, gbc);

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
					return String.class;
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

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
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
		if (activityNumber >= 3000000) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task)
					.setAttribute("ui.class", "partition3000000");

		} else if (activityNumber >= 1000000) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task)
					.setAttribute("ui.class", "partition1000000");
		} else if (activityNumber >= 300000) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition300000");
		} else if (activityNumber >= 50000) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition50000");
		} else if (activityNumber >= 10000) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition10000");
		} else if (activityNumber >= 5000) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition5000");
		} else if (activityNumber >= 1000) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition1000");
		} else if (activityNumber >= 500) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition500");
		} else if (activityNumber >= 150) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
			taskGraph.getNode(task).setAttribute("ui.class", "partition150");
		} else if (activityNumber >= 50) {
			// taskGraph.getNode(task).removeAttribute("ui.class");
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
			// System.out.println(currentBest.getTask());
			currentBestScheduleList.add(currentBest);
			currentBest = currentBest.getParent();
		}

		for (int i = currentBestScheduleList.size() - 1; i >= 0; i--) {
			Schedule schedule = currentBestScheduleList.get(i);
			int startTime = schedule.getTime();
			int processor = schedule.getProcessor();
			int task = schedule.getTask();
			int[] nodeCostArray = input.getNodeCosts();
			if (task != -1) {
				int nodeCost = nodeCostArray[task];
				instance.addTaskToProcessor(processor, task, nodeCost,
						startTime);
			}

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
			String valueAt = (String)table.getModel().getValueAt(row, column);
			System.out.println(valueAt);
			if (valueAt.equals("Idle Time")){
				c.setBackground(Color.BLACK);
			}
			
			return c;
		}

	}
}


