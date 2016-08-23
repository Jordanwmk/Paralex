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
        int localCurrentBestTime = Integer.MAX_VALUE;//####[62]####
//####[63]####
        ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[63]####
//####[65]####
        int schedulesTraversed = 0;//####[65]####
//####[66]####
        int schedulesCompleted = 0;//####[66]####
//####[68]####
        public void run() {//####[68]####
            Schedule scheduleWeAreCurrentlyAt;//####[69]####
            while (true) //####[70]####
            {//####[70]####
                while (!localStack.isEmpty()) //####[72]####
                {//####[72]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[73]####
                    schedulesTraversed++;//####[74]####
                    if (schedulesTraversed > globalReadFrequency) //####[75]####
                    {//####[75]####
                        schedulesTraversed = 0;//####[76]####
                        updateLocalBest();//####[77]####
                    }//####[78]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[81]####
                    {//####[81]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[82]####
                        for (Schedule s : childNodes) //####[85]####
                        {//####[85]####
                            if (s.getEstimate() < localCurrentBestTime) //####[86]####
                            {//####[86]####
                                localStack.push(s);//####[87]####
                            }//####[88]####
                        }//####[89]####
                        if (childNodes.isEmpty()) //####[91]####
                        {//####[91]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[92]####
                            {//####[92]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[93]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[94]####
                                schedulesCompleted++;//####[95]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[96]####
                                {//####[96]####
                                    schedulesCompleted = 0;//####[97]####
                                    updateGlobalBest();//####[98]####
                                    updateLocalBest();//####[99]####
                                }//####[100]####
                            }//####[101]####
                        }//####[102]####
                    }//####[103]####
                }//####[104]####
                Schedule nextSchedule = getFromSharedQueue();//####[107]####
                if (nextSchedule == null) //####[109]####
                {//####[109]####
                    updateGlobalBest();//####[110]####
                    return;//####[111]####
                } else {//####[112]####
                    localStack.push(nextSchedule);//####[113]####
                }//####[114]####
            }//####[115]####
        }//####[116]####
//####[118]####
        private Schedule getFromSharedQueue() {//####[118]####
            synchronized (ParallelBranchAndBound.this) {//####[119]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[120]####
                {//####[120]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[121]####
                } else {//####[122]####
                    return null;//####[123]####
                }//####[124]####
            }//####[125]####
        }//####[126]####
//####[131]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[131]####
        private void updateLocalBest() {//####[131]####
            synchronized (ParallelBranchAndBound.this) {//####[132]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[133]####
                {//####[133]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[134]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[135]####
                }//####[136]####
            }//####[137]####
        }//####[138]####
//####[143]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[143]####
        private void updateGlobalBest() {//####[143]####
            synchronized (ParallelBranchAndBound.this) {//####[144]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[145]####
                {//####[145]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[146]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[147]####
                }//####[148]####
            }//####[149]####
        }//####[150]####
    }//####[150]####
//####[153]####
    private static volatile Method __pt__startWorker__method = null;//####[153]####
    private synchronized static void __pt__startWorker__ensureMethodVarSet() {//####[153]####
        if (__pt__startWorker__method == null) {//####[153]####
            try {//####[153]####
                __pt__startWorker__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[153]####
                    //####[153]####
                });//####[153]####
            } catch (Exception e) {//####[153]####
                e.printStackTrace();//####[153]####
            }//####[153]####
        }//####[153]####
    }//####[153]####
    public TaskID<Void> startWorker() {//####[153]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[153]####
        return startWorker(new TaskInfo());//####[153]####
    }//####[153]####
    public TaskID<Void> startWorker(TaskInfo taskinfo) {//####[153]####
        // ensure Method variable is set//####[153]####
        if (__pt__startWorker__method == null) {//####[153]####
            __pt__startWorker__ensureMethodVarSet();//####[153]####
        }//####[153]####
        taskinfo.setParameters();//####[153]####
        taskinfo.setMethod(__pt__startWorker__method);//####[153]####
        taskinfo.setInstance(this);//####[153]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[153]####
    }//####[153]####
    public void __pt__startWorker() {//####[153]####
        new Worker().run();//####[154]####
    }//####[155]####
//####[155]####
//####[157]####
    private static volatile Method __pt__startWorkers__method = null;//####[157]####
    private synchronized static void __pt__startWorkers__ensureMethodVarSet() {//####[157]####
        if (__pt__startWorkers__method == null) {//####[157]####
            try {//####[157]####
                __pt__startWorkers__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorkers", new Class[] {//####[157]####
                    //####[157]####
                });//####[157]####
            } catch (Exception e) {//####[157]####
                e.printStackTrace();//####[157]####
            }//####[157]####
        }//####[157]####
    }//####[157]####
    public TaskIDGroup<Void> startWorkers() {//####[157]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[157]####
        return startWorkers(new TaskInfo());//####[157]####
    }//####[157]####
    public TaskIDGroup<Void> startWorkers(TaskInfo taskinfo) {//####[157]####
        // ensure Method variable is set//####[157]####
        if (__pt__startWorkers__method == null) {//####[157]####
            __pt__startWorkers__ensureMethodVarSet();//####[157]####
        }//####[157]####
        taskinfo.setParameters();//####[157]####
        taskinfo.setMethod(__pt__startWorkers__method);//####[157]####
        taskinfo.setInstance(this);//####[157]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, coresToRunOn);//####[157]####
    }//####[157]####
    public void __pt__startWorkers() {//####[157]####
        for (int i = 0; i < coresToRunOn; i++) //####[158]####
        {//####[158]####
            new Worker().run();//####[159]####
        }//####[160]####
    }//####[161]####
//####[161]####
}//####[161]####
