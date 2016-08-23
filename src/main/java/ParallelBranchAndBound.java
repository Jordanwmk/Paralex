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
//####[16]####
    Queue<Schedule> processingQueue = new ArrayDeque<Schedule>();//####[16]####
//####[17]####
    int threadsCurrentlyRunning = 0;//####[17]####
//####[18]####
    Schedule currentBest;//####[18]####
//####[19]####
    int currentBestTime = Integer.MAX_VALUE;//####[19]####
//####[20]####
    int coresToRunOn = 0;//####[20]####
//####[22]####
    public ParallelBranchAndBound(int coresToRunOn) {//####[22]####
        this.coresToRunOn = coresToRunOn;//####[23]####
    }//####[24]####
//####[27]####
    @Override//####[27]####
    public Schedule schedule(Graph taskGraph) {//####[27]####
        processingQueue.add(Schedule.getEmptySchedule(taskGraph));//####[29]####
        for (int i = 0; i < 50; i++) //####[30]####
        {//####[30]####
            processingQueue.addAll(processingQueue.remove().generateChildren());//####[31]####
        }//####[32]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[34]####
        for (int i = 0; i < coresToRunOn; i++) //####[35]####
        {//####[35]####
            TaskID id = startWorker();//####[36]####
            g.add(id);//####[37]####
        }//####[38]####
        try {//####[39]####
            g.waitTillFinished();//####[40]####
        } catch (Exception e3) {//####[41]####
            e3.printStackTrace();//####[42]####
        }//####[43]####
        return currentBest;//####[44]####
    }//####[46]####
//####[48]####
    class Worker {//####[48]####
//####[48]####
        /*  ParaTask helper method to access private/protected slots *///####[48]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[48]####
            if (m.getParameterTypes().length == 0)//####[48]####
                m.invoke(instance);//####[48]####
            else if ((m.getParameterTypes().length == 1))//####[48]####
                m.invoke(instance, arg);//####[48]####
            else //####[48]####
                m.invoke(instance, arg, interResult);//####[48]####
        }//####[48]####
//####[49]####
        Schedule localCurrentBest;//####[49]####
//####[50]####
        int localCurrentBestTime = Integer.MAX_VALUE;//####[50]####
//####[51]####
        Stack<Schedule> localStack = new Stack<Schedule>();//####[51]####
//####[53]####
        int schedulesTraversed = 0;//####[53]####
//####[54]####
        int schedulesCompleted = 0;//####[54]####
//####[56]####
        public void run() {//####[56]####
            Schedule scheduleWeAreCurrentlyAt;//####[57]####
            while (true) //####[58]####
            {//####[58]####
                while (!localStack.isEmpty()) //####[60]####
                {//####[60]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[61]####
                    schedulesTraversed++;//####[62]####
                    if (schedulesTraversed > globalReadFrequency) //####[63]####
                    {//####[63]####
                        schedulesTraversed = 0;//####[64]####
                        updateLocalBest();//####[65]####
                    }//####[66]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[69]####
                    {//####[69]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[70]####
                        for (Schedule s : childNodes) //####[73]####
                        {//####[73]####
                            if (s.getEstimate() < localCurrentBestTime) //####[74]####
                            {//####[74]####
                                localStack.push(s);//####[75]####
                            }//####[76]####
                        }//####[77]####
                        if (childNodes.isEmpty()) //####[79]####
                        {//####[79]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[80]####
                            {//####[80]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[81]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[82]####
                                schedulesCompleted++;//####[83]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[84]####
                                {//####[84]####
                                    schedulesCompleted = 0;//####[85]####
                                    updateGlobalBest();//####[86]####
                                    updateLocalBest();//####[87]####
                                }//####[88]####
                            }//####[89]####
                        }//####[90]####
                    }//####[91]####
                }//####[92]####
                Schedule nextSchedule = getFromSharedQueue();//####[95]####
                if (nextSchedule == null) //####[97]####
                {//####[97]####
                    updateGlobalBest();//####[98]####
                    return;//####[99]####
                } else {//####[100]####
                    localStack.add(nextSchedule);//####[101]####
                }//####[102]####
            }//####[103]####
        }//####[104]####
//####[106]####
        private Schedule getFromSharedQueue() {//####[106]####
            synchronized (ParallelBranchAndBound.this) {//####[107]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[108]####
                {//####[108]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[109]####
                } else {//####[110]####
                    return null;//####[111]####
                }//####[112]####
            }//####[113]####
        }//####[114]####
//####[119]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[119]####
        private void updateLocalBest() {//####[119]####
            synchronized (ParallelBranchAndBound.this) {//####[120]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[121]####
                {//####[121]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[122]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[123]####
                }//####[124]####
            }//####[125]####
        }//####[126]####
//####[131]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[131]####
        private void updateGlobalBest() {//####[131]####
            synchronized (ParallelBranchAndBound.this) {//####[132]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[133]####
                {//####[133]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[134]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[135]####
                }//####[136]####
            }//####[137]####
        }//####[138]####
    }//####[138]####
//####[141]####
    private static volatile Method __pt__startWorker__method = null;//####[141]####
    private synchronized static void __pt__startWorker__ensureMethodVarSet() {//####[141]####
        if (__pt__startWorker__method == null) {//####[141]####
            try {//####[141]####
                __pt__startWorker__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[141]####
                    //####[141]####
                });//####[141]####
            } catch (Exception e) {//####[141]####
                e.printStackTrace();//####[141]####
            }//####[141]####
        }//####[141]####
    }//####[141]####
    public TaskID<Void> startWorker() {//####[141]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[141]####
        return startWorker(new TaskInfo());//####[141]####
    }//####[141]####
    public TaskID<Void> startWorker(TaskInfo taskinfo) {//####[141]####
        // ensure Method variable is set//####[141]####
        if (__pt__startWorker__method == null) {//####[141]####
            __pt__startWorker__ensureMethodVarSet();//####[141]####
        }//####[141]####
        taskinfo.setParameters();//####[141]####
        taskinfo.setMethod(__pt__startWorker__method);//####[141]####
        taskinfo.setInstance(this);//####[141]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[141]####
    }//####[141]####
    public void __pt__startWorker() {//####[141]####
        new Worker().run();//####[142]####
    }//####[143]####
//####[143]####
}//####[143]####
