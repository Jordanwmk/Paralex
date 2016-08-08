import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.IOException;

/**
 * Created by Hanzhi on 1/08/2016.
 */
public class Output {

    public void createOutput (){

        Graph graph = Input.getInputG();

        FileSinkDOT writer = new FileSinkDOT(true);
        writer.setDirected(true);

        try{
            writer.writeAll(graph, "src/test2.dot");
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
