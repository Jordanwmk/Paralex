
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
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
	private JPanel contentPane;
	// private Graph graph;
	private JTable table;
	public int totalProcessors = 0;
	public int[] procFinishTimes;
	public ArrayList<JTable> procTables = new ArrayList<JTable>();
	public Input input;
	private Graph taskGraph;
	private ArrayList<Integer> listOfTasks;
	private String currentHoveredCell = null;
	private String currentColour = null;
	public ArrayList<Schedule> currentBestScheduleList = new ArrayList<Schedule>();
	private int scalingFactor = 2;

	public VFrame() {
	}

	public VFrame(int numOfCores, String fileName, int processors) {
		instance = this;
		instance.setup(numOfCores, fileName, processors);
	}

	private void setup(int numOfCores, String fileName, int processors) {
		try {
			input = new Input(fileName);
			taskGraph = input.getInputG();
		} catch (IOException e) {
			e.printStackTrace();
		}

		totalProcessors = processors;
		setupScalingFactor();
		this.prepareGui();
		this.showFrame();
		int numNodes = taskGraph.getNodeCount();
		procFinishTimes = new int[numNodes];
		listOfTasks = new ArrayList<Integer>(Collections.nCopies(numNodes, 0));
		listOfAccess = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < numOfCores; i++) {
			listOfAccess.add(i, listOfTasks);
		}
		taskGraph.addAttribute("ui.stylesheet",
				"url('src/main/java/graphStyleSheet.css'))");
		// taskGraph.getNode(0).setAttribute("ui.color", 0.2);
		// taskGraph.getNode(1).setAttribute("ui.color", 1);

		for (Node node : taskGraph) {
			node.addAttribute("ui.label", node.getId());
			// node.addAttribute("ui.style", "fill-color: #80d4ff;");
		}
		// for (Edge edge: taskGraph.getEachEdge()){
		// edge.addAttribute("ui.style", "fill-color: black;");
		// }
		//

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
		mainFrame.setSize(1000, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridLayout(1, 2));

		JPanel outerGraphPanel = new JPanel();
		JPanel graphPanel = new JPanel();
		JPanel outerProcessPanel = new JPanel();
		JPanel processPanel = new JPanel();
		JPanel statusPanel = new JPanel();

		outerGraphPanel.setLayout(new BorderLayout());

		// JLabel graphLabel = new JLabel("Task Graph", SwingConstants.CENTER);
		graphPanel.setLayout(new BorderLayout());

		Viewer viewer = new Viewer(taskGraph,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);
		viewer.enableAutoLayout();

		graphPanel.add((Component) view, BorderLayout.CENTER);
		// graphPanel.add(graphLabel, BorderLayout.NORTH);

		outerGraphPanel.add(graphPanel, BorderLayout.CENTER);
		outerGraphPanel.setBorder(BorderFactory
				.createTitledBorder("Task Graph"));

		processPanel.setLayout(new GridLayout(1, totalProcessors));
		// Creating JTable for each processor and adding it to the processors
		// panel
		for (int i = 0; i < totalProcessors; i++) {

			DefaultTableModel model = new DefaultTableModel();
			model.addColumn("Process " + (i));
			JTable table = new JTable(model);

			table.setRowSelectionAllowed(false);
			table.setEnabled(false); // Disabling user edit
			table.addMouseMotionListener(new MouseMotionListener() {

				public void mouseMoved(java.awt.event.MouseEvent evt) {
					int row = table.rowAtPoint(evt.getPoint());
					int col = table.columnAtPoint(evt.getPoint());
					if (table.getValueAt(row, col) != null) {
						String value = (String) table.getValueAt(row, col);

						// Checks if nothing has been hovered over and you are
						// not hovering over
						// idle time
						if (currentHoveredCell == null && (!(value.equals("Idle Time")))) {
							currentColour = taskGraph.getNode(value).getAttribute("ui.class");
							System.out.println(value);
							System.out.println(currentColour);
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

							// System.out.println(taskGraph.getNode(currentHoveredCell).getAttribute("ui.class"));
						}
					}
					// System.out.println("The row is " + row +
					// ". The column is "+ col);
					// do some action if appropriate column

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
			procTables.add(table);
			JPanel tablePanel = new JPanel();
			tablePanel.setLayout(new BorderLayout());
			tablePanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), ("Proc " + (i + 1)),
					TitledBorder.CENTER, TitledBorder.TOP));
			tablePanel.add(table);
			processPanel.add(tablePanel);
		}

		JScrollPane scrollPane = new JScrollPane(processPanel);

		// JLabel processLabel = new JLabel("Current Best Schedule",
		// SwingConstants.CENTER);
		outerProcessPanel.setLayout(new BorderLayout());
		outerProcessPanel.add(scrollPane);
		outerProcessPanel.setBorder(BorderFactory
				.createTitledBorder("Current Best Schedule"));

		contentPanel.add(outerGraphPanel);
		contentPanel.add(outerProcessPanel);
		mainFrame.add(contentPanel);
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

	private void showFrame() {
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

	public void addTaskToProcessor(int proc, int task, int nodeCost,
			int startTime) {
		JTable table = procTables.get(proc);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int earliestStartOnProc = procFinishTimes[proc];
		int idleTime = startTime - earliestStartOnProc;

		if (idleTime > 0) {
			model.addRow(new String[] { "Idle Time" });
			;
			table.setRowHeight(table.getRowCount() - 1,
					(idleTime * scalingFactor));
		}

		model.addRow(new String[] { "" + task });
		table.setRowHeight(table.getRowCount() - 1, (nodeCost * scalingFactor));
		procFinishTimes[proc] = startTime + nodeCost;
	}

	// Setting up the factor to which the rows in the processor tables need to
	// be resized to
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
