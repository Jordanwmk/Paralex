import java.io.IOException;

public class Main {

    public static void main(String[] args){
//    	//Check for what options are enabled in executing the jar.
//    	

    	//Default values when no additional option is added.
    	int threadsUsed = 1;
    	String fileName = args[0];
    	int numProcessors = Integer.parseInt(args[1]);
    	boolean useVisualisation = false;
    	System.out.println("GG");
    	
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
     	if (useVisualisation){
     		VFrame frame = new VFrame(threadsUsed, fileName, numProcessors);
     		
     	}

		TaskGraph taskGraph;
		try {
			taskGraph = new TaskGraph(fileName,numProcessors,useVisualisation);
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


        if (useVisualisation){
        	TableThreader tt = new TableThreader(taskGraph, outputName);
        	tt.execute();
        	
        } else {
        	 startTime = System.currentTimeMillis();
             Schedule branchAndBoundSolution=new BranchAndBoundAlgorithm().schedule(taskGraph);
             endTime   = System.currentTimeMillis();
             totalTime = endTime - startTime;
             new Output().createOutput(branchAndBoundSolution,outputName);
//             VFrame frame = VFrame.getInstance();
//             frame.printStuff();
        }

//        startTime = System.currentTimeMillis();
//        Schedule aStarSolution=new AStarAlgorithm().schedule(taskGraph);
//        endTime   = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//        System.out.println(aStarSolution);
//        System.out.println("A* solution time: "+aStarSolution.getTotalTime());
//        System.out.println("A* runtime: " + totalTime + "ms");
//        System.out.println();

        
        
//        InputParser lol = new InputParser(filename);
//        Schedule =  scheduler.schedule(Graph.getGraph);
//        Outputparser.print(schedule);
        

    }
}
