package inputParser;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import java.io.IOException;



/**
 * Created by Ben on 8/4/2016.
 */
public class Input {

    public static void main(String[] args) throws IOException {

    	//pass in the input file name
        Graph g = parseInput("F:\\test.dot");


    }


    public static Graph parseInput(String fileName) throws IOException {

        //create the graph object
        Graph inputGraph= new DefaultGraph("inputGraph");
        FileSource fs = FileSourceFactory.sourceFor(fileName);
        fs.addSink(inputGraph);

        //read in the graph contents from the given fileName
        try {
            fs.begin(fileName);
            System.out.println("booty");
        } catch( IOException e) {
            e.printStackTrace();
        }
        try {
            fs.end();
        } catch( IOException e) {
            e.printStackTrace();
        } finally {
            fs.removeSink(inputGraph);
        }


		for (Node node : inputGraph) {
		        node.addAttribute("ui.label", node.getId());
		        node.setAttribute("size", "small");
		    }
		
        inputGraph.display();

        int numNodes = inputGraph.getNodeCount();
        byte adjacencyMatrix[][] = new byte[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++){
            for (int j = 0; j < numNodes; j++){
                adjacencyMatrix[i][j] = (byte) (inputGraph.getNode(i).hasEdgeBetween(j) ? 1 : 0);
            }
        }

        for (int i = 0; i < numNodes; i++){
            for (int j = 0; j < numNodes; j++){
                System.out.print(adjacencyMatrix[i][j]);
            }
            System.out.println("");
        }


        return inputGraph;
    }


}
