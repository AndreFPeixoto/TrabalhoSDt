import java.io.File;

public class ResourcesManager {

    public static double getDisk() {
        File cDrive = new File("C:");
        double totalSpace = (double) cDrive.getTotalSpace() / 1073741824;
        double usedSpace = (double) (cDrive.getTotalSpace() - cDrive.getFreeSpace()) / 1073741824;
        return (usedSpace / totalSpace) * 100;
    }

    public static double getMemory() {
        Runtime runtime = Runtime.getRuntime();
        double maxMemory = (double) runtime.maxMemory() / 1073741824;
        double usedRam = (double) (runtime.totalMemory() - runtime.freeMemory()) / 1073741824;
        return (usedRam / maxMemory) * 100;
    }

    public static int getCPU() {
        return (int) (Math.random() * (100 - 1 + 1) + 1);
    }
}
