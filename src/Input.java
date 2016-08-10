

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

/**
 * Created by Ben Mitchell on 8/4/2016.
 * Edited by Ammar Bagasrawala
 */
public class Input {

	
	private Graph inputG;
	private ArrayList<ArrayList<Integer>> adjList = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> dependencyList = new ArrayList<ArrayList<Integer>>();
	private int[] nodeCosts;
	private int[] botLevels;
	private int adjMatrix[][];
	private ArrayList<Integer> srcNodes = new ArrayList<Integer>();
	
	public int[] getNodeCosts() {
		return nodeCosts;
	}
	public void setNodeCosts(int[] nodeCosts) {
		this.nodeCosts = nodeCosts;
	}
	public ArrayList<ArrayList<Integer>> getDependencyList() {
		return dependencyList;
	}
	public void setDependencyList(ArrayList<ArrayList<Integer>> dependencyList) {
		this.dependencyList = dependencyList;
	}
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
	
	public Input(){
		
	}
	public Input(String fileName) throws IOException{
		//pass in the input file name
        this.readInputGraph(fileName);
        
        //creating the adjacency matrix and list
        this.createMatrixList();
        
        //creating the array of node costs
        this.createNodeCosts();
        
        // Adding labels to the graph so they display
        for (Node node : this.getInputG()) {
        	node.addAttribute("ui.label", node.getId());
        	node.addAttribute("ui.style", "fill-color: red;");
        }
        
        
        this.getInputG().display();  
        
        //finding all of the source nodes
        this.createSourceNodes();
        
        //creating a phantomSource node
        ArrayList phantomSource = this.createPhantomSource();
        
        //instantiating the bottom level array
        this.setBotLevels(new int[this.getInputG().getNodeCount()]);
        
        //using phantomSource to find all bottom levels and critical path
        int criticalPath = this.createBottomLevels(phantomSource);
        
        //removing the phantom node
        this.deletePhatomSource();
        
	}
	
//	public static void main(String[] args) throws IOException {
//		Input input = new Input();
//		
//    	//pass in the input file name
//        input.readInputGraph("src/test.dot");
//        
//        //creating the adjacency matrix and list
//        input.createMatrixList();
//        
//        //finding all of the source nodes
//        input.createSourceNodes();
//        
//        // Adding labels to the graph so they display
//        for (Node node : input.getInputG()) {
//        	node.addAttribute("ui.label", node.getId());
//        	node.addAttribute("ui.style", "fill-color: red;");
//        }
//        
//        
//        input.getInputG().display();      
//        
//        //creating a phantomSource node
//        ArrayList phantomSource = input.createPhantomSource();
//        
//        //instantiating the bottom level array
//        input.setBotLevels(new int[input.getInputG().getNodeCount()]);
//        
//        //using phantomSource to find all bottom levels and critical path
//        int criticalPath = input.createBottomLevels(phantomSource);
//        
//        //removing the phantom node
//        input.deletePhatomSource();
//        
//        //printing critical path and bottom levels
//        System.out.println("The critical path of this input graph is " + criticalPath);
//        System.out.println("------------------------------");
//        for (int j = 0; j < input.getInputG().getNodeCount(); j++){
//            System.out.print("The bottom level for node index[" + j +"] is " +  input.getBotLevels()[j] +"\n");
//        }
//        
//        System.out.println("------------------------------");
//        System.out.println("Source nodes for this graph");
//        ArrayList<Integer> srcNodes = input.getSrcNodes();
//        for (int i = 0; i < srcNodes.size(); i ++) {
//        	System.out.print(srcNodes.get(i) + " ");
//        }
//        System.out.println("");
//        
//        //printing matrixList to check validity
//        System.out.println("--------Adj matrix below----------------------");
//        for (int i = 0; i < input.getInputG().getNodeCount(); i++){
//            for (int j = 0; j < input.getInputG().getNodeCount(); j++){
//                System.out.print(input.getAdjMatrix()[i][j]+ " ");
//            }
//            System.out.println("");
//        }
//        System.out.println("-----Adj List below--------------------");
//        
//        for (int i = 0; i < input.getInputG().getNodeCount(); i++){
//        	 for (int j = 0; j < input.getAdjList().get(i).size(); j++){
//                 System.out.print(input.getAdjList().get(i).get(j)+ " ");
//             }
//             System.out.println("");
//        }
//        System.out.println("------------------------------");
//        System.out.println("-----Dependency List below--------------------");
//        
//        for (int i = 0; i < input.getInputG().getNodeCount(); i++){
//        	 for (int j = 0; j < input.getDependencyList().get(i).size(); j++){
//                 System.out.print(input.getDependencyList().get(i).get(j)+ " ");
//             }
//             System.out.println("");
//        }
//        System.out.println("------------------------------");
//        
//    }

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
                adjacencyMatrix[i][j] = (inputGraph.getNode(i).hasEdgeBetween(j) ? ((Integer) inputGraph.getNode(i).getEdgeBetween(j).getAttribute("Weight")).intValue() : -1);
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
        
