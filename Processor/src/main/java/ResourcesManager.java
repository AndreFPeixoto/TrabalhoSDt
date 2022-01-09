import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try {
            Process p = Runtime.getRuntime().exec("wmic cpu get loadpercentage");
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitVal = p.waitFor();
            if (exitVal == 0) {
                return Integer.parseInt(getNbr(output.toString()));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return (int) (Math.random() * (100 - 1 + 1) + 1);
    }

    static String getNbr(String str) {
        str = str.replaceAll("[^\\d]", " ");
        str = str.trim();
        str = str.replaceAll(" +", " ");
        return str;
    }
}
