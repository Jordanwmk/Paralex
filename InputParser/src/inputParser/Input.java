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

	private Graph inputG;
	private ArrayList<ArrayList<Integer>> adjList = new ArrayList<ArrayList<Integer>>();
	private int[] botLevels;
	private int adjMatrix[][];
	private ArrayList<Integer> srcNodes = new ArrayList<Integer>();
	
	public ArrayList<Integer> getSrcNodes() {
		return srcNodes;
	}
	public void setSrcNodes(ArrayList<Integer> srcNodes) {
		this.srcNodes = srcNodes;
	}
	public int[][] getAdjMatrix() {
		return adjMatrix;
	}
	public void setAdjMatrix(int[][] adjMatrix) {
		this.adjMatrix = adjMatrix;
	}
	public ArrayList<ArrayList<Integer>> getAdjList() {
		return adjList;
	}
	public void setAdjList(ArrayList<ArrayList<Integer>> adjList) {
		this.adjList = adjList;
	}
	public int[] getBotLevels() {
		return botLevels;
	}
	public void setBotLevels(int[] botLevels) {
		this.botLevels = botLevels;
	}
    public Graph getInputG() {
		return inputG;
	}
	public void setInputG(Graph inputG) {
		this.inputG = inputG;
	}
	
	public static void main(String[] args) throws IOException {
		Input input = new Input();
		
    	//pass in the input file name
        input.readInputGraph("src/test.dot");
        
        //creating the adjacency matrix and list
        input.createMatrixList();
        
        //finding all of the source nodes
        input.createSourceNodes();
        
        //creating a phantomSource node
        ArrayList phantomSource = input.createPhantomSource();
        
        //instantiating the bottom level array
        input.setBotLevels(new int[input.getInputG().getNodeCount()]);
        
        //using phantomSource to find all bottom levels and critical path
        int criticalPath = input.createBottomLevels(phantomSource);
        
        //removing the phantom node
        input.deletePhatomSource();
        
        //printing critical path and bottom levels
        System.out.println("The critical path of this input graph is " + criticalPath);
        System.out.println("------------------------------");
        for (int j = 0; j < input.getInputG().getNodeCount(); j++){
            System.out.print("The bottom level for node index[" + j +"] is " +  input.getBotLevels()[j] +"\n");
        }
        
    }

	private void readInputGraph(String fileName) throws IOException {
		
		//create the graph object
        Graph inputGraph = new DefaultGraph("inputGraph");
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

        this.setInputG(inputGraph);
	}
	
    private void createMatrixList (){
    	
    	Graph inputGraph = this.getInputG();
    	
    	//creating the adjacencyMatrix of the input file graph
        int numNodes = inputGraph.getNodeCount();
        int adjacencyMatrix[][] = new int[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++){
            for (int j = 0; j < numNodes; j++){
                adjacencyMatrix[i][j] = (inputGraph.getNode(i).hasEdgeBetween(j) ? Integer.parseInt(inputGraph.getNode(i).getEdgeBetween(j).getAttribute("weight")) : -1);
            }
        }
        this.setAdjMatrix(adjacencyMatrix);
        
   
        // creating the Adjacency list of the input file graph
        ArrayList temp1 = new ArrayList();
        for (int i = 0; i < numNodes; i++) {
        	ArrayList temp = new ArrayList();
        	temp1.add(temp);
        	for (int j = 0; j < numNodes; j++) {
        		if (adjacencyMatrix[i][j] != -1){
        			temp.add(j);
        		}
        	}
        }
        this.setAdjList(temp1);
        
    }
    
    private void createSourceNodes (){
    	
    	Graph inputGraph = this.getInputG();
        int numNodes = inputGraph.getNodeCount();
    	
        // Finds all source nodes
        ArrayList<Integer> sourceNodes = new ArrayList<Integer>();
        int counter = 0;       
        for (int k = 0; k < numNodes; k++) {
        	counter = 0;
        	for (int i = 0; i < this.getAdjList().size(); i++) {
        		for (int j = 0; j < this.getAdjList().get(i).size(); j++) {
        			if ((this.getAdjList().get(i).get(j).equals(k) && ( k!= i))) {
        				counter++;
        			} 
        		}
        	}
        	if (counter == 0){
        		sourceNodes.add(k);
        	}
    	}
      
        this.setSrcNodes(sourceNodes);
        
    }
    
    private ArrayList createPhantomSource (){
        
    	Graph inputGraph = this.getInputG();
    	ArrayList<Integer> sourceNodes = this.getSrcNodes();
    	
        //constructing a phantom node to be single source
        inputGraph.addNode("source1");
        inputGraph.getNode("source1").addAttribute("weight", "0");
        //adding it to the adjacency list with edges to real sources
        ArrayList<ArrayList<Integer>> adjacencyList = this.getAdjList();
        adjacencyList.add(sourceNodes);
        
        //setting the values of the newly changed list and graph
        this.setInputG(inputGraph);
        this.setAdjList(adjacencyList);
        
        //adding the new phantom node to a arraylist of its own which will be used to find all bottom levels
        ArrayList singleSource = new ArrayList();
        singleSource.add(inputGraph.getNodeCount() -1);
        
        return singleSource;
    }
    
    private void deletePhatomSource (){
    	
    	Graph inputGraph = this.getInputG();
    	inputGraph.removeNode("source1");
    	
    	ArrayList<ArrayList<Integer>> adjacencyList = this.getAdjList();
    	adjacencyList.remove(inputGraph.getNodeCount());
    	
    }
    
    private int createBottomLevels(ArrayList<Integer> nodes){
		
    	for (Integer i: nodes){
    		ArrayList<Integer> children = this.getAdjList().get(i);
	
    		if(!children.isEmpty()){
    			ArrayList<Integer> cBottomLevels = new ArrayList<Integer>();
    			
    				for(Integer j : children){
    					ArrayList<Integer> singleChild = new ArrayList<Integer>();
    					singleChild.add(j);
    					cBottomLevels.add(this.createBottomLevels(singleChild));
    				}
    				int[] temp = this.getBotLevels();
    				temp[i] = ( Integer.parseInt(this.getInputG().getNode(i).getAttribute("weight")) + Collections.max(cBottomLevels));
    				this.setBotLevels(temp);
    				return (Integer.parseInt(this.getInputG().getNode(i).getAttribute("weight")) + Collections.max(cBottomLevels));
    		} else {
    			int[] temp = this.getBotLevels();
    			temp[i] = Integer.parseInt(this.getInputG().getNode(i).getAttribute("weight"));
    			this.setBotLevels(temp);
    			
    			return Integer.parseInt(this.getInputG().getNode(i).getAttribute("weight"));
    		}
    	}
		return 0;
    }

  
}
