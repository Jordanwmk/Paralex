package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

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

		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.setPreferredSize(new Dimension(500, 500));
		
		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);
		((Component) view).setPreferredSize(new Dimension(500, 500));
		contentPane.add((Component) view, BorderLayout.CENTER);
		viewer.enableAutoLayout();
		
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
