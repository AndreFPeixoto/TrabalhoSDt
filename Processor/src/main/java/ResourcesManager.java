import java.io.File;

public class ResourcesManager {

    private static double getDisk() {

        File cDrive = new File("C:");
        double totalSpace = (double) cDrive.getTotalSpace() / 1073741824;
        double usedSpace = (double) (cDrive.getTotalSpace() - cDrive.getFreeSpace()) / 1073741824;
        return (usedSpace / totalSpace) * 100;
    }

    private static double getMemory() {
        Runtime runtime = Runtime.getRuntime();
        double maxMemory = (double) runtime.maxMemory();
        double usedRam = (double) (runtime.totalMemory() - runtime.freeMemory());
        return (usedRam / maxMemory) * 100;
    }

    private static double getCPU() {
        return 0;
    }

    public static boolean hasResources() {
        double disk = getDisk();
        double memory = getMemory();
        double cpu = getCPU();
        System.out.printf("Disk: %.2f %n", disk);
        System.out.printf("Memory: %.2f %n", memory);
        System.out.printf("CPU: %.2f %n", cpu);
        if (disk < 90 && memory < 80) {
            return true;
        }
        return false;
    }
}
