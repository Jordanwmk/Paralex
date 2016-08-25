import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

/**
 * The Input class handles all of the input reading. It takes a fileName and attempts to read the
 * corresponding file. If it is a valid dot file it will successfully read the file and create a graph
 * from the contents. The Input class makes use of the graphStream external library to do this. Once the 
 * input graph has been produced various relevant pieces of information are then calculated, such as the 
 * graphs adjacency list and matrix, other things such as node bottom levels are also calculated. All of
 * the calculated information related to the input graph is stored in various data structures inside this
 * class. 
 * @author bmit436
 *
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
	private void setInputG(Graph inputG) {
		this.inputG = inputG;
	}
	public Input(){
		
	}
	
	/**
	 * This is the Input classe's main constructor, it takes a file and constructs the
	 * related graph, via multiple other methods of the Input class. 
	 * 
	 * @param fileName - The name of the file containing the input graph
	 * @throws IOException
	 */
	public Input(String fileName) throws IOException{
		//pass in the input file name
        this.readInputGraph(fileName);
        
        //creating the adjacency matrix and list
        this.createMatrixList();
        
        //creating the array of node costs
        this.createNodeCosts();
        

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

	/**
	 * This method reads in the input graph from dot format into graphStreams in built
	 * Graph, Node and Edge classes. 
	 * 
	 * @param fileName - The filename of the input graph
	 * @throws IOException
	 */
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
        
        //setting the nodes and edges to be Integer values
        List<String> attributes=new ArrayList<String>(inputGraph.getAttributeKeySet());
        for(String attribute:attributes){
        	inputGraph.removeAttribute(attribute);
        }
        
        //change the weights from double to int
        for(Node node: inputGraph){
        	node.setAttribute("Weight", (((Double) (node.getAttribute("Weight"))).intValue()));
        }
        for(Edge e : inputGraph.getEachEdge()){
        	e.setAttribute("Weight", ((Double)e.getAttribute("Weight")).intValue());
        }
        this.setInputG(inputGraph);
	}
	
	
	/**
	 * The creatematrixList method creates the input graphs' adjacency list, matrix and dependency
	 * list. It then stores each of these in a data structure that the instance of Input owns. 
	 */
    private void createMatrixList (){
    	
    	Graph inputGraph = this.getInputG();
    	
    	//creating the adjacencyMatrix of the input file graph
        int numNodes = inputGraph.getNodeCount();
        int adjacencyMatrix[][] = new int[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++){
            for (int j = 0; j < numNodes; j++){
                adjacencyMatrix[i][j] = (int) (inputGraph.getNode(i).hasEdgeBetween(j) ? inputGraph.getNode(i).getEdgeBetween(j).getAttribute("Weight") : -1);
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
    
    /**
     * The createSourceNodes method takes no parametres and simply finds all of the source nodes
     * in the inputted graph, it then stores these nodes into an ArrayList of Integers, where each
     * Integer relates to a node index.
     */
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
    
    
    /**
     * The createNodeCosts method iterates over all of the nodes of the graph and stores
     * each of the nodes cost into an int array where the value of position i is the weight
     * of node[i].
     */
    private void createNodeCosts(){
    	Graph inputGraph = this.getInputG();
        int numNodes = inputGraph.getNodeCount();
        int[] nodeCosts = new int[numNodes];
        for (int i =0; i<numNodes; i++) {
        	nodeCosts[i] = inputGraph.getNode(i).getAttribute("Weight");
        	
        }
        
        this.setNodeCosts(nodeCosts);
    }
    
    /**
     * The createPhantomSource method creates a phantom source node that has edges to all
     * of the real sources of the input graph. The phantom node allows the bottom levels to be
     * found in a more efficient way. The method returns an ArrayList with a node index corresponding
     * to the newly created phantom node.
     * @return
     */
    private ArrayList createPhantomSource (){
        
    	Graph inputGraph = this.getInputG();
    	ArrayList<Integer> sourceNodes = this.getSrcNodes();
    	
        //constructing a phantom node to be single source
        inputGraph.addNode("source1");
        inputGraph.getNode("source1").addAttribute("Weight", 0);
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
    
    /**
     * For each node in the input graph the createBottomLevels method iterates over every path to any 
     * leaf. It recursively carries out this path finding process. Once a leaf is encountered recursively 
     * the leaf returns its own bottom level which is just its own weight. its parents receive bottom
     * levels from all of their children and add their own weight to the max in order to find their bottom
     * level. Bottom levels are recursively returned up while simultaneously being stored in a 
     * @param nodes
     * @return 
     */
 	private int createBottomLevels(ArrayList<Integer> nodes){
		for (Integer i: nodes){
    		ArrayList<Integer> children = this.getAdjList().get(i);
    		
    		//checking if the node is a leaf
    		if(!children.isEmpty()){
    			ArrayList<Integer> cBottomLevels = new ArrayList<Integer>();
    				//get all of the children paths of this node
    				for(Integer j : children){
    					ArrayList<Integer> singleChild = new ArrayList<Integer>();
    					singleChild.add(j);
    					cBottomLevels.add(this.createBottomLevels(singleChild));
    				}
    				//create the bottom level of this node and set it
    				int[] temp = this.getBotLevels();
    				temp[i] =  ((Integer)this.getInputG().getNode(i).getAttribute("Weight")) + Collections.max(cBottomLevels);
    				this.setBotLevels(temp);
    				return ((Integer) this.getInputG().getNode(i).getAttribute("Weight")) + Collections.max(cBottomLevels);
    		} else {
    			//if the node is a leaf set bottom level to its weight 
    			int[] temp = this.getBotLevels();
    			temp[i] = ((Integer) this.getInputG().getNode(i).getAttribute("Weight"));
    			this.setBotLevels(temp);
    			
    			return ((Integer) this.getInputG().getNode(i).getAttribute("Weight"));
    		}
    	}
		return 0;
    }
 
 	/**
 	 * This method removes the phantom node from the graph
 	 */
    private void deletePhatomSource (){
    	
    	Graph inputGraph = this.getInputG();
    	inputGraph.removeNode("source1");
    	
    	ArrayList<ArrayList<Integer>> adjacencyList = this.getAdjList();
    	adjacencyList.remove(inputGraph.getNodeCount());
    	
    }
    
    /**
 	 * This will show the visualisation of the algorithm
 	 */
   	public void showVisualisation(){
		getInputG().display();
	}
  
}
