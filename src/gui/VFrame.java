package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
public class VFrame {

	private JFrame mainFrame;
	private JPanel contentPane;
	private Graph graph;

	// Should be removed before merging into master
	public static void main (String[] args) {
		VFrame frame = new VFrame();
		frame.createGraph();
		frame.prepareGui();
		frame.showFrame();
	}

	private void prepareGui () {

		mainFrame = new JFrame("Paralex - Parallel Task Scheduling");
		mainFrame.setSize(1000,800);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(1, 2));
		
		JPanel graphPane = new JPanel();
		graphPane.setLayout(new BorderLayout());
		graphPane.setPreferredSize(new Dimension(500, 800));
		
		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);
		viewer.enableAutoLayout();
		//((Component) view).setPreferredSize(new Dimension(500, 500));
		graphPane.add((Component) view, BorderLayout.CENTER);
		
		JPanel processPanel = new JPanel();
		processPanel.setLayout(new BorderLayout());
		JButton button = new JButton("hello");
		//processPanel.add(button);
		
		contentPane.add(graphPane);
		contentPane.add(processPanel);
		mainFrame.add(contentPane);
		//mainFrame.pack();
	}

	private void showFrame () {
		mainFrame.setVisible(true);
	}


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
