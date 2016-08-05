import java.util.List;

/**
 * Created by Hanzhi on 1/08/2016.
 */
public class Schedule {
    private List<ScheduledTask> [] tasks;
    private int estimateTotal;
    private int idleTime;
    private boolean[] tasksComplete;
    private int nonCompletedTasks;
    private List<Integer> doableTasks;
    private Generator generator;
}
