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
    public Schedule schedule(Graph taskGraph) {//####[28]####
        processingQueue.add(Schedule.getEmptySchedule(taskGraph));//####[30]####
        for (int i = 0; i < startingIterations; i++) //####[31]####
        {//####[31]####
            processingQueue.addAll(processingQueue.remove().generateChildren());//####[32]####
        }//####[33]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[37]####
        for (int i = 0; i < coresToRunOn; i++) //####[38]####
        {//####[38]####
            TaskID id = startWorker();//####[39]####
            g.add(id);//####[40]####
        }//####[41]####
        try {//####[42]####
            g.waitTillFinished();//####[43]####
        } catch (Exception e3) {//####[44]####
            e3.printStackTrace();//####[45]####
        }//####[46]####
        return currentBest;//####[57]####
    }//####[59]####
//####[61]####
    class Worker {//####[61]####
//####[61]####
        /*  ParaTask helper method to access private/protected slots *///####[61]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[61]####
            if (m.getParameterTypes().length == 0)//####[61]####
                m.invoke(instance);//####[61]####
            else if ((m.getParameterTypes().length == 1))//####[61]####
                m.invoke(instance, arg);//####[61]####
            else //####[61]####
                m.invoke(instance, arg, interResult);//####[61]####
        }//####[61]####
//####[62]####
        Schedule localCurrentBest;//####[62]####
//####[63]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[63]####
//####[64]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[64]####
//####[66]####
        int schedulesTraversed = 0;//####[66]####
//####[67]####
        int schedulesCompleted = 0;//####[67]####
//####[69]####
        public void run() {//####[69]####
            Schedule scheduleWeAreCurrentlyAt;//####[70]####
            while (true) //####[71]####
            {//####[71]####
                while (!localStack.isEmpty()) //####[73]####
                {//####[73]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[74]####
                    schedulesTraversed++;//####[75]####
                    if (schedulesTraversed > globalReadFrequency) //####[76]####
                    {//####[76]####
                        schedulesTraversed = 0;//####[77]####
                        updateLocalBest();//####[78]####
                    }//####[79]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[82]####
                    {//####[82]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[83]####
                        for (Schedule s : childNodes) //####[86]####
                        {//####[86]####
                            if (s.getEstimate() < localCurrentBestTime) //####[87]####
                            {//####[87]####
                                localStack.push(s);//####[88]####
                            }//####[89]####
                        }//####[90]####
                        if (childNodes.isEmpty()) //####[92]####
                        {//####[92]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[93]####
                            {//####[93]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[94]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[95]####
                                schedulesCompleted++;//####[96]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[97]####
                                {//####[97]####
                                    schedulesCompleted = 0;//####[98]####
                                    updateGlobalBest();//####[99]####
                                    updateLocalBest();//####[100]####
                                }//####[101]####
                            }//####[102]####
                        }//####[103]####
                    }//####[104]####
                }//####[105]####
                Schedule nextSchedule = getFromSharedQueue();//####[108]####
                if (nextSchedule == null) //####[110]####
                {//####[110]####
                    updateGlobalBest();//####[111]####
                    return;//####[112]####
                } else {//####[113]####
                    localStack.push(nextSchedule);//####[114]####
                }//####[115]####
            }//####[116]####
        }//####[117]####
//####[119]####
        private Schedule getFromSharedQueue() {//####[119]####
            synchronized (ParallelBranchAndBound.this) {//####[120]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[121]####
                {//####[121]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[122]####
                } else {//####[123]####
                    return null;//####[124]####
                }//####[125]####
            }//####[126]####
        }//####[127]####
//####[132]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[132]####
        private void updateLocalBest() {//####[132]####
            synchronized (ParallelBranchAndBound.this) {//####[133]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[134]####
                {//####[134]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[135]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[136]####
                }//####[137]####
            }//####[138]####
        }//####[139]####
//####[144]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[144]####
        private void updateGlobalBest() {//####[144]####
            synchronized (ParallelBranchAndBound.this) {//####[145]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[146]####
                {//####[146]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[147]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[148]####
                }//####[149]####
            }//####[150]####
        }//####[151]####
    }//####[151]####
//####[154]####
    private static volatile Method __pt__startWorker__method = null;//####[154]####
    private synchronized static void __pt__startWorker__ensureMethodVarSet() {//####[154]####
        if (__pt__startWorker__method == null) {//####[154]####
            try {//####[154]####
                __pt__startWorker__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[154]####
                    //####[154]####
                });//####[154]####
            } catch (Exception e) {//####[154]####
                e.printStackTrace();//####[154]####
            }//####[154]####
        }//####[154]####
    }//####[154]####
    public TaskID<Void> startWorker() {//####[154]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[154]####
        return startWorker(new TaskInfo());//####[154]####
    }//####[154]####
    public TaskID<Void> startWorker(TaskInfo taskinfo) {//####[154]####
        // ensure Method variable is set//####[154]####
        if (__pt__startWorker__method == null) {//####[154]####
            __pt__startWorker__ensureMethodVarSet();//####[154]####
        }//####[154]####
        taskinfo.setParameters();//####[154]####
        taskinfo.setMethod(__pt__startWorker__method);//####[154]####
        taskinfo.setInstance(this);//####[154]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[154]####
    }//####[154]####
    public void __pt__startWorker() {//####[154]####
        new Worker().run();//####[155]####
    }//####[156]####
//####[156]####
//####[158]####
    private static volatile Method __pt__startWorkers__method = null;//####[158]####
    private synchronized static void __pt__startWorkers__ensureMethodVarSet() {//####[158]####
        if (__pt__startWorkers__method == null) {//####[158]####
            try {//####[158]####
                __pt__startWorkers__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorkers", new Class[] {//####[158]####
                    //####[158]####
                });//####[158]####
            } catch (Exception e) {//####[158]####
                e.printStackTrace();//####[158]####
            }//####[158]####
        }//####[158]####
    }//####[158]####
    public TaskIDGroup<Void> startWorkers() {//####[158]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[158]####
        return startWorkers(new TaskInfo());//####[158]####
    }//####[158]####
    public TaskIDGroup<Void> startWorkers(TaskInfo taskinfo) {//####[158]####
        // ensure Method variable is set//####[158]####
        if (__pt__startWorkers__method == null) {//####[158]####
            __pt__startWorkers__ensureMethodVarSet();//####[158]####
        }//####[158]####
        taskinfo.setParameters();//####[158]####
        taskinfo.setMethod(__pt__startWorkers__method);//####[158]####
        taskinfo.setInstance(this);//####[158]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, coresToRunOn);//####[158]####
    }//####[158]####
    public void __pt__startWorkers() {//####[158]####
        for (int i = 0; i < coresToRunOn; i++) //####[159]####
        {//####[159]####
            new Worker().run();//####[160]####
        }//####[161]####
    }//####[162]####
//####[162]####
}//####[162]####
