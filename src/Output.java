import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkDOT;

/**
 * Created by Jordan on 8/08/2016.
 */
public class Output {

    String fileName = "src/test2.dot";

    public void createOutput(Schedule aStarSolution){

    	while (aStarSolution.getTask() != -1){
    		
    		int task = aStarSolution.getTask();
    		int processor = aStarSolution.getProcessor();
    		int time = aStarSolution.getTime();
    		Graph graph = Input.getInputG();
    		
    		formatAttributes(graph, task, processor, time);
    		aStarSolution = aStarSolution.getParent();
    	}
    	
    	
        //Get created graph from input dot file
        Graph graph = Input.getInputG();

        //Create the dot file writer for output. Constructor parameter "true" indicates that graph is directed
        FileSinkDOT writer = new FileSinkDOT(true);

        try{
            writer.writeAll(graph, fileName);
        } catch (Exception e){
            e.printStackTrace();
        }


    }
    
    public void formatAttributes(Graph graph, int task, int processor, int time){
    	graph.getNode(task).addAttribute("Processor", processor + 1);
		graph.getNode(task).addAttribute("Start", time);
		graph.getNode(task).removeAttribute("ui.style");
		graph.getNode(task).removeAttribute("ui.label");
    }
}
