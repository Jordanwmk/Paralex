/**
 * Created by Hanzhi on 5/08/2016.
 */
public class Main {
    public static void main(String[] args){

        long startTime = System.currentTimeMillis();
        Schedule bruteForceSolution=new BruteForceAlgorithm().schedule(Graph.getInstance());
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Brute Force Time: " + totalTime + "ms");
        System.out.println(bruteForceSolution);
        System.out.println("brute force total time: " + bruteForceSolution.getTotalTime());
        System.out.println();

        startTime = System.currentTimeMillis();
        Schedule aStarSolution=new AStarAlgorithm().schedule(Graph.getInstance());
        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("A Star Time: " + totalTime + "ms");

        System.out.println(aStarSolution);
        System.out.println("a* total time: "+aStarSolution.getTotalTime());

    }
}
