/**
 * Created by Jun on 9/08/2016.
 */
public class Main {
    public static void main(String[] args){
    	//Check for what options are enabled in executing the jar.
    	
//    	//Default values when no additional option is added.
//    	int threadsUsed = 0;
//    	String fileName = args[0];
//    	int numOfProcessors = Integer.parseInt(args[1]);
//    	boolean useVisualisation = false;
//    	String outputName = fileName + "-output.dot";
//    	
//    	//Check when additional options are added.
//     	if (args.length > 2) {
//    		for (int i=2; i<args.length; i++ ){
//    			if (args[i].equals("-p")){
//    				threadsUsed = Integer.parseInt(args[i+1]);
//    			}else if (args[i].equals("-v") ){
//    				useVisualisation = true;
//    			}else if (args[i].equals("-o")){
//    				outputName = args[i+1];
//    			}
//    		}
//    	}
     	
        long startTime,endTime,totalTime;

//        startTime = System.currentTimeMillis();
//        Schedule bruteForceSolution=new BruteForceAlgorithm().schedule(Graph.getInstance());
//        endTime   = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//        System.out.println(bruteForceSolution);
//        System.out.println("brute Force solution time: " + bruteForceSolution.getTotalTime());
//        System.out.println("Brute Force runtime: " + totalTime + "ms");
//        System.out.println();
        
        startTime = System.currentTimeMillis();
        Schedule branchAndBoundSolution=new BranchAndBoundAlgorithm().schedule(Graph.getInstance());
        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println(branchAndBoundSolution);
        System.out.println("Branch & Bound solution time: "+branchAndBoundSolution.getTotalTime());
        System.out.println("Branch & Bound runtime: " + totalTime + "ms");
        System.out.println();

//        startTime = System.currentTimeMillis();
//        Schedule aStarSolution=new AStarAlgorithm().schedule(Graph.getInstance());
//        endTime   = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//        System.out.println(aStarSolution);
//        System.out.println("A* solution time: "+aStarSolution.getTotalTime());
//        System.out.println("A* runtime: " + totalTime + "ms");
//        System.out.println();

        new Output().createOutput(branchAndBoundSolution);
        System.exit(0);
        
//        InputParser lol = new InputParser(filename);
//        Schedule =  scheduler.schedule(Graph.getGraph);
//        Outputparser.print(schedule);
        

    }
}
