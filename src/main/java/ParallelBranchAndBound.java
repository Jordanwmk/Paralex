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
public class ParallelBranchAndBound implements Algorithm {//####[12]####
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
//####[24]####
    public ParallelBranchAndBound(int coresToRunOn) {//####[24]####
        this(coresToRunOn, false);//####[25]####
    }//####[26]####
//####[28]####
    public ParallelBranchAndBound(int coresToRunOn, boolean useVisualisation) {//####[28]####
        this.coresToRunOn = coresToRunOn;//####[29]####
        this.useVisualisation = useVisualisation;//####[30]####
    }//####[31]####
//####[34]####
    @Override//####[34]####
    public Schedule schedule(TaskGraph taskGraph) {//####[34]####
        processingQueue.add(Schedule.getEmptySchedule(taskGraph));//####[36]####
        for (int i = 0; i < startingIterations; i++) //####[37]####
        {//####[37]####
            processingQueue.addAll(processingQueue.remove().generateChildren());//####[38]####
        }//####[39]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[43]####
        for (int i = 0; i < coresToRunOn; i++) //####[44]####
        {//####[44]####
            TaskID id = startWorker(i);//####[45]####
            g.add(id);//####[46]####
        }//####[47]####
        try {//####[48]####
            g.waitTillFinished();//####[49]####
        } catch (Exception e3) {//####[50]####
            e3.printStackTrace();//####[51]####
        }//####[52]####
        return currentBest;//####[63]####
    }//####[64]####
//####[66]####
    public Schedule getCurrentBest() {//####[66]####
        synchronized (this) {//####[67]####
            return currentBest;//####[68]####
        }//####[69]####
    }//####[70]####
//####[72]####
    class Worker {//####[72]####
//####[72]####
        /*  ParaTask helper method to access private/protected slots *///####[72]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[72]####
            if (m.getParameterTypes().length == 0)//####[72]####
                m.invoke(instance);//####[72]####
            else if ((m.getParameterTypes().length == 1))//####[72]####
                m.invoke(instance, arg);//####[72]####
            else //####[72]####
                m.invoke(instance, arg, interResult);//####[72]####
        }//####[72]####
//####[73]####
        Schedule localCurrentBest;//####[73]####
//####[74]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[74]####
//####[75]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[75]####
//####[77]####
        int id;//####[77]####
//####[78]####
        int schedulesTraversed = 0;//####[78]####
//####[79]####
        int schedulesCompleted = 0;//####[79]####
//####[81]####
        public Worker(int id) {//####[81]####
            this.id = id;//####[82]####
        }//####[83]####
//####[85]####
        public void run() {//####[85]####
            Schedule scheduleWeAreCurrentlyAt;//####[86]####
            while (true) //####[87]####
            {//####[87]####
                while (!localStack.isEmpty()) //####[89]####
                {//####[89]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[90]####
                    if (useVisualisation) //####[92]####
                    {//####[92]####
                        VFrame frame = VFrame.getInstance();//####[93]####
                        if (scheduleWeAreCurrentlyAt.getTask() != -1) //####[94]####
                        {//####[94]####
                            frame.incrementTask(scheduleWeAreCurrentlyAt.getTask(), id);//####[95]####
                        }//####[96]####
                    }//####[97]####
                    schedulesTraversed++;//####[99]####
                    if (schedulesTraversed > globalReadFrequency) //####[100]####
                    {//####[100]####
                        schedulesTraversed = 0;//####[101]####
                        updateLocalBest();//####[102]####
                    }//####[103]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[106]####
                    {//####[106]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[107]####
                        for (Schedule s : childNodes) //####[110]####
                        {//####[110]####
                            if (s.getEstimate() < localCurrentBestTime) //####[111]####
                            {//####[111]####
                                localStack.push(s);//####[112]####
                            }//####[113]####
                        }//####[114]####
                        if (childNodes.isEmpty()) //####[116]####
                        {//####[116]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[117]####
                            {//####[117]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[118]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[119]####
                                schedulesCompleted++;//####[120]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[121]####
                                {//####[121]####
                                    schedulesCompleted = 0;//####[122]####
                                    updateGlobalBest();//####[123]####
                                    updateLocalBest();//####[124]####
                                }//####[125]####
                            }//####[126]####
                        }//####[127]####
                    }//####[128]####
                }//####[129]####
                Schedule nextSchedule = getFromSharedQueue();//####[132]####
                if (nextSchedule == null) //####[134]####
                {//####[134]####
                    updateGlobalBest();//####[135]####
                    return;//####[136]####
                } else {//####[137]####
                    localStack.push(nextSchedule);//####[138]####
                }//####[139]####
            }//####[140]####
        }//####[141]####
//####[143]####
        private Schedule getFromSharedQueue() {//####[143]####
            synchronized (ParallelBranchAndBound.this) {//####[144]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[145]####
                {//####[145]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[146]####
                } else {//####[147]####
                    return null;//####[148]####
                }//####[149]####
            }//####[150]####
        }//####[151]####
//####[156]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[156]####
        private void updateLocalBest() {//####[156]####
            synchronized (ParallelBranchAndBound.this) {//####[157]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[158]####
                {//####[158]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[159]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[160]####
                }//####[161]####
            }//####[162]####
        }//####[163]####
//####[168]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[168]####
        private void updateGlobalBest() {//####[168]####
            synchronized (ParallelBranchAndBound.this) {//####[169]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[170]####
                {//####[170]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[171]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[172]####
                }//####[173]####
            }//####[174]####
        }//####[175]####
    }//####[175]####
//####[178]####
    private static volatile Method __pt__startWorker_int_method = null;//####[178]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[178]####
        if (__pt__startWorker_int_method == null) {//####[178]####
            try {//####[178]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[178]####
                    int.class//####[178]####
                });//####[178]####
            } catch (Exception e) {//####[178]####
                e.printStackTrace();//####[178]####
            }//####[178]####
        }//####[178]####
    }//####[178]####
    public TaskID<Void> startWorker(int id) {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return startWorker(id, new TaskInfo());//####[178]####
    }//####[178]####
    public TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__startWorker_int_method == null) {//####[178]####
            __pt__startWorker_int_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setParameters(id);//####[178]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    public TaskID<Void> startWorker(TaskID<Integer> id) {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return startWorker(id, new TaskInfo());//####[178]####
    }//####[178]####
    public TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__startWorker_int_method == null) {//####[178]####
            __pt__startWorker_int_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setTaskIdArgIndexes(0);//####[178]####
        taskinfo.addDependsOn(id);//####[178]####
        taskinfo.setParameters(id);//####[178]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return startWorker(id, new TaskInfo());//####[178]####
    }//####[178]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__startWorker_int_method == null) {//####[178]####
            __pt__startWorker_int_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setParameters(id);//####[178]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    public void __pt__startWorker(int id) {//####[178]####
        new Worker(id).run();//####[179]####
    }//####[180]####
//####[180]####
}//####[180]####
