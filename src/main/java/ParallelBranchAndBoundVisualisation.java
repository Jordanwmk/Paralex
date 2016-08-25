import java.util.List;//####[1]####
import java.util.Queue;//####[2]####
import java.util.ArrayDeque;//####[3]####
import java.util.Stack;//####[4]####
import java.util.concurrent.ExecutionException;//####[5]####
import pt.runtime.*;//####[7]####
import java.util.EmptyStackException;//####[8]####
import java.lang.Thread;//####[9]####
//####[9]####
//-- ParaTask related imports//####[9]####
import pt.runtime.*;//####[9]####
import java.util.concurrent.ExecutionException;//####[9]####
import java.util.concurrent.locks.*;//####[9]####
import java.lang.reflect.*;//####[9]####
import pt.runtime.GuiThread;//####[9]####
import java.util.concurrent.BlockingQueue;//####[9]####
import java.util.ArrayList;//####[9]####
import java.util.List;//####[9]####
//####[9]####
public class ParallelBranchAndBoundVisualisation implements Algorithm {//####[12]####
    static{ParaTask.init();}//####[12]####
    /*  ParaTask helper method to access private/protected slots *///####[12]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[12]####
        if (m.getParameterTypes().length == 0)//####[12]####
            m.invoke(instance);//####[12]####
        else if ((m.getParameterTypes().length == 1))//####[12]####
            m.invoke(instance, arg);//####[12]####
        else //####[12]####
            m.invoke(instance, arg, interResult);//####[12]####
    }//####[12]####
//####[13]####
    final int globalReadFrequency = 10000;//####[13]####
//####[14]####
    final int globalUpdateFrequency = 0;//####[14]####
//####[15]####
    final int startingIterations = 50;//####[15]####
//####[17]####
    Queue<Schedule> processingQueue = new ArrayDeque<Schedule>();//####[17]####
//####[18]####
    int threadsCurrentlyRunning = 0;//####[18]####
//####[19]####
    Schedule currentBest;//####[19]####
//####[20]####
    int currentBestTime = Integer.MAX_VALUE;//####[20]####
//####[21]####
    int coresToRunOn = 0;//####[21]####
//####[22]####
    boolean useVisualisation;//####[22]####
//####[23]####
    boolean done = false;//####[23]####
//####[25]####
    public ParallelBranchAndBoundVisualisation(int coresToRunOn) {//####[25]####
        this(coresToRunOn, false);//####[26]####
    }//####[27]####
//####[29]####
    public ParallelBranchAndBoundVisualisation(int coresToRunOn, boolean useVisualisation) {//####[29]####
        this.coresToRunOn = coresToRunOn;//####[30]####
        this.useVisualisation = useVisualisation;//####[31]####
    }//####[32]####
//####[35]####
    @Override//####[35]####
    public Schedule schedule(TaskGraph taskGraph) {//####[35]####
        processingQueue.add(Schedule.getEmptySchedule(taskGraph));//####[37]####
        for (int i = 0; i < startingIterations; i++) //####[38]####
        {//####[38]####
            if (processingQueue.isEmpty()) //####[39]####
            {//####[39]####
                break;//####[40]####
            }//####[41]####
            Schedule schedule = processingQueue.remove();//####[43]####
            List<Schedule> children = schedule.generateChildren();//####[44]####
            if (children.isEmpty()) //####[46]####
            {//####[46]####
                if (schedule.getTotalTime() < currentBestTime) //####[47]####
                {//####[47]####
                    currentBest = schedule;//####[48]####
                    currentBestTime = schedule.getTotalTime();//####[49]####
                }//####[50]####
            }//####[51]####
            processingQueue.addAll(children);//####[53]####
        }//####[54]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[58]####
        for (int i = 0; i < coresToRunOn; i++) //####[59]####
        {//####[59]####
            TaskID id = startWorker(i);//####[60]####
            g.add(id);//####[61]####
        }//####[62]####
        try {//####[63]####
            g.waitTillFinished();//####[64]####
        } catch (Exception e3) {//####[65]####
            e3.printStackTrace();//####[66]####
        }//####[67]####
        done = true;//####[78]####
        return currentBest;//####[79]####
    }//####[80]####
//####[82]####
    public Schedule getCurrentBest() {//####[82]####
        synchronized (this) {//####[83]####
            return currentBest;//####[84]####
        }//####[85]####
    }//####[86]####
//####[88]####
    public boolean isDone() {//####[88]####
        return done;//####[89]####
    }//####[90]####
//####[92]####
    class Worker {//####[92]####
//####[92]####
        /*  ParaTask helper method to access private/protected slots *///####[92]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[92]####
            if (m.getParameterTypes().length == 0)//####[92]####
                m.invoke(instance);//####[92]####
            else if ((m.getParameterTypes().length == 1))//####[92]####
                m.invoke(instance, arg);//####[92]####
            else //####[92]####
                m.invoke(instance, arg, interResult);//####[92]####
        }//####[92]####
//####[93]####
        Schedule localCurrentBest;//####[93]####
//####[94]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[94]####
//####[95]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[95]####
//####[97]####
        int id;//####[97]####
//####[98]####
        int schedulesTraversed = 0;//####[98]####
//####[99]####
        int schedulesCompleted = 0;//####[99]####
//####[101]####
        public Worker(int id) {//####[101]####
            this.id = id;//####[102]####
        }//####[103]####
