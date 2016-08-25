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
 * Parallel implementation of the branch and bound algorithm
 *///####[13]####
public class ParallelBranchAndBound implements Algorithm {//####[14]####
    static{ParaTask.init();}//####[14]####
    /*  ParaTask helper method to access private/protected slots *///####[14]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[14]####
        if (m.getParameterTypes().length == 0)//####[14]####
            m.invoke(instance);//####[14]####
        else if ((m.getParameterTypes().length == 1))//####[14]####
            m.invoke(instance, arg);//####[14]####
        else //####[14]####
            m.invoke(instance, arg, interResult);//####[14]####
    }//####[14]####
//####[15]####
    private final int globalReadFrequency = 10000;//####[15]####
//####[16]####
    private final int globalUpdateFrequency = 0;//####[16]####
//####[17]####
    private final int startingIterations = 50;//####[17]####
//####[19]####
    private Queue<Schedule> processingQueue = new ArrayDeque<Schedule>();//####[19]####
//####[20]####
    private int threadsCurrentlyRunning = 0;//####[20]####
//####[21]####
    private Schedule currentBest;//####[21]####
//####[22]####
    private int currentBestTime = Integer.MAX_VALUE;//####[22]####
//####[23]####
    private int coresToRunOn = 0;//####[23]####
//####[30]####
    /**
	 * Initialise the algorithm with the number of cores to run on
	 * 
	 * @param coresToRunOn the number of cores to use
	 *///####[30]####
    public ParallelBranchAndBound(int coresToRunOn) {//####[30]####
        this(coresToRunOn, false);//####[31]####
    }//####[32]####
//####[40]####
    /**
	 * Initialise the algorithm with the number of cores to run on and whether to use visualisation
	 * 
	 * @param coresToRunOn the number of cores to use
	 * @param useVisualisation whether to enable visualisation
	 *///####[40]####
    public ParallelBranchAndBound(int coresToRunOn, boolean useVisualisation) {//####[40]####
        this.coresToRunOn = coresToRunOn;//####[41]####
    }//####[42]####
//####[52]####
    /**
	 * Finds the optimal schedule for a given task graph
	 * 
	 * @param taskGraph the input graph
	 * @return an optimal schedule for the given input
	 *///####[52]####
    @Override//####[52]####
    public Schedule schedule(TaskGraph taskGraph) {//####[52]####
        processingQueue.add(Schedule.getEmptySchedule(taskGraph));//####[54]####
        for (int i = 0; i < startingIterations; i++) //####[55]####
        {//####[55]####
            if (processingQueue.isEmpty()) //####[56]####
            {//####[56]####
                break;//####[57]####
            }//####[58]####
            Schedule schedule = processingQueue.remove();//####[60]####
            List<Schedule> children = schedule.generateChildren();//####[61]####
            if (children.isEmpty()) //####[63]####
            {//####[63]####
                if (schedule.getTotalTime() < currentBestTime) //####[64]####
                {//####[64]####
                    currentBest = schedule;//####[65]####
                    currentBestTime = schedule.getTotalTime();//####[66]####
                }//####[67]####
            }//####[68]####
            processingQueue.addAll(children);//####[70]####
        }//####[71]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[75]####
        for (int i = 0; i < coresToRunOn; i++) //####[76]####
        {//####[76]####
            TaskID id = startWorker(i);//####[77]####
            g.add(id);//####[78]####
        }//####[79]####
        try {//####[80]####
            g.waitTillFinished();//####[81]####
        } catch (Exception e3) {//####[82]####
            e3.printStackTrace();//####[83]####
        }//####[84]####
        return currentBest;//####[86]####
    }//####[87]####
//####[89]####
    public Schedule getCurrentBest() {//####[89]####
        synchronized (this) {//####[90]####
            return currentBest;//####[91]####
        }//####[92]####
    }//####[93]####
//####[95]####
    public boolean isDone() {//####[95]####
        return false;//####[96]####
    }//####[97]####
//####[102]####
    /**
	 * A worker class which runs on a single core
	 *///####[102]####
    class Worker {//####[102]####
//####[102]####
        /*  ParaTask helper method to access private/protected slots *///####[102]####
        public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[102]####
            if (m.getParameterTypes().length == 0)//####[102]####
                m.invoke(instance);//####[102]####
            else if ((m.getParameterTypes().length == 1))//####[102]####
                m.invoke(instance, arg);//####[102]####
            else //####[102]####
                m.invoke(instance, arg, interResult);//####[102]####
        }//####[102]####
