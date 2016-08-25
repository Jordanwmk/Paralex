import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used in order to visualise how the program functions.
 * The frame created contains four major components - input graphs, graph key, status of the program
 * and the current best schedule. The input graphs and key are used to present information based on the number
 * of times each core has tried to schedule a certain task. The 'hotter' (more red) the colour of the node it
 * means it has been tried to schedule more. The current best schedule informs the best schedule produced at that time.
 * 
 * Created 07/08/16
 * 
 * @author Paralex Team - Jordan Wong, Jun Xu, Hanzhi Wang, Ben Mitchell, Kevin Yu, Ammar Bagasrawala
 *
 */
public class VFrame{
	public static VFrame instance;
	private List<ArrayList<Integer>> listOfCoreNodeFrequencies;
	private JFrame mainFrame;
	private JTable table;
	public int totalProcessors = 0;
	public int[] procFinishTimes;
	public ArrayList<JTable> procTables = new ArrayList<JTable>();
	public Input input;
	public Graph graphStreamGraph;
	private ArrayList<Integer> listOfTasks;
	private ArrayList<Graph> taskGraphList = new ArrayList<Graph>();
	private String currentHoveredCell = null;
	private String[] currentColour = null;
	public Schedule currentBestSchedule = null;
	private int scalingFactor = 2;
	private int numCores = 4;
	private JLabel runningLabel;
	private JLabel cpuLabel;
	private JLabel memoryLabel;
	private JLabel totalScheduleTimeLabel;
	private JLabel elapsedTimeLabel;

	public VFrame() {}

	public VFrame(int numOfCores, String fileName, int processors) {
		instance = this;
		instance.setup(numOfCores, fileName, processors);
	}


	/**
	 * Setting up VFrame so only 1 instance is ever created (Singleton)
	 * @return
	 */
	public static VFrame getInstance() {
		if (instance == null) {
			instance = new VFrame();
		}
		return instance;
	}