//####[105]####
        public void run() {//####[105]####
            Schedule scheduleWeAreCurrentlyAt;//####[106]####
            while (true) //####[107]####
            {//####[107]####
                while (!localStack.isEmpty()) //####[109]####
                {//####[109]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[110]####
                    if (useVisualisation) //####[112]####
                    {//####[112]####
                        VFrame frame = VFrame.getInstance();//####[113]####
                        if (scheduleWeAreCurrentlyAt.getTask() != -1) //####[114]####
                        {//####[114]####
                            frame.incrementTask(scheduleWeAreCurrentlyAt.getTask(), id);//####[117]####
                        }//####[118]####
                    }//####[119]####
                    schedulesTraversed++;//####[121]####
                    if (schedulesTraversed > globalReadFrequency) //####[122]####
                    {//####[122]####
                        schedulesTraversed = 0;//####[123]####
                        updateLocalBest();//####[124]####
                    }//####[125]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[128]####
                    {//####[128]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[129]####
                        for (Schedule s : childNodes) //####[132]####
                        {//####[132]####
                            if (s.getEstimate() < localCurrentBestTime) //####[133]####
                            {//####[133]####
                                localStack.push(s);//####[134]####
                            }//####[135]####
                        }//####[136]####
                        if (childNodes.isEmpty()) //####[138]####
                        {//####[138]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[139]####
                            {//####[139]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[140]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[141]####
                                schedulesCompleted++;//####[142]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[143]####
                                {//####[143]####
                                    schedulesCompleted = 0;//####[144]####
                                    updateGlobalBest();//####[145]####
                                    updateLocalBest();//####[146]####
                                }//####[147]####
                            }//####[148]####
                        }//####[149]####
                    }//####[150]####
                }//####[151]####
                Schedule nextSchedule = getFromSharedQueue();//####[154]####
                if (nextSchedule == null) //####[156]####
                {//####[156]####
                    updateGlobalBest();//####[157]####
                    return;//####[158]####
                } else {//####[159]####
                    localStack.push(nextSchedule);//####[160]####
                }//####[161]####
            }//####[162]####
        }//####[163]####
//####[165]####
        private Schedule getFromSharedQueue() {//####[165]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[166]####
                if (!ParallelBranchAndBoundVisualisation.this.processingQueue.isEmpty()) //####[167]####
                {//####[167]####
                    return ParallelBranchAndBoundVisualisation.this.processingQueue.remove();//####[168]####
                } else {//####[169]####
                    return null;//####[170]####
                }//####[171]####
            }//####[172]####
        }//####[173]####
//####[178]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[178]####
        private void updateLocalBest() {//####[178]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[179]####
                if (localCurrentBestTime > ParallelBranchAndBoundVisualisation.this.currentBestTime) //####[180]####
                {//####[180]####
                    localCurrentBest = ParallelBranchAndBoundVisualisation.this.currentBest;//####[181]####
                    localCurrentBestTime = ParallelBranchAndBoundVisualisation.this.currentBestTime;//####[182]####
                }//####[183]####
            }//####[184]####
        }//####[185]####
//####[190]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[190]####
        private void updateGlobalBest() {//####[190]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[191]####
                if (localCurrentBestTime < ParallelBranchAndBoundVisualisation.this.currentBestTime) //####[192]####
                {//####[192]####
                    ParallelBranchAndBoundVisualisation.this.currentBest = localCurrentBest;//####[193]####
                    ParallelBranchAndBoundVisualisation.this.currentBestTime = localCurrentBestTime;//####[194]####
                }//####[195]####
            }//####[196]####
        }//####[197]####
    }//####[197]####
//####[200]####
    private static volatile Method __pt__startWorker_int_method = null;//####[200]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[200]####
        if (__pt__startWorker_int_method == null) {//####[200]####
            try {//####[200]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[200]####
                    int.class//####[200]####
                });//####[200]####
            } catch (Exception e) {//####[200]####
                e.printStackTrace();//####[200]####
            }//####[200]####
        }//####[200]####
    }//####[200]####
    public TaskID<Void> startWorker(int id) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return startWorker(id, new TaskInfo());//####[200]####
    }//####[200]####
    public TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__startWorker_int_method == null) {//####[200]####
            __pt__startWorker_int_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setParameters(id);//####[200]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[200]####
        taskinfo.setInstance(this);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    public TaskID<Void> startWorker(TaskID<Integer> id) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return startWorker(id, new TaskInfo());//####[200]####
    }//####[200]####
    public TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__startWorker_int_method == null) {//####[200]####
            __pt__startWorker_int_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setTaskIdArgIndexes(0);//####[200]####
        taskinfo.addDependsOn(id);//####[200]####
        taskinfo.setParameters(id);//####[200]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[200]####
        taskinfo.setInstance(this);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[200]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[200]####
        return startWorker(id, new TaskInfo());//####[200]####
    }//####[200]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[200]####
        // ensure Method variable is set//####[200]####
        if (__pt__startWorker_int_method == null) {//####[200]####
            __pt__startWorker_int_ensureMethodVarSet();//####[200]####
        }//####[200]####
        taskinfo.setQueueArgIndexes(0);//####[200]####
        taskinfo.setIsPipeline(true);//####[200]####
        taskinfo.setParameters(id);//####[200]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[200]####
        taskinfo.setInstance(this);//####[200]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[200]####
    }//####[200]####
    public void __pt__startWorker(int id) {//####[200]####
        new Worker(id).run();//####[201]####
    }//####[202]####
//####[202]####
}//####[202]####
