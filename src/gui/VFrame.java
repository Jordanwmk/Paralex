package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;


/**
 * 
 * Created 07/08/16
 * @author Ammar Bagasrawala
 *
 */
public class VFrame{

	private JFrame mainFrame;
	private JPanel contentPane;
	private Graph graph;
	private JTable table;
	private int processors = 3;
	private ArrayList<JTable> procTables = new ArrayList<JTable>();

	// Should be removed before merging into master
	public static void main (String[] args) {
		VFrame frame = new VFrame();
		frame.createGraph();
		frame.prepareGui();
		frame.showFrame();
		frame.addTaskToProcessor(1, 0, 1);	
		frame.addTaskToProcessor(1, 2, 2);
		frame.addTaskToProcessor(1, 1, 2);
		frame.addTaskToProcessor(0, 1, 8);
		//frame.removeTaskFromProcessor(1, 1);
		frame.addIdleTime(1, 2, 3);
	}

	private void prepareGui () {

		mainFrame = new JFrame("Paralex - Parallel Task Scheduling");
		mainFrame.setSize(1000,700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(1, 2));

		// Left panel to hold graph
		JPanel graphPane = new JPanel();
		graphPane.setLayout(new BorderLayout());
		graphPane.setPreferredSize(new Dimension(500, 7));

		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);
		viewer.enableAutoLayout();
		//((Component) view).setPreferredSize(new Dimension(500, 500));
		graphPane.add((Component) view, BorderLayout.CENTER);

		// Right panel to hold table of processors and tasks
		JPanel processPanel = new JPanel();
		processPanel.setLayout(new GridLayout(1, processors));

		
		// Creating JTable for each processor and adding it to the processors panel
		for (int i = 0; i < processors; i++) {
			
			DefaultTableModel model = new DefaultTableModel();
			model.addColumn("Proc " + i);
			model.addRow(new String[] {"Tasks"});
			JTable table = new JTable(model);
			
			JScrollPane scrollPane = new JScrollPane(table);
			table.setRowSelectionAllowed(false);
			table.setEnabled(false);	// Disabling user edit
			procTables.add(table);
			processPanel.add(scrollPane);
		}
		
		contentPane.add(graphPane);
		contentPane.add(processPanel);
		mainFrame.add(contentPane);
	}

	private void showFrame () {
		mainFrame.setVisible(true);
	}

	// Method to add a task to a certain processor
	public void addTaskToProcessor(int proc, int task, int nodeCost) {
		JTable table = procTables.get(proc);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new String[]{"" + task});
		int numRows = model.getRowCount();	// Getting which row to change height of
		table.setRowHeight(numRows-1, (nodeCost*16));	// 16 px is original height		
	}
	
	// Allows removal of a task
	public void removeTaskFromProcessor (int proc, int task) {
		JTable table = procTables.get(proc);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		// Not sure if row should be removed or not +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		for (int i = 0; i < model.getRowCount(); i++) {
			if (model.getValueAt(i, 0).equals(Integer.toString(task))) {
				//model.removeRow(i);			// Removes row
				model.setValueAt("", i, 0);		// Makes row blank
			}
		}
	}
	
	// Method to add idle time to a processor
	public void addIdleTime (int proc, int priorTask, int idleTime) {
		JTable table = procTables.get(proc);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		for (int i = 0; i < model.getRowCount(); i++) {
			if (model.getValueAt(i, 0).equals(Integer.toString(priorTask))) {
				model.insertRow(i+1 , new String[]{"Idle Time"});
				table.setRowHeight(i+1, (idleTime * 16));
			}
		}
		
	}
	
	// TO BE REMOVED
	private void createGraph () {
		graph = new DefaultGraph("graph");
		graph.addNode("A" );
		graph.addNode("B" );
		graph.addNode("C" );
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
	}


}
