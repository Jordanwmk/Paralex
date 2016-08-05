import java.util.List;

/**
 * Created by Hanzhi on 1/08/2016.
 */
public class Graph {
    private int[] nodeCosts;
    private int[] nodeBL;
    private int[][] edgeCosts;
    private List<Integer>[] adjListChildren;
    private List<Integer>[] adjListDependencies;

    private static Graph instance;

    public static Graph getInstance(){
        if(instance==null) {
            instance = new Graph();
        }
        return instance;
    }

    int getNodeCost(int node){
        return 0;
    }

    int getBottomLevel(int node){
        return 0;
    }

    int getEdgeCost(int src, int dest){
        return 0;
    }

    List<Integer> getChildren(int node){
        return null;
    }

    List<Integer> getDependencies(int node){
        return null;
    }


}
