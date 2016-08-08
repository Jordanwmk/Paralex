import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hanzhi on 1/08/2016.
 */
public class Graph {
    private int[] nodeCosts;
    private int[] nodeBL;
    private int[][] edgeCosts;
    private List<ArrayList<Integer>> adjListChildren;
    private List<ArrayList<Integer>> adjListDependencies;
    private int numProcessors =2;
    private int totalTaskTime = 10;
    private int totalNumTasks = 4;

    private static Graph instance;

    private Graph(){
       Input input = new Input();
    	try {
			 input = new Input("src/test.dot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	nodeCosts= input.getNodeCosts();
        nodeBL= input.getBotLevels();
        edgeCosts=input.getAdjMatrix();
        adjListChildren=input.getAdjList();
        adjListDependencies= input.getDependencyList();

    }

    public static Graph getInstance(){
        if(instance==null) {
            instance = new Graph();
        }
        return instance;
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
        return Arrays.asList(new Integer[]{0});
    }
}
