import java.io.IOException;

public class Main {

    public static void main(String[] args){
//    	//Check for what options are enabled in executing the jar.
//    	
    	//Default values when no additional option is added.
    	int threadsUsed = 0;
    	String fileName = args[0];
    	int numProcessors = Integer.parseInt(args[1]);
    	boolean useVisualisation = false;
    	
    	String[] splitName = fileName.split(".dot");
    	String outputName = splitName[0] + "-output.dot";
    	
    	//Check when additional options are added.
     	if (args.length > 2) {
    		for (int i=2; i<args.length; i++ ){
    			if (args[i].equals("-p")){
    				threadsUsed = Integer.parseInt(args[i+1]);
    			}else if (args[i].equals("-v") ){
    				useVisualisation = true;
    			}else if (args[i].equals("-o")){
    				outputName = args[i+1];
    				if (!outputName.contains(".dot")) {	// Checking if user has specified file extension as .dot
    					outputName = outputName + ".dot";
    				}
    			}
    		}
    	}

		Graph taskGraph;
		try {
			taskGraph = new Graph(fileName,numProcessors);
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("IOException while reading graph file");
			return;
		}
     	
        long startTime,endTime,totalTime;

//        startTime = System.currentTimeMillis();
//        Schedule bruteForceSolution=new BruteForceAlgorithm().schedule(taskGraph);
//        endTime   = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//        System.out.println(bruteForceSolution);
//        System.out.println("brute Force solution time: " + bruteForceSolution.getTotalTime());
//        System.out.println("Brute Force runtime: " + totalTime + "ms");
//        System.out.println();

        startTime = System.currentTimeMillis();
        Schedule branchAndBoundSolution=new BranchAndBoundAlgorithm().schedule(taskGraph);
        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println(branchAndBoundSolution);
        System.out.println("Branch & Bound solution time: "+branchAndBoundSolution.getTotalTime());
        System.out.println("Branch & Bound runtime: " + totalTime + "ms");
        System.out.println();


//        startTime = System.currentTimeMillis();
//        Schedule aStarSolution=new AStarAlgorithm().schedule(taskGraph);
//        endTime   = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//        System.out.println(aStarSolution);
//        System.out.println("A* solution time: "+aStarSolution.getTotalTime());
//        System.out.println("A* runtime: " + totalTime + "ms");
//        System.out.println();

        new Output().createOutput(branchAndBoundSolution,outputName);
        
//        InputParser lol = new InputParser(filename);
//        Schedule =  scheduler.schedule(Graph.getGraph);
//        Outputparser.print(schedule);
        

    }
}
