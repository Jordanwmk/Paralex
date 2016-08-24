import java.io.IOException;

import javax.sound.sampled.Clip;

public class Main {

	public static void main(String[] args) {
		// //Check for what options are enabled in executing the jar.
		//

		// Default values when no additional option is added.
		int numCores = 1;
		String fileName = args[0];
		int numProcessors = Integer.parseInt(args[1]);
		boolean useVisualisation = false;
		boolean darude = false;
		
		String[] splitName = fileName.split(".dot");
		String outputName = splitName[0] + "-output.dot";

		// Check when additional options are added.
		if (args.length > 2) {
			for (int i = 2; i < args.length; i++) {
				if (args[i].equals("-p")) {
					numCores = Integer.parseInt(args[i + 1]);
				} else if (args[i].equals("-v")) {
					useVisualisation = true;
				} else if (args[i].equals("-o")) {
					outputName = args[i + 1];
					if (!outputName.contains(".dot")) { // Checking if user has
														// specified file
														// extension as .dot
						outputName = outputName + ".dot";
					}
				} else if(args[i].equals("-d")) {
					darude=true;
				}
			}
		}

		// if (useVisualisation){
		// VFrame frame = new VFrame(threadsUsed, fileName, numProcessors);
		// }

		TaskGraph taskGraph;
		try {
			taskGraph = new TaskGraph(fileName, numProcessors, useVisualisation);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOException while reading graph file");
			return;
		}

		long startTime, endTime, totalTime;
		startTime = System.currentTimeMillis();
		Algorithm algorithm;
		Clip sandstorm = null;
		VFrame frame = null;
		if (useVisualisation) {
			if (numCores == 1) {
				algorithm = new BranchAndBoundVisualisation(useVisualisation);
			} else {
				algorithm = new ParallelBranchAndBoundVisualisation(numCores,useVisualisation);
			}
		} else {
			if (numCores == 1) {
				algorithm = new BranchAndBoundAlgorithm();
			} else {
				algorithm = new ParallelBranchAndBound(numCores,useVisualisation);
			}
		}
//		if (numCores == 1) {
//			algorithm = new BranchAndBoundVisualisation(useVisualisation);
//		} else {
//			algorithm = new ParallelBranchAndBound(numCores,useVisualisation);
//		}
		
//		Clip sandstorm = null;
		
		if (useVisualisation) {
			//play the music
			if(darude){
				sandstorm=VFrame.getInstance().playSound("src/main/resources/sandstorm.wav");
			}
			
			frame = new VFrame(numCores, fileName, numProcessors);
			TableThreader tt = new TableThreader(algorithm, frame);

			tt.execute();
		}
		Schedule branchAndBoundSolution = algorithm.schedule(taskGraph);
		endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		
		VFrame.getInstance().playSound("src/main/resources/paralex.wav");
		if(useVisualisation && darude){
			sandstorm.stop();
		}
		if (useVisualisation) {
			new Output(useVisualisation).createOutput(branchAndBoundSolution, outputName,
					frame.getTaskGraphList().get(numCores));
		} else {
			new Output().createOutput(branchAndBoundSolution, outputName,
					taskGraph.getInput().getInputG());
			
		}


		//
		//
		// if (useVisualisation){
		//
		// Schedule branchAndBoundSolution=new
		// BranchAndBoundAlgorithm().schedule(taskGraph);
		// TableThreader tt = new TableThreader(branchAndBoundSolution);
		// tt.execute();
		// new Output().createOutput(branchAndBoundSolution,outputName);
		//
		// } else {
		// startTime = System.currentTimeMillis();
		//
		// Algorithm alg=new BranchAndBoundAlgorithm();
		//
		// timer.schedule(guiUpdater);
		//
		// Schedule solution=alg.schedule(taskGraph);
		//
		// Schedule branchAndBoundSolution=new
		// BranchAndBoundAlgorithm().schedule(taskGraph);
		// endTime = System.currentTimeMillis();
		// totalTime = endTime - startTime;
		// System.out.println(totalTime);
		// new Output().createOutput(branchAndBoundSolution,outputName);
		// VFrame frame = VFrame.getInstance();
		// frame.printStuff();
		// }



    }
    
    
    

}
