import java.util.List;//####[1]####
import java.util.Stack;//####[2]####
import java.util.concurrent.ExecutionException;//####[3]####
import pt.runtime.*;//####[5]####
import java.util.EmptyStackException;//####[6]####
import java.lang.Thread;//####[7]####
//####[7]####
//-- ParaTask related imports//####[7]####
import pt.runtime.*;//####[7]####
import java.util.concurrent.ExecutionException;//####[7]####
import java.util.concurrent.locks.*;//####[7]####
import java.lang.reflect.*;//####[7]####
import pt.runtime.GuiThread;//####[7]####
import java.util.concurrent.BlockingQueue;//####[7]####
import java.util.ArrayList;//####[7]####
import java.util.List;//####[7]####
//####[7]####
public class ParallelBranchAndBound implements Algorithm {//####[10]####
    static{ParaTask.init();}//####[10]####
    /*  ParaTask helper method to access private/protected slots *///####[10]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[10]####
        if (m.getParameterTypes().length == 0)//####[10]####
            m.invoke(instance);//####[10]####
        else if ((m.getParameterTypes().length == 1))//####[10]####
            m.invoke(instance, arg);//####[10]####
        else //####[10]####
            m.invoke(instance, arg, interResult);//####[10]####
    }//####[10]####
//####[12]####
    Stack<Schedule> stack = new Stack<Schedule>();//####[12]####
//####[13]####
    int threadsCurrentlyRunning = 0;//####[13]####
//####[14]####
    Schedule currentBest;//####[14]####
//####[15]####
    int currentBestTime = Integer.MAX_VALUE;//####[15]####
//####[16]####
    int coresToRunOn = 0;//####[16]####
//####[18]####
    public ParallelBranchAndBound(int coresToRunOn) {//####[18]####
        this.coresToRunOn = coresToRunOn;//####[19]####
    }//####[20]####
//####[23]####
    @Override//####[23]####
    public Schedule schedule(Graph taskGraph) {//####[23]####
        stack.add(Schedule.getEmptySchedule(taskGraph));//####[24]####
        TaskIDGroup g = new TaskIDGroup(coresToRunOn);//####[25]####
        for (int i = 0; i < coresToRunOn; i++) //####[26]####
        {//####[26]####
            TaskID id = startWorker();//####[27]####
            g.add(id);//####[28]####
        }//####[29]####
        try {//####[30]####
            g.waitTillFinished();//####[31]####
        } catch (Exception e3) {//####[32]####
            e3.printStackTrace();//####[33]####
        }//####[34]####
        return currentBest;//####[35]####
    }//####[37]####
//####[39]####
    private static volatile Method __pt__startWorker__method = null;//####[39]####
    private synchronized static void __pt__startWorker__ensureMethodVarSet() {//####[39]####
        if (__pt__startWorker__method == null) {//####[39]####
            try {//####[39]####
                __pt__startWorker__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__startWorker", new Class[] {//####[39]####
                    //####[39]####
                });//####[39]####
            } catch (Exception e) {//####[39]####
                e.printStackTrace();//####[39]####
            }//####[39]####
        }//####[39]####
    }//####[39]####
    public TaskID<Void> startWorker() {//####[39]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[39]####
        return startWorker(new TaskInfo());//####[39]####
    }//####[39]####
    public TaskID<Void> startWorker(TaskInfo taskinfo) {//####[39]####
        // ensure Method variable is set//####[39]####
        if (__pt__startWorker__method == null) {//####[39]####
            __pt__startWorker__ensureMethodVarSet();//####[39]####
        }//####[39]####
        taskinfo.setParameters();//####[39]####
        taskinfo.setMethod(__pt__startWorker__method);//####[39]####
        taskinfo.setInstance(this);//####[39]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[39]####
    }//####[39]####
    public void __pt__startWorker() {//####[39]####
        Schedule scheduleWeAreCurrentlyAt = null;//####[41]####
        while (!stack.isEmpty() || threadsCurrentlyRunning != 0) //####[42]####
        {//####[42]####
            try {//####[44]####
                synchronized (this) {//####[45]####
                    scheduleWeAreCurrentlyAt = stack.pop();//####[46]####
                    threadsCurrentlyRunning++;//####[47]####
                }//####[48]####
                if (scheduleWeAreCurrentlyAt.getFinishTimeEstimate() < currentBestTime) //####[51]####
                {//####[51]####
                    List<Schedule> childNodes = scheduleWeAreCurrentlyAt.generateChildren();//####[52]####
                    if (childNodes.isEmpty()) //####[54]####
                    {//####[54]####
                        synchronized (this) {//####[56]####
                            if (scheduleWeAreCurrentlyAt.getTotalTime() < currentBestTime) //####[57]####
                            {//####[57]####
                                currentBest = scheduleWeAreCurrentlyAt;//####[58]####
                                currentBestTime = scheduleWeAreCurrentlyAt.getTotalTime();//####[59]####
                            }//####[60]####
                        }//####[61]####
                    }//####[62]####
                    synchronized (this) {//####[63]####
                        stack.addAll(childNodes);//####[64]####
                    }//####[65]####
                }//####[66]####
                synchronized (this) {//####[67]####
                    threadsCurrentlyRunning--;//####[68]####
                }//####[69]####
            } catch (EmptyStackException e) {//####[71]####
                continue;//####[78]####
            }//####[79]####
        }//####[81]####
    }//####[82]####
//####[82]####
}//####[82]####
