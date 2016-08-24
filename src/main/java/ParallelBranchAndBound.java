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
        this.coresToRunOn = coresToRunOn;//####[24]####
    }//####[25]####
//####[28]####
    @Override//####[28]####
    public Schedule schedule(TaskGraph taskGraph) {//####[28]####
        processingQueue.add(Schedule.getEmptySchedule(taskGraph));//####[30]####
        for (int i = 0; i < startingIterations; i++) //####[31]####
        {//####[31]####
            processingQueue.addAll(processingQueue.remove().generateChildren());//####[32]####
        }//####[33]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[37]####
        for (int i = 0; i < coresToRunOn; i++) //####[38]####
        {//####[38]####
            TaskID id = startWorker(i);//####[39]####
            g.add(id);//####[40]####
        }//####[41]####
        try {//####[42]####
            g.waitTillFinished();//####[43]####
        } catch (Exception e3) {//####[44]####
            e3.printStackTrace();//####[45]####
        }//####[46]####
        return currentBest;//####[57]####
    }//####[58]####
//####[60]####
    public Schedule getCurrentBest() {//####[60]####
        synchronized (this) {//####[61]####
            return currentBest;//####[62]####
        }//####[63]####
    }//####[64]####
//####[66]####
    class Worker {//####[66]####
//####[66]####
        /*  ParaTask helper method to access private/protected slots *///####[66]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[66]####
            if (m.getParameterTypes().length == 0)//####[66]####
                m.invoke(instance);//####[66]####
            else if ((m.getParameterTypes().length == 1))//####[66]####
                m.invoke(instance, arg);//####[66]####
            else //####[66]####
                m.invoke(instance, arg, interResult);//####[66]####
        }//####[66]####
//####[67]####
        Schedule localCurrentBest;//####[67]####
//####[68]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[68]####
//####[69]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[69]####
//####[71]####
        int id;//####[71]####
//####[72]####
        int schedulesTraversed = 0;//####[72]####
//####[73]####
        int schedulesCompleted = 0;//####[73]####
//####[75]####
        public Worker(int id) {//####[75]####
            this.id = id;//####[76]####
        }//####[77]####
//####[79]####
        public void run() {//####[79]####
            Schedule scheduleWeAreCurrentlyAt;//####[80]####
            while (true) //####[81]####
            {//####[81]####
                while (!localStack.isEmpty()) //####[83]####
                {//####[83]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[84]####
                    VFrame frame = VFrame.getInstance();//####[85]####
                    if (scheduleWeAreCurrentlyAt.getTask() != -1) //####[86]####
                    {//####[86]####
                        frame.incrementTask(scheduleWeAreCurrentlyAt.getTask(), id);//####[87]####
                    }//####[88]####
                    schedulesTraversed++;//####[89]####
                    if (schedulesTraversed > globalReadFrequency) //####[90]####
                    {//####[90]####
                        schedulesTraversed = 0;//####[91]####
                        updateLocalBest();//####[92]####
                    }//####[93]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[96]####
                    {//####[96]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[97]####
                        for (Schedule s : childNodes) //####[100]####
                        {//####[100]####
                            if (s.getEstimate() < localCurrentBestTime) //####[101]####
                            {//####[101]####
                                localStack.push(s);//####[102]####
                            }//####[103]####
                        }//####[104]####
                        if (childNodes.isEmpty()) //####[106]####
                        {//####[106]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[107]####
                            {//####[107]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[108]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[109]####
                                schedulesCompleted++;//####[110]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[111]####
                                {//####[111]####
                                    schedulesCompleted = 0;//####[112]####
                                    updateGlobalBest();//####[113]####
                                    updateLocalBest();//####[114]####
                                }//####[115]####
                            }//####[116]####
                        }//####[117]####
                    }//####[118]####
                }//####[119]####
                Schedule nextSchedule = getFromSharedQueue();//####[122]####
                if (nextSchedule == null) //####[124]####
                {//####[124]####
                    updateGlobalBest();//####[125]####
                    return;//####[126]####
                } else {//####[127]####
                    localStack.push(nextSchedule);//####[128]####
                }//####[129]####
            }//####[130]####
        }//####[131]####
//####[133]####
        private Schedule getFromSharedQueue() {//####[133]####
            synchronized (ParallelBranchAndBound.this) {//####[134]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[135]####
                {//####[135]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[136]####
                } else {//####[137]####
                    return null;//####[138]####
                }//####[139]####
            }//####[140]####
        }//####[141]####
//####[146]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[146]####
        private void updateLocalBest() {//####[146]####
            synchronized (ParallelBranchAndBound.this) {//####[147]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[148]####
                {//####[148]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[149]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[150]####
                }//####[151]####
            }//####[152]####
        }//####[153]####
//####[158]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[158]####
        private void updateGlobalBest() {//####[158]####
            synchronized (ParallelBranchAndBound.this) {//####[159]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[160]####
                {//####[160]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[161]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[162]####
                }//####[163]####
            }//####[164]####
        }//####[165]####
    }//####[165]####
//####[168]####
    private static volatile Method __pt__startWorker_int_method = null;//####[168]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[168]####
        if (__pt__startWorker_int_method == null) {//####[168]####
            try {//####[168]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[168]####
                    int.class//####[168]####
                });//####[168]####
            } catch (Exception e) {//####[168]####
                e.printStackTrace();//####[168]####
            }//####[168]####
        }//####[168]####
    }//####[168]####
    public TaskID<Void> startWorker(int id) {//####[168]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[168]####
        return startWorker(id, new TaskInfo());//####[168]####
    }//####[168]####
    public TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[168]####
        // ensure Method variable is set//####[168]####
        if (__pt__startWorker_int_method == null) {//####[168]####
            __pt__startWorker_int_ensureMethodVarSet();//####[168]####
        }//####[168]####
        taskinfo.setParameters(id);//####[168]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[168]####
        taskinfo.setInstance(this);//####[168]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[168]####
    }//####[168]####
    public TaskID<Void> startWorker(TaskID<Integer> id) {//####[168]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[168]####
        return startWorker(id, new TaskInfo());//####[168]####
    }//####[168]####
    public TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[168]####
        // ensure Method variable is set//####[168]####
        if (__pt__startWorker_int_method == null) {//####[168]####
            __pt__startWorker_int_ensureMethodVarSet();//####[168]####
        }//####[168]####
        taskinfo.setTaskIdArgIndexes(0);//####[168]####
        taskinfo.addDependsOn(id);//####[168]####
        taskinfo.setParameters(id);//####[168]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[168]####
        taskinfo.setInstance(this);//####[168]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[168]####
    }//####[168]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[168]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[168]####
        return startWorker(id, new TaskInfo());//####[168]####
    }//####[168]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[168]####
        // ensure Method variable is set//####[168]####
        if (__pt__startWorker_int_method == null) {//####[168]####
            __pt__startWorker_int_ensureMethodVarSet();//####[168]####
        }//####[168]####
        taskinfo.setQueueArgIndexes(0);//####[168]####
        taskinfo.setIsPipeline(true);//####[168]####
        taskinfo.setParameters(id);//####[168]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[168]####
        taskinfo.setInstance(this);//####[168]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[168]####
    }//####[168]####
    public void __pt__startWorker(int id) {//####[168]####
        new Worker(id).run();//####[169]####
    }//####[170]####
//####[170]####
}//####[170]####
