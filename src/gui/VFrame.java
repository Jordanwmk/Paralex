package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;


/**
 * 
 * Created 07/08/16
 * @author Ammar Bagasrawala
 *
 */
public class VFrame{

	private static Graph graph = new SingleGraph("Tutorial 1");

    public static class MyFrame extends JFrame
    {
        private static final long serialVersionUID = 8394236698316485656L;

        //private Graph graph = new MultiGraph("embedded");
        private Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        //private Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);
        private View view = viewer.addDefaultView(false);

        public MyFrame() {
             setLayout(new BorderLayout());
             this.add(BorderLayout.CENTER);
             setDefaultCloseOperation(EXIT_ON_CLOSE);
             viewer.enableAutoLayout();
        }
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MyFrame frame = new MyFrame();
                frame.setSize(320, 240);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                graph.addNode("A");
                graph.addNode("B");
                graph.addNode("C");
                graph.addEdge("AB", "A", "B");
                graph.addEdge("BC", "B", "C");
                graph.addEdge("CA", "C", "A");

                graph.addAttribute("ui.quality");
                graph.addAttribute("ui.antialias");
            }
        });
    }
	
}
