import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Output {

    //String fileName = "src/test2.dot";
	//String fileName = Main.fileName;
    public void createOutput(Schedule solution, String outputName, Input input){

        //Get created graph from input dot file
    	Graph graph = input.getInputG();
    	for (int i = 0; i<solution.getTaskStartTimes().length; i++) {
    		int time = solution.getTaskStartTimes()[i];
			int processor = solution.getTaskProcessors()[i];
			int task = i;
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
            //FileWriter  writer2 = new FileWriter (new File("src/output.dot"));
            FileWriter  writer2 = new FileWriter (new File(outputName));
            writer2.write(temp);

            writer2.close();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void formatAttributes(Graph graph, int task, int processor, int time){
    	graph.getNode(task).addAttribute("Processor", processor + 1);
		graph.getNode(task).addAttribute("Start", time);
		graph.getNode(task).removeAttribute("ui.style");
//		graph.getNode(task).removeAttribute("ui.label");
//		graph.getNode(task).removeAttribute("ui.class");
//		graph.getNode(task).removeAttribute("ui.stylesheet");
    }
}
