import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Heartbeat implements Serializable {

    int processorID;
    List<Script> tasks;
    double cpu;

    public Heartbeat(int processorID, List<Script> tasks, double cpu) {
        this.processorID = processorID;
        this.tasks = tasks;
        this.cpu = cpu;
    }

    public int getProcessorID() {
        return processorID;
    }

    public void setProcessorID(int processorID) {
        this.processorID = processorID;
    }

    public List<Script> getTasks() {
        return tasks;
    }

    public void setTasks(List<Script> tasks) {
        this.tasks = tasks;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }
}
