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
/**
 * Parallel implementation of branch and bound algorithm with visualisation
 * (Note: this is exactly the same as the ParallelBranchAndBound class except it updates the visualiser each time it traverses a node)
 *///####[14]####
public class ParallelBranchAndBoundVisualisation implements Algorithm {//####[15]####
    static{ParaTask.init();}//####[15]####
    /*  ParaTask helper method to access private/protected slots *///####[15]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[15]####
        if (m.getParameterTypes().length == 0)//####[15]####
            m.invoke(instance);//####[15]####
        else if ((m.getParameterTypes().length == 1))//####[15]####
            m.invoke(instance, arg);//####[15]####
        else //####[15]####
            m.invoke(instance, arg, interResult);//####[15]####
    }//####[15]####
//####[16]####
    private final int globalReadFrequency = 10000;//####[16]####
//####[17]####
    private final int globalUpdateFrequency = 0;//####[17]####
//####[18]####
    private final int startingIterations = 50;//####[18]####
//####[20]####
    private Queue<Schedule> processingQueue = new ArrayDeque<Schedule>();//####[20]####
//####[21]####
    private int threadsCurrentlyRunning = 0;//####[21]####
//####[22]####
    private Schedule currentBest;//####[22]####
//####[23]####
    private int currentBestTime = Integer.MAX_VALUE;//####[23]####
//####[24]####
    private int coresToRunOn = 0;//####[24]####
//####[25]####
    private boolean useVisualisation;//####[25]####
//####[26]####
    private boolean done = false;//####[26]####
//####[28]####
    public ParallelBranchAndBoundVisualisation(int coresToRunOn) {//####[28]####
        this(coresToRunOn, false);//####[29]####
    }//####[30]####
//####[32]####
    public ParallelBranchAndBoundVisualisation(int coresToRunOn, boolean useVisualisation) {//####[32]####
        this.coresToRunOn = coresToRunOn;//####[33]####
        this.useVisualisation = useVisualisation;//####[34]####
    }//####[35]####
//####[38]####
    @Override//####[38]####
    public Schedule schedule(TaskGraph taskGraph) {//####[38]####
        processingQueue.add(Schedule.getEmptySchedule(taskGraph));//####[40]####
        for (int i = 0; i < startingIterations; i++) //####[41]####
        {//####[41]####
            if (processingQueue.isEmpty()) //####[42]####
            {//####[42]####
                break;//####[43]####
            }//####[44]####
            Schedule schedule = processingQueue.remove();//####[46]####
            List<Schedule> children = schedule.generateChildren();//####[47]####
            if (children.isEmpty()) //####[49]####
            {//####[49]####
                if (schedule.getTotalTime() < currentBestTime) //####[50]####
                {//####[50]####
                    currentBest = schedule;//####[51]####
                    currentBestTime = schedule.getTotalTime();//####[52]####
                }//####[53]####
            }//####[54]####
            processingQueue.addAll(children);//####[56]####
        }//####[57]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[61]####
        for (int i = 0; i < coresToRunOn; i++) //####[62]####
        {//####[62]####
            TaskID id = startWorker(i);//####[63]####
            g.add(id);//####[64]####
        }//####[65]####
        try {//####[66]####
            g.waitTillFinished();//####[67]####
        } catch (Exception e3) {//####[68]####
            e3.printStackTrace();//####[69]####
        }//####[70]####
        done = true;//####[72]####
        return currentBest;//####[73]####
    }//####[74]####
//####[76]####
    public Schedule getCurrentBest() {//####[76]####
        synchronized (this) {//####[77]####
            return currentBest;//####[78]####
        }//####[79]####
    }//####[80]####
//####[82]####
    public boolean isDone() {//####[82]####
        return done;//####[83]####
    }//####[84]####
//####[86]####
    class Worker {//####[86]####
//####[86]####
        /*  ParaTask helper method to access private/protected slots *///####[86]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[86]####
            if (m.getParameterTypes().length == 0)//####[86]####
                m.invoke(instance);//####[86]####
            else if ((m.getParameterTypes().length == 1))//####[86]####
                m.invoke(instance, arg);//####[86]####
            else //####[86]####
                m.invoke(instance, arg, interResult);//####[86]####
        }//####[86]####
//####[87]####
        private Schedule localCurrentBest;//####[87]####
//####[88]####
        private int localCurrentBestTime = Integer.MAX_VALUE;//####[88]####
//####[89]####
        private ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[89]####
//####[91]####
        private int id;//####[91]####
//####[92]####
        private int schedulesTraversed = 0;//####[92]####
//####[93]####
        private int schedulesCompleted = 0;//####[93]####
//####[95]####
        public Worker(int id) {//####[95]####
            this.id = id;//####[96]####
        }//####[97]####
