import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkDOT;

/**
 * Created by Jordan on 8/08/2016.
 */
public class Output {

    String fileName = "src/test2.dot";

    public void createOutput(){

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
}