        ArrayList temp2 = new ArrayList();
        for (int i = 0; i < numNodes; i++) {
        	ArrayList temp3 = new ArrayList();
        	temp2.add(temp3);
        	for (int j = 0; j < numNodes; j++) {
        		if (adjacencyMatrix[j][i] != -1){
        			temp3.add(j);
        		}
        	}
        }
        this.setDependencyList(temp2);
        
    }
    
    private void createSourceNodes (){
    	
    	Graph inputGraph = this.getInputG();
        int numNodes = inputGraph.getNodeCount();
        ArrayList<Integer> sourceNodes = new ArrayList<Integer>();
        int [][] matrix = getAdjMatrix();  
        
        // Finds all source nodes by checking each column and if it's all -1s then it is a source node     
        for (int i = 0; i < numNodes; i++) {
        	int counter = 0;
        	for (int j = 0; j < numNodes; j++) {
        		if (matrix[j][i] != -1) {
        			counter++;
        		}
        	}
        	if (counter == 0) {
        		sourceNodes.add(i);
        	}
        }
        
        this.setSrcNodes(sourceNodes);
    }
    
    private void createNodeCosts(){
    	Graph inputGraph = this.getInputG();
        int numNodes = inputGraph.getNodeCount();
        int[] nodeCosts = new int[numNodes];
        for (int i =0; i<numNodes; i++) {
        	nodeCosts[i] = ((Integer) inputGraph.getNode(i).getAttribute("Weight")).intValue();
        	
        }
        
        this.setNodeCosts(nodeCosts);
    }
    
    private ArrayList createPhantomSource (){
        
    	Graph inputGraph = this.getInputG();
    	ArrayList<Integer> sourceNodes = this.getSrcNodes();
    	
        //constructing a phantom node to be single source
        inputGraph.addNode("source1");
        inputGraph.getNode("source1").addAttribute("Weight", "0");
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
    				temp[i] = ( ((Integer) this.getInputG().getNode(i).getAttribute("Weight")).intValue() + Collections.max(cBottomLevels));
    				this.setBotLevels(temp);
    				return (((Integer) this.getInputG().getNode(i).getAttribute("Weight")).intValue() + Collections.max(cBottomLevels));
    		} else {
    			int[] temp = this.getBotLevels();
    			temp[i] = ((Integer) this.getInputG().getNode(i).getAttribute("Weight")).intValue();
    			this.setBotLevels(temp);
    			
    			return ((Integer) this.getInputG().getNode(i).getAttribute("Weight")).intValue();
    		}
    	}
		return 0;
    }
 
    private void deletePhatomSource (){
    	
    	Graph inputGraph = this.getInputG();
    	inputGraph.removeNode("source1");
    	
    	ArrayList<ArrayList<Integer>> adjacencyList = this.getAdjList();
    	adjacencyList.remove(inputGraph.getNodeCount());
    	
    }
    
   

  
}
