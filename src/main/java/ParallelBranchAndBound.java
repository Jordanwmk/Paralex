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
//####[23]####
    public ParallelBranchAndBound(int coresToRunOn) {//####[23]####
        this(coresToRunOn, false);//####[24]####
    }//####[25]####
//####[27]####
    public ParallelBranchAndBound(int coresToRunOn, boolean useVisualisation) {//####[27]####
        this.coresToRunOn = coresToRunOn;//####[28]####
    }//####[29]####
//####[32]####
    @Override//####[32]####
    public Schedule schedule(TaskGraph taskGraph) {//####[32]####
        processingQueue.add(Schedule.getEmptySchedule(taskGraph));//####[34]####
        for (int i = 0; i < startingIterations; i++) //####[35]####
        {//####[35]####
            if (processingQueue.isEmpty()) //####[36]####
            {//####[36]####
                break;//####[37]####
            }//####[38]####
            Schedule schedule = processingQueue.remove();//####[40]####
            List<Schedule> children = schedule.generateChildren();//####[41]####
            if (children.isEmpty()) //####[43]####
            {//####[43]####
                if (schedule.getTotalTime() < currentBestTime) //####[44]####
                {//####[44]####
                    currentBest = schedule;//####[45]####
                    currentBestTime = schedule.getTotalTime();//####[46]####
                }//####[47]####
            }//####[48]####
            processingQueue.addAll(children);//####[50]####
        }//####[51]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[55]####
        for (int i = 0; i < coresToRunOn; i++) //####[56]####
        {//####[56]####
            TaskID id = startWorker(i);//####[57]####
            g.add(id);//####[58]####
        }//####[59]####
        try {//####[60]####
            g.waitTillFinished();//####[61]####
        } catch (Exception e3) {//####[62]####
            e3.printStackTrace();//####[63]####
        }//####[64]####
        return currentBest;//####[75]####
    }//####[76]####
//####[78]####
    public Schedule getCurrentBest() {//####[78]####
        synchronized (this) {//####[79]####
            return currentBest;//####[80]####
        }//####[81]####
    }//####[82]####
//####[84]####
    public boolean isDone() {//####[84]####
        return false;//####[85]####
    }//####[86]####
//####[88]####
    class Worker {//####[88]####
//####[88]####
        /*  ParaTask helper method to access private/protected slots *///####[88]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[88]####
            if (m.getParameterTypes().length == 0)//####[88]####
                m.invoke(instance);//####[88]####
            else if ((m.getParameterTypes().length == 1))//####[88]####
                m.invoke(instance, arg);//####[88]####
            else //####[88]####
                m.invoke(instance, arg, interResult);//####[88]####
        }//####[88]####
//####[89]####
        Schedule localCurrentBest;//####[89]####
//####[90]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[90]####
//####[91]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[91]####
//####[93]####
        int id;//####[93]####
//####[94]####
        int schedulesTraversed = 0;//####[94]####
//####[95]####
        int schedulesCompleted = 0;//####[95]####
//####[97]####
        public Worker(int id) {//####[97]####
            this.id = id;//####[98]####
        }//####[99]####
//####[101]####
        public void run() {//####[101]####
            Schedule scheduleWeAreCurrentlyAt;//####[102]####
            while (true) //####[103]####
            {//####[103]####
                while (!localStack.isEmpty()) //####[105]####
                {//####[105]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[106]####
                    schedulesTraversed++;//####[108]####
                    if (schedulesTraversed > globalReadFrequency) //####[109]####
                    {//####[109]####
                        schedulesTraversed = 0;//####[110]####
                        updateLocalBest();//####[111]####
                    }//####[112]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[115]####
                    {//####[115]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[116]####
                        for (Schedule s : childNodes) //####[119]####
                        {//####[119]####
                            if (s.getEstimate() < localCurrentBestTime) //####[120]####
                            {//####[120]####
                                localStack.push(s);//####[121]####
                            }//####[122]####
                        }//####[123]####
                        if (childNodes.isEmpty()) //####[125]####
                        {//####[125]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[126]####
                            {//####[126]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[127]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[128]####
                                schedulesCompleted++;//####[129]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[130]####
                                {//####[130]####
                                    schedulesCompleted = 0;//####[131]####
                                    updateGlobalBest();//####[132]####
                                    updateLocalBest();//####[133]####
                                }//####[134]####
                            }//####[135]####
                        }//####[136]####
                    }//####[137]####
                }//####[138]####
                Schedule nextSchedule = getFromSharedQueue();//####[141]####
                if (nextSchedule == null) //####[143]####
                {//####[143]####
                    updateGlobalBest();//####[144]####
                    return;//####[145]####
                } else {//####[146]####
                    localStack.push(nextSchedule);//####[147]####
                }//####[148]####
            }//####[149]####
        }//####[150]####
//####[152]####
        private Schedule getFromSharedQueue() {//####[152]####
            synchronized (ParallelBranchAndBound.this) {//####[153]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[154]####
                {//####[154]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[155]####
                } else {//####[156]####
                    return null;//####[157]####
                }//####[158]####
            }//####[159]####
        }//####[160]####
//####[165]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[165]####
        private void updateLocalBest() {//####[165]####
            synchronized (ParallelBranchAndBound.this) {//####[166]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[167]####
                {//####[167]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[168]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[169]####
                }//####[170]####
            }//####[171]####
        }//####[172]####
//####[177]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[177]####
        private void updateGlobalBest() {//####[177]####
            synchronized (ParallelBranchAndBound.this) {//####[178]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[179]####
                {//####[179]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[180]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[181]####
                }//####[182]####
            }//####[183]####
        }//####[184]####
    }//####[184]####
//####[187]####
    private static volatile Method __pt__startWorker_int_method = null;//####[187]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[187]####
        if (__pt__startWorker_int_method == null) {//####[187]####
            try {//####[187]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[187]####
                    int.class//####[187]####
                });//####[187]####
            } catch (Exception e) {//####[187]####
                e.printStackTrace();//####[187]####
            }//####[187]####
        }//####[187]####
    }//####[187]####
    public TaskID<Void> startWorker(int id) {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return startWorker(id, new TaskInfo());//####[187]####
    }//####[187]####
    public TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__startWorker_int_method == null) {//####[187]####
            __pt__startWorker_int_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setParameters(id);//####[187]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    public TaskID<Void> startWorker(TaskID<Integer> id) {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return startWorker(id, new TaskInfo());//####[187]####
    }//####[187]####
    public TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__startWorker_int_method == null) {//####[187]####
            __pt__startWorker_int_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setTaskIdArgIndexes(0);//####[187]####
        taskinfo.addDependsOn(id);//####[187]####
        taskinfo.setParameters(id);//####[187]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return startWorker(id, new TaskInfo());//####[187]####
    }//####[187]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__startWorker_int_method == null) {//####[187]####
            __pt__startWorker_int_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setParameters(id);//####[187]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    public void __pt__startWorker(int id) {//####[187]####
        new Worker(id).run();//####[188]####
    }//####[189]####
//####[189]####
}//####[189]####
