import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.IOException;

public class OutputParser{

	public void printOutput(Graph graph) throws IOException{
		
        for (Node node: graph){
        	node.removeAttribute("ui.label");
        	node.removeAttribute("ui.style");
        }
		
		FileSinkDOT writer = new FileSinkDOT(true);
        writer.setDirected(true);
        writer.writeAll(graph, "src/test2.dot");
	}
	
}
