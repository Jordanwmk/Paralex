import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Hanzhi on 7/08/2016.
 */
public class Algorithm {
    /**
     * Schedule tasks for the given task graph
     * @param taskGraph The graph of tasks to
     * @return
     */
    public Schedule schedule(Graph taskGraph){
        PriorityQueue<Schedule> queue=new PriorityQueue<>();
        Schedule root=new Schedule(null,-1,-1,0,0,0,1, taskGraph.getEntryPoints());
        queue.add(root);
        Schedule scheduleWeAreCurrentlyAt = null;
        while(!queue.isEmpty()) {
            scheduleWeAreCurrentlyAt = queue.poll();
            List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();
            if(childNodes.isEmpty()){
                break;
            }
            queue.addAll(childNodes);
        }
        return scheduleWeAreCurrentlyAt;
    }
}