//####[103]####
        private Schedule localCurrentBest;//####[103]####
//####[104]####
        private int localCurrentBestTime = Integer.MAX_VALUE;//####[104]####
//####[105]####
        private ArrayDeque<Schedule> localStack = new ArrayDeque<Schedule>();//####[105]####
//####[107]####
        private int id;//####[107]####
//####[108]####
        private int schedulesTraversed = 0;//####[108]####
//####[109]####
        private int schedulesCompleted = 0;//####[109]####
//####[111]####
        public Worker(int id) {//####[111]####
            this.id = id;//####[112]####
        }//####[113]####
//####[115]####
        public void run() {//####[115]####
            Schedule scheduleWeAreCurrentlyAt;//####[116]####
            while (true) //####[117]####
            {//####[117]####
                while (!localStack.isEmpty()) //####[119]####
                {//####[119]####
                    scheduleWeAreCurrentlyAt = localStack.pop();//####[120]####
                    schedulesTraversed++;//####[122]####
                    if (schedulesTraversed > globalReadFrequency) //####[125]####
                    {//####[125]####
                        schedulesTraversed = 0;//####[126]####
                        updateLocalBest();//####[127]####
                    }//####[128]####
                    if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < localCurrentBestTime) //####[131]####
                    {//####[131]####
                        List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[132]####
                        for (Schedule s : childNodes) //####[135]####
                        {//####[135]####
                            if (s.getEstimate() < localCurrentBestTime) //####[136]####
                            {//####[136]####
                                localStack.push(s);//####[137]####
                            }//####[138]####
                        }//####[139]####
                        if (childNodes.isEmpty()) //####[141]####
                        {//####[141]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < localCurrentBestTime) //####[142]####
                            {//####[142]####
                                localCurrentBest = scheduleWeAreCurrentlyAt;//####[143]####
                                localCurrentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[144]####
                                schedulesCompleted++;//####[145]####
                                if (schedulesCompleted > globalUpdateFrequency) //####[146]####
                                {//####[146]####
                                    schedulesCompleted = 0;//####[147]####
                                    updateGlobalBest();//####[148]####
                                    updateLocalBest();//####[149]####
                                }//####[150]####
                            }//####[151]####
                        }//####[152]####
                    }//####[153]####
                }//####[154]####
                Schedule nextSchedule = getFromSharedQueue();//####[157]####
                if (nextSchedule == null) //####[159]####
                {//####[159]####
                    updateGlobalBest();//####[160]####
                    return;//####[161]####
                } else {//####[162]####
                    localStack.push(nextSchedule);//####[163]####
                }//####[164]####
            }//####[165]####
        }//####[166]####
//####[173]####
        /**
		 * Get a new schedule (subtree root) from the global queue
		 * 
		 * @return a schedule to work on
		 *///####[173]####
        private Schedule getFromSharedQueue() {//####[173]####
            synchronized (ParallelBranchAndBound.this) {//####[174]####
                if (!ParallelBranchAndBound.this.processingQueue.isEmpty()) //####[175]####
                {//####[175]####
                    return ParallelBranchAndBound.this.processingQueue.remove();//####[176]####
                } else {//####[177]####
                    return null;//####[178]####
                }//####[179]####
            }//####[180]####
        }//####[181]####
//####[186]####
        /**
		 * Update the local best time and schedule with the global best if it is better
		 *///####[186]####
        private void updateLocalBest() {//####[186]####
            synchronized (ParallelBranchAndBound.this) {//####[187]####
                if (localCurrentBestTime > ParallelBranchAndBound.this.currentBestTime) //####[188]####
                {//####[188]####
                    localCurrentBest = ParallelBranchAndBound.this.currentBest;//####[189]####
                    localCurrentBestTime = ParallelBranchAndBound.this.currentBestTime;//####[190]####
                }//####[191]####
            }//####[192]####
        }//####[193]####
