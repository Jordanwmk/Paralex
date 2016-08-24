import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Main {

    public static void main(String[] args){
//    	//Check for what options are enabled in executing the jar.
//    	

    	//Default values when no additional option is added.
    	int threadsUsed = 1;
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
     	
//     	if (useVisualisation){
//     		VFrame frame = new VFrame(threadsUsed, fileName, numProcessors);
//     	}
     	
		TaskGraph taskGraph;
		try {
			taskGraph = new TaskGraph(fileName,numProcessors,useVisualisation);
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("IOException while reading graph file");
			return;
		}

        long startTime,endTime,totalTime;

        startTime = System.currentTimeMillis();
        BranchAndBoundAlgorithm algorithm = new BranchAndBoundAlgorithm();

        if (useVisualisation){
        	VFrame frame = new VFrame(threadsUsed, fileName, numProcessors);
        	TableThreader tt = new TableThreader(algorithm, frame);

        	tt.execute();
        }
        Schedule branchAndBoundSolution = algorithm.schedule(taskGraph);
        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        try {
        	while(true){
        		Thread.sleep(100);
        		Main.getProcessCpuLoad();
        	}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        new Output().createOutput(branchAndBoundSolution,outputName);
//        
//
//        if (useVisualisation){
//        	
//        	Schedule branchAndBoundSolution=new BranchAndBoundAlgorithm().schedule(taskGraph);
//        	TableThreader tt = new TableThreader(branchAndBoundSolution);
//        	tt.execute();
//        	new Output().createOutput(branchAndBoundSolution,outputName);
//        	
//        } else {
//        	 startTime = System.currentTimeMillis();
//        	 
//        	 Algorithm alg=new BranchAndBoundAlgorithm();
//        	 
//        	 timer.schedule(guiUpdater);
//        	 
//        	 Schedule solution=alg.schedule(taskGraph);
//        	 
//             Schedule branchAndBoundSolution=new BranchAndBoundAlgorithm().schedule(taskGraph);
//             endTime   = System.currentTimeMillis();
//             totalTime = endTime - startTime;
//             System.out.println(totalTime);
//             new Output().createOutput(branchAndBoundSolution,outputName);
//             VFrame frame = VFrame.getInstance();
//             frame.printStuff();
//        }

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
    
    public static double getProcessCpuLoad() throws Exception {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision
       
        double temp = (value * 1000) / 10.0;
        System.out.println(temp);
        return (temp);
    }
    
    
}