//####[99]####
        public void run() {//####[99]####
            Schedule scheduleWeAreCurrentlyAt;//####[100]####
            while (true) //####[101]####
            {//####[101]####
                while (!localStack.isEmpty()) //####[103]####
                {//####[103]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[104]####
                    if (useVisualisation) //####[106]####
                    {//####[106]####
                        VFrame frame = VFrame.getInstance();//####[107]####
                        if (scheduleWeAreCurrentlyAt.getTask() != -1) //####[108]####
                        {//####[108]####
                            frame.incrementTask(scheduleWeAreCurrentlyAt.getTask(), id);//####[111]####
                        }//####[112]####
                    }//####[113]####
                    schedulesTraversed++;//####[115]####
                    if (schedulesTraversed > globalReadFrequency) //####[116]####
                    {//####[116]####
                        schedulesTraversed = 0;//####[117]####
                        updateLocalBest();//####[118]####
                    }//####[119]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[122]####
                    {//####[122]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[123]####
                        for (Schedule s : childNodes) //####[126]####
                        {//####[126]####
                            if (s.getEstimate() < localCurrentBestTime) //####[127]####
                            {//####[127]####
                                localStack.push(s);//####[128]####
                            }//####[129]####
                        }//####[130]####
                        if (childNodes.isEmpty()) //####[132]####
                        {//####[132]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[133]####
                            {//####[133]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[134]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[135]####
                                schedulesCompleted++;//####[136]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[137]####
                                {//####[137]####
                                    schedulesCompleted = 0;//####[138]####
                                    updateGlobalBest();//####[139]####
                                    updateLocalBest();//####[140]####
                                }//####[141]####
                            }//####[142]####
                        }//####[143]####
                    }//####[144]####
                }//####[145]####
                Schedule nextSchedule = getFromSharedQueue();//####[148]####
                if (nextSchedule == null) //####[150]####
                {//####[150]####
                    updateGlobalBest();//####[151]####
                    return;//####[152]####
                } else {//####[153]####
                    localStack.push(nextSchedule);//####[154]####
                }//####[155]####
            }//####[156]####
        }//####[157]####
//####[159]####
        private Schedule getFromSharedQueue() {//####[159]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[160]####
                if (!ParallelBranchAndBoundVisualisation.this.processingQueue.isEmpty()) //####[161]####
                {//####[161]####
                    return ParallelBranchAndBoundVisualisation.this.processingQueue.remove();//####[162]####
                } else {//####[163]####
                    return null;//####[164]####
                }//####[165]####
            }//####[166]####
        }//####[167]####
//####[172]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[172]####
        private void updateLocalBest() {//####[172]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[173]####
                if (localCurrentBestTime > ParallelBranchAndBoundVisualisation.this.currentBestTime) //####[174]####
                {//####[174]####
                    localCurrentBest = ParallelBranchAndBoundVisualisation.this.currentBest;//####[175]####
                    localCurrentBestTime = ParallelBranchAndBoundVisualisation.this.currentBestTime;//####[176]####
                }//####[177]####
            }//####[178]####
        }//####[179]####
//####[184]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[184]####
        private void updateGlobalBest() {//####[184]####
            synchronized (ParallelBranchAndBoundVisualisation.this) {//####[185]####
                if (localCurrentBestTime < ParallelBranchAndBoundVisualisation.this.currentBestTime) //####[186]####
                {//####[186]####
                    ParallelBranchAndBoundVisualisation.this.currentBest = localCurrentBest;//####[187]####
                    ParallelBranchAndBoundVisualisation.this.currentBestTime = localCurrentBestTime;//####[188]####
                }//####[189]####
            }//####[190]####
        }//####[191]####
    }//####[191]####
//####[194]####
    private static volatile Method __pt__startWorker_int_method = null;//####[194]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[194]####
        if (__pt__startWorker_int_method == null) {//####[194]####
            try {//####[194]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[194]####
                    int.class//####[194]####
                });//####[194]####
            } catch (Exception e) {//####[194]####
                e.printStackTrace();//####[194]####
            }//####[194]####
        }//####[194]####
    }//####[194]####
    public TaskID<Void> startWorker(int id) {//####[194]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[194]####
        return startWorker(id, new TaskInfo());//####[194]####
    }//####[194]####
    public TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[194]####
        // ensure Method variable is set//####[194]####
        if (__pt__startWorker_int_method == null) {//####[194]####
            __pt__startWorker_int_ensureMethodVarSet();//####[194]####
        }//####[194]####
        taskinfo.setParameters(id);//####[194]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[194]####
        taskinfo.setInstance(this);//####[194]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[194]####
    }//####[194]####
    public TaskID<Void> startWorker(TaskID<Integer> id) {//####[194]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[194]####
        return startWorker(id, new TaskInfo());//####[194]####
    }//####[194]####
    public TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[194]####
        // ensure Method variable is set//####[194]####
        if (__pt__startWorker_int_method == null) {//####[194]####
            __pt__startWorker_int_ensureMethodVarSet();//####[194]####
        }//####[194]####
        taskinfo.setTaskIdArgIndexes(0);//####[194]####
        taskinfo.addDependsOn(id);//####[194]####
        taskinfo.setParameters(id);//####[194]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[194]####
        taskinfo.setInstance(this);//####[194]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[194]####
    }//####[194]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[194]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[194]####
        return startWorker(id, new TaskInfo());//####[194]####
    }//####[194]####
    public TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[194]####
        // ensure Method variable is set//####[194]####
        if (__pt__startWorker_int_method == null) {//####[194]####
            __pt__startWorker_int_ensureMethodVarSet();//####[194]####
        }//####[194]####
        taskinfo.setQueueArgIndexes(0);//####[194]####
        taskinfo.setIsPipeline(true);//####[194]####
        taskinfo.setParameters(id);//####[194]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[194]####
        taskinfo.setInstance(this);//####[194]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[194]####
    }//####[194]####
    public void __pt__startWorker(int id) {//####[194]####
        new Worker(id).run();//####[195]####
    }//####[196]####
//####[196]####
}//####[196]####
