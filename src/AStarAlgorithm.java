import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Hanzhi on 7/08/2016.
 */
public class AStarAlgorithm implements Algorithm{
    /**
     * Schedule tasks for the given task graph
     * @param taskGraph The graph of tasks to
     * @return
     */
    @Override
    public Schedule schedule(Graph taskGraph){
        PriorityQueue<Schedule> queue=new PriorityQueue<>();
        queue.add(Schedule.getEmptySchedule());
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
