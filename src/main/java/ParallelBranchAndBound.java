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
    class Worker {//####[60]####
//####[60]####
        /*  ParaTask helper method to access private/protected slots *///####[60]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[60]####
            if (m.getParameterTypes().length == 0)//####[60]####
                m.invoke(instance);//####[60]####
            else if ((m.getParameterTypes().length == 1))//####[60]####
                m.invoke(instance, arg);//####[60]####
            else //####[60]####
                m.invoke(instance, arg, interResult);//####[60]####
        }//####[60]####
//####[61]####
        Schedule localCurrentBest;//####[61]####
//####[62]####
        int id;//####[62]####
//####[63]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[63]####
//####[64]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[64]####
//####[66]####
        int schedulesTraversed = 0;//####[66]####
//####[67]####
        int schedulesCompleted = 0;//####[67]####
//####[69]####
        public Worker(int id) {//####[69]####
            this.id = id;//####[70]####
        }//####[71]####
//####[73]####
        public void run() {//####[73]####
            Schedule scheduleWeAreCurrentlyAt;//####[74]####
            while (true) //####[75]####
            {//####[75]####
                while (!localStack.isEmpty()) //####[77]####
                {//####[77]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[78]####
                    schedulesTraversed++;//####[79]####
                    if (schedulesTraversed > globalReadFrequency) //####[80]####
                    {//####[80]####
                        schedulesTraversed = 0;//####[81]####
                        updateLocalBest();//####[82]####
                    }//####[83]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[86]####
                    {//####[86]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[87]####
                        for (Schedule s : childNodes) //####[90]####
                        {//####[90]####
                            if (s.getEstimate() < localCurrentBestTime) //####[91]####
                            {//####[91]####
                                localStack.push(s);//####[92]####
                            }//####[93]####
                        }//####[94]####
                        if (childNodes.isEmpty()) //####[96]####
                        {//####[96]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[97]####
                            {//####[97]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[98]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[99]####
                                schedulesCompleted++;//####[100]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[101]####
                                {//####[101]####
                                    schedulesCompleted = 0;//####[102]####
                                    updateGlobalBest();//####[103]####
                                    updateLocalBest();//####[104]####
                                }//####[105]####
                            }//####[106]####
                        }//####[107]####
                    }//####[108]####
                }//####[109]####
                Schedule nextSchedule = getFromSharedQueue();//####[112]####
                if (nextSchedule == null) //####[114]####
                {//####[114]####
                    updateGlobalBest();//####[115]####
                    return;//####[116]####
                } else {//####[117]####
                    localStack.push(nextSchedule);//####[118]####
                }//####[119]####
            }//####[120]####
        }//####[121]####
//####[123]####
        private Schedule getFromSharedQueue() {//####[123]####
            synchronized (ParallelBranchAndBound.this) {//####[124]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[125]####
                {//####[125]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[126]####
                } else {//####[127]####
                    return null;//####[128]####
                }//####[129]####
            }//####[130]####
        }//####[131]####
//####[136]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[136]####
        private void updateLocalBest() {//####[136]####
            synchronized (ParallelBranchAndBound.this) {//####[137]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[138]####
                {//####[138]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[139]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[140]####
                }//####[141]####
            }//####[142]####
        }//####[143]####
//####[148]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[148]####
        private void updateGlobalBest() {//####[148]####
            synchronized (ParallelBranchAndBound.this) {//####[149]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[150]####
                {//####[150]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[151]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[152]####
                }//####[153]####
            }//####[154]####
        }//####[155]####
    }//####[155]####
//####[158]####
    private static volatile Method __pt__startWorker_int_method = null;//####[158]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[158]####
        if (__pt__startWorker_int_method == null) {//####[158]####
            try {//####[158]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[158]####
                    int.class//####[158]####
                });//####[158]####
            } catch (Exception e) {//####[158]####
                e.printStackTrace();//####[158]####
            }//####[158]####
        }//####[158]####
    }//####[158]####
    public TaskID<Void> startWorker(int id) {//####[158]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[158]####
        return startWorker(id, new TaskInfo());//####[158]####
    }//####[158]####
    public TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[158]####
        // ensure Method variable is set//####[158]####
        if (__pt__startWorker_int_method == null) {//####[158]####
            __pt__startWorker_int_ensureMethodVarSet();//####[158]####
        }//####[158]####
        taskinfo.setParameters(id);//####[158]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[158]####
        taskinfo.setInstance(this);//####[158]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[158]####
    }//####[158]####
    public TaskID<Void> startWorker(TaskID<Integer> id) {//####[158]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[158]####
        return startWorker(id, new TaskInfo());//####[158]####
    }//####[158]####
    public TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[158]####
        // ensure Method variable is set//####[158]####
        if (__pt__startWorker_int_method == null) {//####[158]####
            __pt__startWorker_int_ensureMethodVarSet();//####[158]####
        }//####[158]####
        taskinfo.setTaskIdArgIndexes(0);//####[158]####
        taskinfo.addDependsOn(id);//####[158]####
        taskinfo.setParameters(id);//####[158]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[158]####
        taskinfo.setInstance(this);//####[158]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[158]####
    }//####[158]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[158]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[158]####
        return startWorker(id, new TaskInfo());//####[158]####
    }//####[158]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[158]####
        // ensure Method variable is set//####[158]####
        if (__pt__startWorker_int_method == null) {//####[158]####
            __pt__startWorker_int_ensureMethodVarSet();//####[158]####
        }//####[158]####
        taskinfo.setQueueArgIndexes(0);//####[158]####
        taskinfo.setIsPipeline(true);//####[158]####
        taskinfo.setParameters(id);//####[158]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[158]####
        taskinfo.setInstance(this);//####[158]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[158]####
    }//####[158]####
    public void __pt__startWorker(int id) {//####[158]####
        new Worker(id).run();//####[159]####
    }//####[160]####
//####[160]####
}//####[160]####
