import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		// //Check for what options are enabled in executing the jar.
		//

		// Default values when no additional option is added.
		int numCores = 1;
		String fileName = args[0];
		int numProcessors = Integer.parseInt(args[1]);
		boolean useVisualisation = false;

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
		if (numCores == 1) {
			algorithm = new BranchAndBoundAlgorithm(useVisualisation);
		} else {
			algorithm = new ParallelBranchAndBound(numCores,useVisualisation);
		}
		if (useVisualisation) {
			VFrame frame = new VFrame(numCores, fileName, numProcessors);
			TableThreader tt = new TableThreader(algorithm, frame);

			tt.execute();
		}
		Schedule branchAndBoundSolution = algorithm.schedule(taskGraph);
		endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		new Output().createOutput(branchAndBoundSolution, outputName,
				taskGraph.getInput());

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

		// startTime = System.currentTimeMillis();
		// Schedule aStarSolution=new AStarAlgorithm().schedule(taskGraph);
		// endTime = System.currentTimeMillis();
		// totalTime = endTime - startTime;
		// System.out.println(aStarSolution);
		// System.out.println("A* solution time: "+aStarSolution.getTotalTime());
		// System.out.println("A* runtime: " + totalTime + "ms");
		// System.out.println();

		// InputParser lol = new InputParser(filename);
		// Schedule = scheduler.schedule(Graph.getGraph);
		// Outputparser.print(schedule);

	}

}
