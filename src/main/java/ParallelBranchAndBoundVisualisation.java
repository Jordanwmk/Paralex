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
            processingQueue.addAll(processingQueue.remove().generateChildren());//####[39]####
        }//####[40]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[44]####
        for (int i = 0; i < coresToRunOn; i++) //####[45]####
        {//####[45]####
            TaskID id = startWorker(i);//####[46]####
            g.add(id);//####[47]####
        }//####[48]####
        try {//####[49]####
            g.waitTillFinished();//####[50]####
        } catch (Exception e3) {//####[51]####
            e3.printStackTrace();//####[52]####
        }//####[53]####
        done = true;//####[64]####
        return currentBest;//####[65]####
    }//####[66]####
//####[68]####
    public Schedule getCurrentBest() {//####[68]####
        synchronized (this) {//####[69]####
            return currentBest;//####[70]####
        }//####[71]####
    }//####[72]####
//####[74]####
    public boolean isDone() {//####[74]####
        return done;//####[75]####
    }//####[76]####
//####[78]####
    class Worker {//####[78]####
//####[78]####
        /*  ParaTask helper method to access private/protected slots *///####[78]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[78]####
            if (m.getParameterTypes().length == 0)//####[78]####
                m.invoke(instance);//####[78]####
            else if ((m.getParameterTypes().length == 1))//####[78]####
                m.invoke(instance, arg);//####[78]####
            else //####[78]####
                m.invoke(instance, arg, interResult);//####[78]####
        }//####[78]####
//####[79]####
        Schedule localCurrentBest;//####[79]####
//####[80]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[80]####
//####[81]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[81]####
//####[83]####
        int id;//####[83]####
//####[84]####
        int schedulesTraversed = 0;//####[84]####
//####[85]####
        int schedulesCompleted = 0;//####[85]####
//####[87]####
        public Worker(int id) {//####[87]####
            this.id = id;//####[88]####
        }//####[89]####
//####[91]####
        public void run() {//####[91]####
            Schedule scheduleWeAreCurrentlyAt;//####[92]####
            while (true) //####[93]####
            {//####[93]####
                while (!localStack.isEmpty()) //####[95]####
                {//####[95]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[96]####
                    if (useVisualisation) //####[98]####
                    {//####[98]####
                        VFrame frame = VFrame.getInstance();//####[99]####
                        if (scheduleWeAreCurrentlyAt.getTask() != -1) //####[100]####
                        {//####[100]####
                            frame.incrementTask(scheduleWeAreCurrentlyAt.getTask(), id);//####[101]####
                        }//####[102]####
                    }//####[103]####
                    schedulesTraversed++;//####[105]####
                    if (schedulesTraversed > globalReadFrequency) //####[106]####
                    {//####[106]####
                        schedulesTraversed = 0;//####[107]####
                        updateLocalBest();//####[108]####
                    }//####[109]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[112]####
                    {//####[112]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[113]####
                        for (Schedule s : childNodes) //####[116]####
                        {//####[116]####
                            if (s.getEstimate() < localCurrentBestTime) //####[117]####
                            {//####[117]####
                                localStack.push(s);//####[118]####
                            }//####[119]####
                        }//####[120]####
                        if (childNodes.isEmpty()) //####[122]####
                        {//####[122]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[123]####
                            {//####[123]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[124]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[125]####
                                schedulesCompleted++;//####[126]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[127]####
                                {//####[127]####
                                    schedulesCompleted = 0;//####[128]####
                                    updateGlobalBest();//####[129]####
                                    updateLocalBest();//####[130]####
                                }//####[131]####
                            }//####[132]####
                        }//####[133]####
                    }//####[134]####
                }//####[135]####
                Schedule nextSchedule = getFromSharedQueue();//####[138]####
                if (nextSchedule == null) //####[140]####
                {//####[140]####
                    updateGlobalBest();//####[141]####
                    return;//####[142]####
                } else {//####[143]####
                    localStack.push(nextSchedule);//####[144]####
                }//####[145]####
            }//####[146]####
        }//####[147]####
//####[149]####
        private Schedule getFromSharedQueue() {//####[149]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[150]####
                if (!ParallelBranchAndBoundVisualisation.this.processingQueue.isEmpty()) //####[151]####
                {//####[151]####
                    return ParallelBranchAndBoundVisualisation.this.processingQueue.remove();//####[152]####
                } else {//####[153]####
                    return null;//####[154]####
                }//####[155]####
            }//####[156]####
        }//####[157]####
//####[162]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[162]####
        private void updateLocalBest() {//####[162]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[163]####
                if (localCurrentBestTime > ParallelBranchAndBoundVisualisation.this.currentBestTime) //####[164]####
                {//####[164]####
                    localCurrentBest = ParallelBranchAndBoundVisualisation.this.currentBest;//####[165]####
                    localCurrentBestTime = ParallelBranchAndBoundVisualisation.this.currentBestTime;//####[166]####
                }//####[167]####
            }//####[168]####
        }//####[169]####
//####[174]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[174]####
        private void updateGlobalBest() {//####[174]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[175]####
                if (localCurrentBestTime < ParallelBranchAndBoundVisualisation.this.currentBestTime) //####[176]####
                {//####[176]####
                    ParallelBranchAndBoundVisualisation.this.currentBest = localCurrentBest;//####[177]####
                    ParallelBranchAndBoundVisualisation.this.currentBestTime = localCurrentBestTime;//####[178]####
                }//####[179]####
            }//####[180]####
        }//####[181]####
    }//####[181]####
//####[184]####
    private static volatile Method __pt__startWorker_int_method = null;//####[184]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[184]####
        if (__pt__startWorker_int_method == null) {//####[184]####
            try {//####[184]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[184]####
                    int.class//####[184]####
                });//####[184]####
            } catch (Exception e) {//####[184]####
                e.printStackTrace();//####[184]####
            }//####[184]####
        }//####[184]####
    }//####[184]####
    public TaskID<Void> startWorker(int id) {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return startWorker(id, new TaskInfo());//####[184]####
    }//####[184]####
    public TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__startWorker_int_method == null) {//####[184]####
            __pt__startWorker_int_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setParameters(id);//####[184]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    public TaskID<Void> startWorker(TaskID<Integer> id) {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return startWorker(id, new TaskInfo());//####[184]####
    }//####[184]####
    public TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__startWorker_int_method == null) {//####[184]####
            __pt__startWorker_int_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setTaskIdArgIndexes(0);//####[184]####
        taskinfo.addDependsOn(id);//####[184]####
        taskinfo.setParameters(id);//####[184]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return startWorker(id, new TaskInfo());//####[184]####
    }//####[184]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__startWorker_int_method == null) {//####[184]####
            __pt__startWorker_int_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setParameters(id);//####[184]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    public void __pt__startWorker(int id) {//####[184]####
        new Worker(id).run();//####[185]####
    }//####[186]####
//####[186]####
}//####[186]####
