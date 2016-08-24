import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;


public class SystemQuery {

	public static String getProcessCpuLoad() throws Exception {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return "";

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return "";
        // returns a percentage value with 1 decimal point precision
       
        double usage = Math.round((value * 1000) / 10.0);

        return (String.valueOf(usage));
    }
	
	public static String getProcessMemLoad() throws Exception {
		 	MemoryUsage heapMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		 
		 	String memUsage = String.valueOf(heapMemory.getCommitted()/ 1000000);
		 	
	        return memUsage;
	}
}
