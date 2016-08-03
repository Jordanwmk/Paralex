import java.util.List;

/**
 * Created by Hanzhi on 1/08/2016.
 */
public class Graph {
    private int[] nodeCosts;
    private int[] nodeBL;
    private int[][] edgeCosts;
    private List<Integer>[] adjList;

    public Graph (int[] nodeCosts, int[] nodeBL, int[][] edgeCosts, List<Integer>[] adjList){
        this.nodeCosts=nodeCosts;
        this.nodeBL=nodeBL;
        this.edgeCosts=edgeCosts;
        this.adjList=adjList;
    }
}
