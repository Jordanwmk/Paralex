package inputParser;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import java.io.IOException;
import java.util.ArrayList;



/**
 * Created by Ben on 8/4/2016.
 */
public class Input {

    public static void main(String[] args) throws IOException {

    	//pass in the input file name
        Graph g = parseInput("src/test.dot");


    }


    public static Graph parseInput(String fileName) throws IOException {

        //create the graph object
        Graph inputGraph= new DefaultGraph("inputGraph");
        FileSource fs = FileSourceFactory.sourceFor(fileName);
        fs.addSink(inputGraph);

        //read in the graph contents from the given fileName
        try {
            fs.begin(fileName);

            while(fs.nextEvents()){
            	//optional code here
            }
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


        //displaying graph --- can delete this ---
		for (Node node : inputGraph) {
		        node.addAttribute("ui.label", node.getId());
		        node.setAttribute("size", "small");
		    }
        inputGraph.display();
        
        //creating the adjacencyMatrix of the input file graph
        int numNodes = inputGraph.getNodeCount();
        int adjacencyMatrix[][] = new int[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++){
            for (int j = 0; j < numNodes; j++){
                adjacencyMatrix[i][j] = (inputGraph.getNode(i).hasEdgeBetween(j) ? Integer.parseInt(inputGraph.getNode(i).getEdgeBetween(j).getAttribute("weight")) : -1);
            }
        }

        //printing the adjacency Matrix --- can delete this ---
        for (int i = 0; i < numNodes; i++){
            for (int j = 0; j < numNodes; j++){
                System.out.print(adjacencyMatrix[i][j] + ",");
            }
            System.out.println("");
        }

        
        // Adjacency list
        ArrayList<ArrayList<Integer>> listOfLists = new ArrayList<ArrayList<Integer>>();
        
        for (int i = 0; i < numNodes; i++) {
        	ArrayList temp = new ArrayList();
        	listOfLists.add(temp);
        	for (int j = 0; j < numNodes; j++) {
        		if (adjacencyMatrix[i][j] != -1){
        			temp.add(j);
        		}
        	}
        }
        
        System.out.println("------------------------------");
        
        for (int i = 0; i < listOfLists.size(); i++){
            for (int j = 0; j < listOfLists.get(i).size(); j++){
                System.out.print(listOfLists.get(i).get(j) + ",");
            }
            System.out.println("");
        }
        

        return inputGraph;
    }


}
