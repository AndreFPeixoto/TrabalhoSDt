import java.io.Serializable;

public class Resources implements Serializable {

    double disk;
    double ram;
    double cpu;

    public Resources() {
    }

    public Resources(double disk, double ram, double cpu) {
        this.disk = disk;
        this.ram = ram;
        this.cpu = cpu;
    }

    public double getDisk() {
        return disk;
    }

    public void setDisk(double disk) {
        this.disk = disk;
    }

    public double getRam() {
        return ram;
    }

    public void setRam(double ram) {
        this.ram = ram;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }
}
