import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskGraph {
    private int[] nodeCosts;
    private int[] nodeBL;
    private int[][] edgeCosts;
    private List<ArrayList<Integer>> adjListChildren;
    private List<ArrayList<Integer>> adjListDependencies;
    private int numProcessors;
    private int totalTaskTime;
    private int totalNumTasks;
    private List<Integer> entryPoints;

    public TaskGraph(String inputFile, int numProcessors) throws IOException {
        this(inputFile,numProcessors,false);
    }

    public TaskGraph(String inputFile, int numProcessors, boolean useVisualisation) throws IOException{
        Input input = new Input(inputFile);
        this.numProcessors=numProcessors;

    	nodeCosts= input.getNodeCosts();
        nodeBL= input.getBotLevels();
        edgeCosts=input.getAdjMatrix();
        adjListChildren=input.getAdjList();
        adjListDependencies= input.getDependencyList();
        totalNumTasks = nodeCosts.length;
        
        for (int i = 0; i < totalNumTasks; i++) {
        	totalTaskTime += nodeCosts[i];
        }

        entryPoints=input.getSrcNodes();

        if(useVisualisation){
            
        }
    }

    int getNodeCost(int node){
        return nodeCosts[node];
    }

    int getBottomLevel(int node){
        return nodeBL[node];
    }

    int getEdgeCost(int src, int dest){
        return edgeCosts[src][dest];
    }

    List<Integer> getChildren(int node){
        return adjListChildren.get(node);
    }

    List<Integer> getDependencies(int node){
        return adjListDependencies.get(node);
    }

    public int getNumProcessors() {
        return numProcessors;
    }


    public int getTotalTaskTime() {
        return totalTaskTime;
    }

    public int getTotalNumTasks() {
        return totalNumTasks;
    }

    public List<Integer> getEntryPoints(){
        return entryPoints;
    }
}
