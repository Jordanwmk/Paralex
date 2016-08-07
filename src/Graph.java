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
    private List<List<Integer>> adjListChildren;
    private List<List<Integer>> adjListDependencies;
    private int numProcessors =2;
    private int totalTaskTime = 10;
    private int totalNumTasks = 4;

    private static Graph instance;

    private Graph(){
        nodeCosts=new int[]{2,3,3,2};
        nodeBL=new int[]{7,5,5,2};
        edgeCosts=new int[][]{{-1,1,2,-1},{-1,-1,-1,2},{-1,-1,-1,1},{-1,-1,-1,-1}};
        adjListChildren=new ArrayList<>();
        adjListChildren.add(Arrays.asList(new Integer[]{1,2}));
        adjListChildren.add(Arrays.asList(new Integer[]{3}));
        adjListChildren.add(Arrays.asList(new Integer[]{3}));
        adjListChildren.add(Arrays.asList(new Integer[]{}));

        adjListDependencies=new ArrayList<>();
        adjListDependencies.add(Arrays.asList(new Integer[]{}));
        adjListDependencies.add(Arrays.asList(new Integer[]{0}));
        adjListDependencies.add(Arrays.asList(new Integer[]{0}));
        adjListDependencies.add(Arrays.asList(new Integer[]{1,2}));
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
