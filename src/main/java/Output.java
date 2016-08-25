import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * This class is responsible for creating the output file that contains the
 * optimal schedule.
 * @author jwon223
 *
 */
public class Output {

	boolean useVisualisation = false;
	
	public Output(){
		
	}
	
	public Output(boolean useVisualisation){
		this.useVisualisation = useVisualisation;
	}
	
	/**
	 * This method writes the output file in DOT format that contains the optimal schedule
	 * 
	 * @param solution the optimal schedule
	 * @param outputName the name of the output file
	 * @param graph the task graph created from the input file
	 */
    public void createOutput(Schedule solution, String outputName, Graph graph){

    	for (int i = 0; i < solution.getTaskStartTimes().length; i++) {
    		int time = solution.getTaskStartTimes()[i];
			int processor = solution.getTaskProcessors()[i];
			int task = i;
			
			//Only include the desired attributes
    		formatAttributes(graph, task, processor, time);
    	}
    	
        //Create the dot file writer for output. Constructor parameter "true" indicates that graph is directed
        FileSinkDOT writer = new FileSinkDOT(true);

        try{
            writer.writeAll(graph, outputName);

            //Store entire file into the string
            String fileContent = new Scanner(new File(outputName)).useDelimiter("\\Z").next();

            //Remove double quotes
            String temp = fileContent.replaceAll("\"","");
            temp = temp.replaceFirst("digraph ", "digraph \"" + outputName + "\" ");

            //Overwrite old file with new contents
            FileWriter  writer2 = new FileWriter (new File(outputName));
            writer2.write(temp);

            writer2.close();

        } catch (Exception e){
            //e.printStackTrace();
        }

    }

    /**
     * This method adds the desired attributes to each node in the task graph.
     * Currently adds processor allocated and start time.
     * @param graph task graph created from input file
     * @param task current task being looked at
     * @param processor processor the task is scheduled on
     * @param time start time of the task
     */
    public void formatAttributes(Graph graph, int task, int processor, int time){
    	graph.getNode(task).addAttribute("Processor", processor + 1);
		graph.getNode(task).addAttribute("Start", time);

    }
}
