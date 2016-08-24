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
            processingQueue.addAll(processingQueue.remove().generateChildren());//####[36]####
        }//####[37]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[41]####
        for (int i = 0; i < coresToRunOn; i++) //####[42]####
        {//####[42]####
            TaskID id = startWorker(i);//####[43]####
            g.add(id);//####[44]####
        }//####[45]####
        try {//####[46]####
            g.waitTillFinished();//####[47]####
        } catch (Exception e3) {//####[48]####
            e3.printStackTrace();//####[49]####
        }//####[50]####
        return currentBest;//####[61]####
    }//####[62]####
//####[64]####
    public Schedule getCurrentBest() {//####[64]####
        synchronized (this) {//####[65]####
            return currentBest;//####[66]####
        }//####[67]####
    }//####[68]####
//####[70]####
    public boolean isDone() {//####[70]####
        return false;//####[71]####
    }//####[72]####
//####[74]####
    class Worker {//####[74]####
//####[74]####
        /*  ParaTask helper method to access private/protected slots *///####[74]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[74]####
            if (m.getParameterTypes().length == 0)//####[74]####
                m.invoke(instance);//####[74]####
            else if ((m.getParameterTypes().length == 1))//####[74]####
                m.invoke(instance, arg);//####[74]####
            else //####[74]####
                m.invoke(instance, arg, interResult);//####[74]####
        }//####[74]####
//####[75]####
        Schedule localCurrentBest;//####[75]####
//####[76]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[76]####
//####[77]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[77]####
//####[79]####
        int id;//####[79]####
//####[80]####
        int schedulesTraversed = 0;//####[80]####
//####[81]####
        int schedulesCompleted = 0;//####[81]####
//####[83]####
        public Worker(int id) {//####[83]####
            this.id = id;//####[84]####
        }//####[85]####
//####[87]####
        public void run() {//####[87]####
            Schedule scheduleWeAreCurrentlyAt;//####[88]####
            while (true) //####[89]####
            {//####[89]####
                while (!localStack.isEmpty()) //####[91]####
                {//####[91]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[92]####
                    schedulesTraversed++;//####[94]####
                    if (schedulesTraversed > globalReadFrequency) //####[95]####
                    {//####[95]####
                        schedulesTraversed = 0;//####[96]####
                        updateLocalBest();//####[97]####
                    }//####[98]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[101]####
                    {//####[101]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[102]####
                        for (Schedule s : childNodes) //####[105]####
                        {//####[105]####
                            if (s.getEstimate() < localCurrentBestTime) //####[106]####
                            {//####[106]####
                                localStack.push(s);//####[107]####
                            }//####[108]####
                        }//####[109]####
                        if (childNodes.isEmpty()) //####[111]####
                        {//####[111]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[112]####
                            {//####[112]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[113]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[114]####
                                schedulesCompleted++;//####[115]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[116]####
                                {//####[116]####
                                    schedulesCompleted = 0;//####[117]####
                                    updateGlobalBest();//####[118]####
                                    updateLocalBest();//####[119]####
                                }//####[120]####
                            }//####[121]####
                        }//####[122]####
                    }//####[123]####
                }//####[124]####
                Schedule nextSchedule = getFromSharedQueue();//####[127]####
                if (nextSchedule == null) //####[129]####
                {//####[129]####
                    updateGlobalBest();//####[130]####
                    return;//####[131]####
                } else {//####[132]####
                    localStack.push(nextSchedule);//####[133]####
                }//####[134]####
            }//####[135]####
        }//####[136]####
//####[138]####
        private Schedule getFromSharedQueue() {//####[138]####
            synchronized (ParallelBranchAndBound.this) {//####[139]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[140]####
                {//####[140]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[141]####
                } else {//####[142]####
                    return null;//####[143]####
                }//####[144]####
            }//####[145]####
        }//####[146]####
//####[151]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[151]####
        private void updateLocalBest() {//####[151]####
            synchronized (ParallelBranchAndBound.this) {//####[152]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[153]####
                {//####[153]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[154]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[155]####
                }//####[156]####
            }//####[157]####
        }//####[158]####
//####[163]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[163]####
        private void updateGlobalBest() {//####[163]####
            synchronized (ParallelBranchAndBound.this) {//####[164]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[165]####
                {//####[165]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[166]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[167]####
                }//####[168]####
            }//####[169]####
        }//####[170]####
    }//####[170]####
//####[173]####
    private static volatile Method __pt__startWorker_int_method = null;//####[173]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[173]####
        if (__pt__startWorker_int_method == null) {//####[173]####
            try {//####[173]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[173]####
                    int.class//####[173]####
                });//####[173]####
            } catch (Exception e) {//####[173]####
                e.printStackTrace();//####[173]####
            }//####[173]####
        }//####[173]####
    }//####[173]####
    public TaskID<Void> startWorker(int id) {//####[173]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[173]####
        return startWorker(id, new TaskInfo());//####[173]####
    }//####[173]####
    public TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[173]####
        // ensure Method variable is set//####[173]####
        if (__pt__startWorker_int_method == null) {//####[173]####
            __pt__startWorker_int_ensureMethodVarSet();//####[173]####
        }//####[173]####
        taskinfo.setParameters(id);//####[173]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[173]####
        taskinfo.setInstance(this);//####[173]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[173]####
    }//####[173]####
    public TaskID<Void> startWorker(TaskID<Integer> id) {//####[173]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[173]####
        return startWorker(id, new TaskInfo());//####[173]####
    }//####[173]####
    public TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[173]####
        // ensure Method variable is set//####[173]####
        if (__pt__startWorker_int_method == null) {//####[173]####
            __pt__startWorker_int_ensureMethodVarSet();//####[173]####
        }//####[173]####
        taskinfo.setTaskIdArgIndexes(0);//####[173]####
        taskinfo.addDependsOn(id);//####[173]####
        taskinfo.setParameters(id);//####[173]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[173]####
        taskinfo.setInstance(this);//####[173]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[173]####
    }//####[173]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[173]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[173]####
        return startWorker(id, new TaskInfo());//####[173]####
    }//####[173]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[173]####
        // ensure Method variable is set//####[173]####
        if (__pt__startWorker_int_method == null) {//####[173]####
            __pt__startWorker_int_ensureMethodVarSet();//####[173]####
        }//####[173]####
        taskinfo.setQueueArgIndexes(0);//####[173]####
        taskinfo.setIsPipeline(true);//####[173]####
        taskinfo.setParameters(id);//####[173]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[173]####
        taskinfo.setInstance(this);//####[173]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[173]####
    }//####[173]####
    public void __pt__startWorker(int id) {//####[173]####
        new Worker(id).run();//####[174]####
    }//####[175]####
//####[175]####
}//####[175]####