	/**
	 * Method which sets up values needed by this class
	 * Also sets up the GUI frame and adds style attributes to the input graph
	 **/
	private void setup(int numOfCores, String fileName, int processors) {

		numCores = numOfCores;
		currentColour = new String[numCores];
		totalProcessors = processors;

		for (int a=0; a <= numOfCores; a++) {
			try {
				input = new Input(fileName);
				graphStreamGraph = input.getInputG();
				taskGraphList.add(graphStreamGraph);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Setting up GUI
		setupScalingFactor();
		this.prepareGui();
		mainFrame.setVisible(true);
		
		// Setting up the lists used to make the processor tables
		int numNodes = graphStreamGraph.getNodeCount();
		procFinishTimes = new int[numNodes];
		listOfTasks = new ArrayList<Integer>(Collections.nCopies(numNodes, 0));
		listOfCoreNodeFrequencies = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < numOfCores; i++) {
			listOfCoreNodeFrequencies.add(i, listOfTasks);
		}

		// Adding a style sheet to the input graph
		for (int i = 0; i < numOfCores; i++) {
			taskGraphList.get(i).addAttribute("ui.stylesheet", "url('src/main/java/graphStyleSheet.css'))");
			taskGraphList.get(i).addAttribute("ui.quality");
			taskGraphList.get(i).addAttribute("ui.antialias");
		}

		// Adding labels to the nodes in the input graph
		for (int i = 0; i < numOfCores; i++) {
			for (Node node : taskGraphList.get(i)) {
				node.addAttribute("ui.label", node.getId());
			}
		}
	}

	/**
	 * Resets the size of the nodes once they are not being traversed
	 */
	public void resetSize(){
		for (int i = 0 ; i<this.numCores; i++){
			for (int j=0; j< taskGraphList.get(i).getNodeCount();j++){
				taskGraphList.get(i).getNode(j).setAttribute("ui.style","size:30px;");
			}
		}
	}

	/**
	 * Increments the activity level of a node being traversed and calls setNodeColour() in order to
	 * set the new activity level of the node
	 * 
	 * @param task
	 * @param coreID
	 */
	public void incrementTask(int task, int coreID) {

		// Getting the node corresponding to the core trying to schedule the task
		ArrayList<Integer> currentCoreNodeFrequencies = listOfCoreNodeFrequencies.get(coreID);
		Integer value = currentCoreNodeFrequencies.get(task); // get value
		currentCoreNodeFrequencies.set(task, value + 1);

		// Setting the colour of the node		
		setNodeColour(currentCoreNodeFrequencies.get(task), task, coreID);
		
		// Checking if node should be enlarged as nodes grow and shrink depending on whether they've been accessed
		if (currentCoreNodeFrequencies.get(task) % 200000 == 0) {
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.style","size:45px;");
		}else if (currentCoreNodeFrequencies.get(task) % 100000 == 0){
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.style","size:30px;");
		}

	}

	/**
	 * Preparing the GUI - sets up panels, labels and main frame
	 */
	private void prepareGui() {

		mainFrame = new JFrame("Paralex - Parallel Task Scheduling");
		mainFrame.setSize(1200, 900);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setMinimumSize(new Dimension(500, 500));

		// Panel to store the main four panels
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Panel for the graph key located in the top left
		JPanel graphKey = new JPanel();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		gbc.weighty = 0.1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		graphKey.setBorder(BorderFactory.createTitledBorder("Graph Key"));
		
		// Setting up the panels used to show the colours associated with number of visits
		graphKey.setLayout(new GridBagLayout());
		GridBagConstraints gbcKey = new GridBagConstraints();
		gbcKey.insets = new Insets(5,5,5,5);
		
		// 0 to 49
		JPanel panel50 = new JPanel();
		panel50.setLayout(new BorderLayout());
		JPanel color50 = new JPanel();
		color50.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color50.setBackground(new Color(245,223,107));
		panel50.add(color50, BorderLayout.WEST);
		panel50.add(new JLabel("  0 - 50 visits"), BorderLayout.EAST);
		gbcKey.gridx = 0;
		gbcKey.gridy = 0;
		gbcKey.weightx = 0.2;
		gbcKey.weighty = 0.2;
		gbcKey.anchor = GridBagConstraints.WEST;
		graphKey.add(panel50, gbcKey);

		// 50 to 149
		JPanel panel150 = new JPanel();
		panel150.setLayout(new BorderLayout());
		JPanel color150 = new JPanel();
		color150.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color150.setBackground(new Color(245,201,98));
		panel150.add(color150, BorderLayout.WEST);
		panel150.add(new JLabel("  50 - 149 visits"), BorderLayout.EAST);
		gbcKey.gridx = 0;
		gbcKey.gridy = 1;
		graphKey.add(panel150, gbcKey);

		// 150 to 499
		JPanel panel500 = new JPanel();
		panel500.setLayout(new BorderLayout());
		JPanel color500 = new JPanel();
		color500.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color500.setBackground(new Color(246,179,89));
		panel500.add(color500, BorderLayout.WEST);
		panel500.add(new JLabel("  150 - 499 visits"), BorderLayout.EAST);
		gbcKey.gridx = 0;
		gbcKey.gridy = 2;
		graphKey.add(panel500, gbcKey);

		// 500 - 999
		JPanel panel1000 = new JPanel();
		panel1000.setLayout(new BorderLayout());
		JPanel color1000 = new JPanel();
		color1000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color1000.setBackground(new Color(246,157,80));
		panel1000.add(color1000, BorderLayout.WEST);
		panel1000.add(new JLabel("  500 - 999 visits"), BorderLayout.EAST);
		gbcKey.gridx = 1;
		gbcKey.gridy = 0;
		graphKey.add(panel1000, gbcKey);

		// 1,000 - 4,999
		JPanel panel5000 = new JPanel();
		panel5000.setLayout(new BorderLayout());
		JPanel color5000 = new JPanel();
		color5000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color5000.setBackground(new Color(247,135,71));
		panel5000.add(color5000, BorderLayout.WEST);
		panel5000.add(new JLabel("  1,000 - 4,999 visits"), BorderLayout.EAST);
		gbcKey.gridx = 1;
		gbcKey.gridy = 1;
		graphKey.add(panel5000, gbcKey);

		// 5,000 - 9,999 
		JPanel panel10000 = new JPanel();
		panel10000.setLayout(new BorderLayout());
		JPanel color10000 = new JPanel();
		color10000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color10000.setBackground(new Color(248,92,52));
		panel10000.add(color10000, BorderLayout.WEST);
		panel10000.add(new JLabel("  5,000 - 9,999 visits"), BorderLayout.EAST);
		gbcKey.gridx = 1;
		gbcKey.gridy = 2;
		graphKey.add(panel10000, gbcKey);

		// 10,000 - 49,999
		JPanel panel50000 = new JPanel();
		panel50000.setLayout(new BorderLayout());
		JPanel color50000 = new JPanel();
		color50000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color50000.setBackground(new Color(221, 51, 8));
		panel50000.add(color50000, BorderLayout.WEST);
		panel50000.add(new JLabel("  10,000 - 49,999 visits"), BorderLayout.EAST);
		gbcKey.gridx = 2;
		gbcKey.gridy = 0;
		graphKey.add(panel50000, gbcKey);

		// 50,000 - 299,999
		JPanel panel300000 = new JPanel();
		panel300000.setLayout(new BorderLayout());
		JPanel color300000 = new JPanel();
		color300000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color300000.setBackground(new Color(249,26,25));
		panel300000.add(color300000, BorderLayout.WEST);
		panel300000.add(new JLabel("  50,000 - 299,999 visits"),
				BorderLayout.EAST);
		gbcKey.gridx = 2;
		gbcKey.gridy = 1;
		graphKey.add(panel300000, gbcKey);

		// 300,000 - 999,999
		JPanel panel1000000 = new JPanel();
		panel1000000.setLayout(new BorderLayout());
		JPanel color1000000 = new JPanel();
		color1000000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color1000000.setBackground(new Color(250,5,16));
		panel1000000.add(color1000000, BorderLayout.WEST);
		panel1000000.add(new JLabel("  300,000 - 999,999 visits"),
				BorderLayout.EAST);
		gbcKey.gridx = 2;
		gbcKey.gridy = 2;
		graphKey.add(panel1000000, gbcKey);

		// 1,000,000 - 2,999,999
		JPanel panel3000000 = new JPanel();
		panel3000000.setLayout(new BorderLayout());
		JPanel color3000000 = new JPanel();
		color3000000.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color3000000.setBackground(new Color(170,3,11));
		panel3000000.add(color3000000, BorderLayout.WEST);
		panel3000000.add(new JLabel("  1,000,000 - 2,999,999 visits"), BorderLayout.EAST);
		gbcKey.gridx = 3;
		gbcKey.gridy = 0;
		graphKey.add(panel3000000, gbcKey);

		// 3,000,000 +
		JPanel panel3000000plus = new JPanel();
		panel3000000plus.setLayout(new BorderLayout());
		JPanel color3000000plus = new JPanel();
		color3000000plus.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		color3000000plus.setBackground(new Color(126,2,8));
		panel3000000plus.add(color3000000plus, BorderLayout.WEST);
		panel3000000plus.add(new JLabel("  3,000,000 + visits"), BorderLayout.EAST);
		gbcKey.gridx = 3;
		gbcKey.gridy = 1;
		graphKey.add(panel3000000plus, gbcKey);
		
		contentPanel.add(graphKey, gbc);

		// Adding status panel which is located in the top right of the frame
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(2, 1));

		// Label indicating whether the program is still running
		runningLabel = new JLabel("Running...");
		runningLabel.setFont(new Font("Sans-serif", Font.BOLD, 20));
		runningLabel.setHorizontalAlignment(JLabel.CENTER);
		statusPanel.add(runningLabel);

		// Setting up other labels
		JPanel innerStatusPanel = new JPanel();
		innerStatusPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbcStatus = new GridBagConstraints();
		innerStatusPanel.setBorder(new EmptyBorder(0,10,0,10));
		gbcStatus.insets = new Insets(0,10,0,10);
		
		// Label for CPU Usage
		gbcStatus.gridx = 0;
		gbcStatus.gridy = 0;
		gbcStatus.weightx = 0.1;
		gbcStatus.weighty = 0.1;
		gbcStatus.fill = GridBagConstraints.BOTH;
		gbcStatus.anchor = GridBagConstraints.WEST;
		cpuLabel = new JLabel("CPU Usage: ");
		innerStatusPanel.add(cpuLabel, gbcStatus);

		// Label for Memory Usage
		gbcStatus.gridx = 1;
		gbcStatus.gridy = 0;
		gbcStatus.weightx = 0.1;
		gbcStatus.weighty = 0.1;
		gbcStatus.fill = GridBagConstraints.BOTH;
		memoryLabel = new JLabel("Memory Usage: ");
		innerStatusPanel.add(memoryLabel, gbcStatus);

		// Label for Elapsed time
		gbcStatus.gridx = 2;
		gbcStatus.gridy = 0;
		gbcStatus.weightx = 0.1;
		gbcStatus.weighty = 0.1;
		gbcStatus.fill = GridBagConstraints.BOTH;
		elapsedTimeLabel = new JLabel("Elapsed Time: 0 s");
		innerStatusPanel.add(elapsedTimeLabel, gbcStatus);

		// Label for Total schedule time
		gbcStatus.gridx = 3;
		gbcStatus.gridy = 0;
		gbcStatus.weightx = 0.1;
		gbcStatus.weighty = 0.1;
		gbcStatus.fill = GridBagConstraints.BOTH;
		totalScheduleTimeLabel = new JLabel("Total Schedule Time: ");
		innerStatusPanel.add(totalScheduleTimeLabel);

		statusPanel.add(innerStatusPanel);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		gbc.weighty = 0.1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;
		statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
		contentPanel.add(statusPanel, gbc);

		// Panel which holds all the graphs
		JPanel outerGraphPanel = new JPanel();
		if (numCores == 4 || numCores == 3) {
			outerGraphPanel.setLayout(new GridLayout(2, 2));
		} else if (numCores == 2) {
			outerGraphPanel.setLayout(new GridLayout(1, 2));
		} else {
			outerGraphPanel.setLayout(new GridLayout(1, 1));
		}

		// Adding graphs to the panel depending on the number of cores being used
		for (int i = 0; i < numCores; i++) {
			JPanel graphPanel = new JPanel();
			graphPanel.setLayout(new BorderLayout());
			graphPanel.setPreferredSize(new Dimension(300, 350));
			Viewer viewer = new Viewer(taskGraphList.get(i),
					Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			View view = viewer.addDefaultView(false);
			viewer.enableAutoLayout();
			graphPanel.add((Component) view);
			graphPanel.setBorder(BorderFactory.createTitledBorder("Task Graph For Core " + (i + 1)));

			outerGraphPanel.add(graphPanel);
		}

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.2;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		contentPanel.add(outerGraphPanel, gbc);

		// Panel which holds the whole process panel which is located on the bottom right
		JPanel outerProcessPanel = new JPanel();
		outerProcessPanel.setLayout(new GridBagLayout());
		outerProcessPanel.setBorder(BorderFactory
				.createTitledBorder("Current Best Schedule"));
		GridBagConstraints gbcProcess = new GridBagConstraints();

		// Panel which holds all the JTables
		JPanel processPanel = new JPanel();
		processPanel.setLayout(new GridLayout(1, totalProcessors));
		
		// Dynamically creating tables based on the number of processors being used
		for (int i = 0; i < totalProcessors; i++) {
			DefaultTableModel model = new DefaultTableModel() {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return String[].class;
				}
			};

			model.addColumn("P");
			JTable table = new JTable(model);
			table.setBackground(new Color(239, 239, 245));
			table.setRowSelectionAllowed(false);
			table.setEnabled(false); // Disabling user edit
			
			// Adding a listener to the table which highlights the task being pointed to on the input graph
			table.addMouseMotionListener(new MouseMotionListener() {

				public void mouseMoved(java.awt.event.MouseEvent evt) {
					int row = table.rowAtPoint(evt.getPoint());
					int col = table.columnAtPoint(evt.getPoint());

					if (row != -1) {
						if (table.getValueAt(row, col) != null) {
							String value = (String) table.getValueAt(row, col);
							if (value != "Idle Time") {
								String[] split = value.split("\\s+");
								value = split[0];
							}
							// Checks if nothing has been hovered over and you are not hovering over idle time
							if (currentHoveredCell == null && (!(value.equals("Idle Time")))) {
								for (int i=0; i<numCores; i++){
									currentColour[i] = taskGraphList.get(i).getNode(value).getAttribute("ui.class");
									taskGraphList.get(i).getNode(value).setAttribute("ui.class", "highlighted");
								}
								currentHoveredCell = value;
							// Checks if you are hovering over Idle time and you have already asssigned something
							} else if (currentHoveredCell != null && (value.equals("Idle Time"))) {
								for (int i=0; i<numCores; i++){
									taskGraphList.get(i).getNode(currentHoveredCell).setAttribute("ui.class","" + currentColour[i]);
									currentColour[i] = null;
								}
								currentHoveredCell = null;
							}
							
							if ((!(value.equals("Idle Time")))&& (!(currentHoveredCell.equals(value)))) {
								for (int i=0; i<numCores; i++){
									taskGraphList.get(i).getNode(currentHoveredCell).setAttribute("ui.class","" + currentColour[i]);
									currentColour[i] = taskGraphList.get(i).getNode(value).getAttribute("ui.class");
									taskGraphList.get(i).getNode(value).setAttribute("ui.class", "highlighted");

								}
								currentHoveredCell = value;
							}
						}
					}

				}

				@Override
				public void mouseDragged(MouseEvent e) {}
			});
			
			// Adding adapter to change the colour of the nodes when required
			table.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseExited(java.awt.event.MouseEvent evt) {
					if (currentHoveredCell != null) {
						for (int i=0; i<numCores; i++){
							taskGraphList.get(i).getNode(currentHoveredCell).setAttribute("ui.class", currentColour[i]);
							currentColour[i] = null;
						}
						currentHoveredCell = null;
					}
				}
			});

