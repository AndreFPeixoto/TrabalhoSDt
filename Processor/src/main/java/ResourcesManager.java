import java.io.File;
import java.lang.management.ManagementFactory;

public class ResourcesManager {

    static int mb = 1024 * 1024;
    static int gb = 1024 * 1024 * 1024;

    public static int getFreeDisk() {
        File diskPartition = new File("C:");
        long totalCapacity = diskPartition.getTotalSpace() / gb;
        double freePartitionSpace = diskPartition.getFreeSpace() / gb;
        return (int) ((freePartitionSpace / totalCapacity) * 100);
    }

    public static int getFreeMemory() {
        ManagementFactory.getOperatingSystemMXBean();
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        long physicalMemorySize = os.getTotalPhysicalMemorySize() / mb;
        long physicalfreeMemorySize = os.getFreePhysicalMemorySize() / mb;
        return (int) ((physicalfreeMemorySize / physicalMemorySize) * 100);
    }

    public static int getFreeCpu() {
        return (int) (Math.random() * (100 - 1 + 1) + 1);
    }
}