//####[198]####
        /**
		 * Update the global best time and schedule with the local best if it is better
		 *///####[198]####
        private void updateGlobalBest() {//####[198]####
            synchronized (ParallelBranchAndBound.this) {//####[199]####
                if (localCurrentBestTime < ParallelBranchAndBound.this.currentBestTime) //####[200]####
                {//####[200]####
                    ParallelBranchAndBound.this.currentBest = localCurrentBest;//####[201]####
                    ParallelBranchAndBound.this.currentBestTime = localCurrentBestTime;//####[202]####
                }//####[203]####
            }//####[204]####
        }//####[205]####
    }//####[205]####
//####[213]####
    private static volatile Method __pt__startWorker_int_method = null;//####[213]####
    private synchronized static void __pt__startWorker_int_ensureMethodVarSet() {//####[213]####
        if (__pt__startWorker_int_method == null) {//####[213]####
            try {//####[213]####
                __pt__startWorker_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[213]####
                    int.class//####[213]####
                });//####[213]####
            } catch (Exception e) {//####[213]####
                e.printStackTrace();//####[213]####
            }//####[213]####
        }//####[213]####
    }//####[213]####
    /**
	 * Starts a new worker (on a new core) to process the state tree
	 * 
	 * @param id the id of the worker
	 *///####[213]####
    private TaskID<Void> startWorker(int id) {//####[213]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[213]####
        return startWorker(id, new TaskInfo());//####[213]####
    }//####[213]####
    /**
	 * Starts a new worker (on a new core) to process the state tree
	 * 
	 * @param id the id of the worker
	 *///####[213]####
    private TaskID<Void> startWorker(int id, TaskInfo taskinfo) {//####[213]####
        // ensure Method variable is set//####[213]####
        if (__pt__startWorker_int_method == null) {//####[213]####
            __pt__startWorker_int_ensureMethodVarSet();//####[213]####
        }//####[213]####
        taskinfo.setParameters(id);//####[213]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[213]####
        taskinfo.setInstance(this);//####[213]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[213]####
    }//####[213]####
    /**
	 * Starts a new worker (on a new core) to process the state tree
	 * 
	 * @param id the id of the worker
	 *///####[213]####
    private TaskID<Void> startWorker(TaskID<Integer> id) {//####[213]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[213]####
        return startWorker(id, new TaskInfo());//####[213]####
    }//####[213]####
    /**
	 * Starts a new worker (on a new core) to process the state tree
	 * 
	 * @param id the id of the worker
	 *///####[213]####
    private TaskID<Void> startWorker(TaskID<Integer> id, TaskInfo taskinfo) {//####[213]####
        // ensure Method variable is set//####[213]####
        if (__pt__startWorker_int_method == null) {//####[213]####
            __pt__startWorker_int_ensureMethodVarSet();//####[213]####
        }//####[213]####
        taskinfo.setTaskIdArgIndexes(0);//####[213]####
        taskinfo.addDependsOn(id);//####[213]####
        taskinfo.setParameters(id);//####[213]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[213]####
        taskinfo.setInstance(this);//####[213]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[213]####
    }//####[213]####
    /**
	 * Starts a new worker (on a new core) to process the state tree
	 * 
	 * @param id the id of the worker
	 *///####[213]####
    private TaskID<Void> startWorker(BlockingQueue<Integer> id) {//####[213]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[213]####
        return startWorker(id, new TaskInfo());//####[213]####
    }//####[213]####
    /**
	 * Starts a new worker (on a new core) to process the state tree
	 * 
	 * @param id the id of the worker
	 *///####[213]####
    private TaskID<Void> startWorker(BlockingQueue<Integer> id, TaskInfo taskinfo) {//####[213]####
        // ensure Method variable is set//####[213]####
        if (__pt__startWorker_int_method == null) {//####[213]####
            __pt__startWorker_int_ensureMethodVarSet();//####[213]####
        }//####[213]####
        taskinfo.setQueueArgIndexes(0);//####[213]####
        taskinfo.setIsPipeline(true);//####[213]####
        taskinfo.setParameters(id);//####[213]####
        taskinfo.setMethod(__pt__startWorker_int_method);//####[213]####
        taskinfo.setInstance(this);//####[213]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[213]####
    }//####[213]####
    /**
	 * Starts a new worker (on a new core) to process the state tree
	 * 
	 * @param id the id of the worker
	 *///####[213]####
    public void __pt__startWorker(int id) {//####[213]####
        new Worker(id).run();//####[214]####
    }//####[215]####
//####[215]####
}//####[215]####