			// Adding the table to a list of the JTables
			procTables.add(table);
			
			// Adding a renderer to colour in the cells
			table.setDefaultRenderer(String[].class, new CustomTableRenderer());

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Processor " + (i+1), TitledBorder.CENTER, TitledBorder.TOP));
			panel.add(table);
			processPanel.add(panel);
		}

		// Adding all the tables to a single scroll pane and adding that to the outer most process panel
		JScrollPane scrollPane = new JScrollPane(processPanel);

		gbcProcess.gridx = 0;
		gbcProcess.gridy = 0;
		gbcProcess.weightx = 1.0;
		gbcProcess.weighty = 1.0;
		gbcProcess.fill = GridBagConstraints.BOTH;
		gbcProcess.anchor = GridBagConstraints.NORTH;
		gbcProcess.insets = new Insets(2, 2, 2, 2);
		outerProcessPanel.add(scrollPane, gbcProcess);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.EAST;
		contentPanel.add(outerProcessPanel, gbc);

		mainFrame.add(contentPanel);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Setting default size to fullscreen
	}

	/**
	 * Method which sets the colour of the node depending on the number of times a core has tried to schedule it.
	 * The numbers indicate how many times a node has been visited and tried to put on a schedule
	 * 
	 * @param activityNumber
	 * @param task
	 * @param coreID
	 */
	private void setNodeColour(int activityNumber, int task, int coreID) {

		if (activityNumber == 3000000) { // 3,000,000 +
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition3000000");

		} else if (activityNumber == 1000000) { // 1,000,000 to 2,999,999
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition1000000");

		} else if (activityNumber == 300000) { // 300,000 to 999,999
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition300000");

		} else if (activityNumber == 50000) { // 50,000 to 299,999
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition50000");

		} else if (activityNumber == 10000) { // 10,000 to 49,999
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition10000");

		} else if (activityNumber == 5000) { // 5,000 to 9,999
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition5000");

		} else if (activityNumber == 1000) { // 1,000 to 4,999
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition1000");

		} else if (activityNumber == 500) { // 500 to 999
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition500");

		} else if (activityNumber == 150) { // 150 to 499
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition150");

		} else if (activityNumber == 50) { // 50 to 149
			taskGraphList.get(coreID).getNode(task).setAttribute("ui.class",
					"partition50");
		}
	}

	/**
	 * Method which adds a input task to a JTable depending on the processor its scheduled on
	 * 
	 * @param proc
	 * @param task
	 * @param nodeCost
	 * @param startTime
	 */
	public void addTaskToProcessor(int proc, int task, int nodeCost, int startTime) {

		// Getting the table to modify
		JTable table = procTables.get(proc);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		// Finding the earliest start time on this processor and the idle time here
		int earliestStartOnProc = procFinishTimes[proc];
		int idleTime = startTime - earliestStartOnProc;

		// Adding a row to the table
		if (task != -1){
			// Adding a task
			String taskName = "" + graphStreamGraph.getNode(task).getId();
			taskName = taskName + " (Weight: "+ graphStreamGraph.getNode(task).getAttribute("Weight") + ")";

			// Adding idle time
			if (idleTime > 0) {
				model.addRow(new String[] { "Idle Time" });
				table.setRowHeight(table.getRowCount() - 1, (idleTime * scalingFactor)); // Scaling factor is used to make the rows fit
			}
			model.addRow(new String[] { taskName });
		}
		table.setRowHeight(table.getRowCount() - 1, (nodeCost * scalingFactor)); // Scaling factor is used to make the rows fit
		procFinishTimes[proc] = startTime + nodeCost;
	}

	/**
	 * Method to set up the scaling factor based on the weights of the input graph
	 */
	public void setupScalingFactor() {
		int[] nodeCostArray = input.getNodeCosts();
		int ave = 0;

		// Finding average node weights for this graph
		for (int i = 0; i < nodeCostArray.length; i++) {
			ave += nodeCostArray[i];
		}

		ave = ave / nodeCostArray.length;

		// Setting the scaling factor depending on the average
		if (ave <= 5) {
			scalingFactor = 15;
		} else if (ave <= 10) {
			scalingFactor = 10;
		} else if (ave <= 15) {
			scalingFactor = 5;
		} else if (ave <= 30) {
			scalingFactor = 3;
		} else {
			scalingFactor = 2;
		}
	}
	

	/**
	 * Play a sound clip (must be wav file)
	 * Reference: http://stackoverflow.com/tags/javasound/info
	 * @param fileName
	 */
	public Clip playSound(String fileName){
		try {
			Clip audioClip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(fileName));
			audioClip.open(inputStream);
			audioClip.start();
			return audioClip;
		}catch(Exception e){}
		return null;

	}
	
	/**
	 * Method which sets up a renderer for the process schedule tables so that cells can have their colours changed 
	 *
	 */
	public class CustomTableRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			// Setting the text in the rows to be centered
			setHorizontalAlignment(SwingConstants.CENTER);

			// Depending on the type of row it is (idle time or actual task) setting the colours
			String valueAt = (String) table.getModel().getValueAt(row, column);
			if (valueAt.equals("Idle Time")) {
				c.setBackground(new Color(239, 239, 245));
				c.setForeground(new Color(239, 239, 245));
			} else {
				c.setBackground(new Color(208, 208, 225));
				c.setForeground(Color.BLACK);
			}
			return c;
		}
	}
	
	/**
	 * Getters and setter for the labels used in the status panel 
	 * @return
	 */
	public JLabel getRunningLabel() {
		return runningLabel;
	}

	public void setRunningLabel(JLabel runningLabel) {
		this.runningLabel = runningLabel;
	}

	public JLabel getCpuLabel() {
		return cpuLabel;
	}

	public void setCpuLabel(JLabel cpuLabel) {
		this.cpuLabel = cpuLabel;
	}

	public JLabel getMemoryLabel() {
		return memoryLabel;
	}

	public void setMemoryLabel(JLabel memoryLabel) {
		this.memoryLabel = memoryLabel;
	}

	public JLabel getTotalScheduleTimeLabel() {
		return totalScheduleTimeLabel;
	}

	public void setTotalScheduleTimeLabel(JLabel totalScheduleTimeLabel) {
		this.totalScheduleTimeLabel = totalScheduleTimeLabel;
	}

	public JLabel getElapsedTimeLabel() {
		return elapsedTimeLabel;
	}

	public void setElapsedTimeLabel(JLabel elapsedTimeLabel) {
		this.elapsedTimeLabel = elapsedTimeLabel;
	}

	public ArrayList<Graph> getTaskGraphList() {
		return taskGraphList;
	}
}
