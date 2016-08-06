package inputParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;





/**
 * Created by Ben on 8/4/2016.
 */
public class Input {

	public static ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>();
	public static int[] bottomLevels;
	public static Graph g;
	
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
		        System.out.println(node.getAttribute("weight"));
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
        for (int i = 0; i < numNodes; i++) {
        	ArrayList temp = new ArrayList();
        	adjacencyList.add(temp);
        	for (int j = 0; j < numNodes; j++) {
        		if (adjacencyMatrix[i][j] != -1){
        			temp.add(j);
        		}
        	}
        }
        
        
        //printing oout the adjaceny list to check if its correct --- can delete this ---
        System.out.println("------------------------------");
        for (int i = 0; i < adjacencyList.size(); i++){
            for (int j = 0; j < adjacencyList.get(i).size(); j++){
                System.out.print(adjacencyList.get(i).get(j) + ",");
            }
            System.out.println("");
        }
        System.out.println("------------------------------");
        
        
        // Finds all source nodes
        ArrayList<Integer> sourceNodes = new ArrayList<Integer>();
        int counter = 0;       
        for (int k = 0; k < numNodes; k++) {
        	counter = 0;
        	for (int i = 0; i < adjacencyList.size(); i++) {
        		for (int j = 0; j < adjacencyList.get(i).size(); j++) {

        			if ((adjacencyList.get(i).get(j).equals(k) && ( k!= i))) {
        				counter++;
        			} 
        			
        		}
        	}
        	if (counter == 0){
        		sourceNodes.add(k);
        	}
    	}
        System.out.println(sourceNodes);
        System.out.println(sourceNodes.size());
        
        
        //=============put in method===========
        //constructing a phantom node to be single source
        inputGraph.addNode("source1");
        inputGraph.getNode("source1").addAttribute("weight", "0");
        //adding it to the adjacency list with edges to real sources
        adjacencyList.add(sourceNodes);
        ArrayList singleSource = new ArrayList();
        singleSource.add(numNodes);
        
        
        //need to assign value to g so that bottom level function can access values
        g = inputGraph;
        
        //bottom level generation
        bottomLevels = new int[numNodes + 1];
        getbottom(singleSource);

        
        //printing out the bottom levels
        System.out.println("------------------------------");
        for (int j = 0; j < numNodes; j++){
            System.out.print(" the bottom level for " + j +" is " +  bottomLevels[j] +"\n");
        }
        
         
        return inputGraph;
    }
    
    public static int getbottom(ArrayList<Integer> nodes){
		
    	for (Integer i: nodes){
    		System.out.println("-------------START OF NEW CALL-----------------");
    		System.out.println("Im looking at node "+ i +" the value of i is " + i);
    		ArrayList<Integer> children = adjacencyList.get(i);
    		
    		System.out.println(children.isEmpty());
    		
    		for(Integer j : children){
				System.out.println("node" + i + " children are " + j);
			}
    		
    		
    		if(!children.isEmpty()){
    			System.out.println("node" + i + " has " + children.size()+" number of children"  +" the value of i is " + i);
    			ArrayList<Integer> cBottomLevels = new ArrayList<Integer>();
    				for(Integer j : children){
    					System.out.println("recusively looking at node " + j  +" the value of j is " + j);
    					ArrayList<Integer> singleChild = new ArrayList<Integer>();
    					singleChild.add(j);
    					System.out.println("recusively looking at node " + j +" the value of j is " + j);
    					cBottomLevels.add(getbottom(singleChild));
    				}
    				System.out.println("Im adding a value to bottomlevels for node "+i);
    				bottomLevels[i] = ( Integer.parseInt(g.getNode(i).getAttribute("weight")) + Collections.max(cBottomLevels));
    				return (Integer.parseInt(g.getNode(i).getAttribute("weight")) + Collections.max(cBottomLevels));
    		} else {
    			//System.out.println(g.getNode(i).getAttribute("weight"));
    			System.out.println("Im adding a value to bottomlevels for node "+i);
    			bottomLevels[i] = Integer.parseInt(g.getNode(i).getAttribute("weight"));
//    			System.out.println("Im looking at node "+ i +" the value of bottomLevels[i] is " + bottomLevels[i]);
    			
    			return Integer.parseInt(g.getNode(i).getAttribute("weight"));
    		}
    		
    		
    		
    		
    	}
		return 0;
    	
    }


}
