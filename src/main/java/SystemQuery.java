import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;


public class SystemQuery {

	public static String getProcessCpuLoad() throws Exception {

		//Factory to get information about the system
		MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
		ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

		if (list.isEmpty())     return "";

		//first attribute is cpu load
		Attribute att = (Attribute)list.get(0);

		//Convert to double
		Double value  = (Double)att.getValue();

		//some time for initialization. in this time the value will be -1
		if (value == -1.0)      return "";

		double usage = Math.round((value * 1000) / 10.0);

		return (String.valueOf(usage));
	}

	public static String getProcessMemLoad() throws Exception {

		//Factory to get information about the system
		MemoryUsage heapMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		//Get memory usage of the JVM
		String memUsage = String.valueOf(heapMemory.getCommitted()/ 1000000);

		return memUsage;
	}
}
