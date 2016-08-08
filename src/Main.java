/**
 * Created by Hanzhi on 5/08/2016.
 */
public class Main {
    public static void main(String[] args){
        long startTime,endTime,totalTime;

        startTime = System.currentTimeMillis();
        Schedule bruteForceSolution=new BruteForceAlgorithm().schedule(Graph.getInstance());
        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println(bruteForceSolution);
        System.out.println("brute Force solution time: " + bruteForceSolution.getTotalTime());
        System.out.println("Brute Force runtime: " + totalTime + "ms");
        System.out.println();

        startTime = System.currentTimeMillis();
        Schedule aStarSolution=new AStarAlgorithm().schedule(Graph.getInstance());
        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println(aStarSolution);
        System.out.println("A* solution time: "+aStarSolution.getTotalTime());
        System.out.println("A* runtime: " + totalTime + "ms");
        System.out.println();

        startTime = System.currentTimeMillis();
        Schedule branchAndBoundSolution=new BranchAndBoundAlgorithm().schedule(Graph.getInstance());
        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println(branchAndBoundSolution);
        System.out.println("Branch & Bound solution time: "+branchAndBoundSolution.getTotalTime());
        System.out.println("Branch & Bound runtime: " + totalTime + "ms");
    }
}
