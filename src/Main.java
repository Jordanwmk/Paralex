/**
 * Created by Hanzhi on 5/08/2016.
 */
public class Main {
    public static void main(String[] args){
        Schedule bruteForceSolution=new BruteForceAlgorithm().schedule(Graph.getInstance());
        System.out.println(bruteForceSolution);
        System.out.println("brute force total time: " + bruteForceSolution.getTotalTime());
        System.out.println();

        Schedule aStarSolution=new AStarAlgorithm().schedule(Graph.getInstance());
        System.out.println(aStarSolution);
        System.out.println("a* total time: "+aStarSolution.getTotalTime());
    }
}
