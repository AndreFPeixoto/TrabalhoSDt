import javax.management.*;
import java.io.File;
import java.lang.management.ManagementFactory;

public class ResourcesManager {

    private static double getDisk() {
        File cDrive = new File("C:");
        double totalSpace = (double) cDrive.getTotalSpace() / 1073741824;
        double usedSpace = (double) (cDrive.getTotalSpace() - cDrive.getFreeSpace()) / 1073741824;
        return (usedSpace / totalSpace) * 100;
    }

    private static double getMemory() {
        Runtime runtime = Runtime.getRuntime();
        double maxMemory = (double) runtime.maxMemory() / 1073741824;
        double usedRam = (double) (runtime.totalMemory() - runtime.freeMemory()) / 1073741824;
        return (usedRam / maxMemory) * 100;
    }

    private static double getCPU() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
            if (list.isEmpty()) return Double.NaN;
            Attribute att = (Attribute) list.get(0);
            Double value = (Double) att.getValue();
            if (value == -1.0) return Double.NaN;
            return ((int) (value * 1000) / 10.0);
        } catch (ReflectionException | MalformedObjectNameException | InstanceNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean hasResources() {
        double disk = getDisk();
        double memory = getMemory();
        double cpu = getCPU();
        return disk < 90 && memory < 80 && cpu < 80;
    }
}
