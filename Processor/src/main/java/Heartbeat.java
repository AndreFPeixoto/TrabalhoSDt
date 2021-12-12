import java.io.Serializable;
import java.util.HashMap;

public class Heartbeat implements Serializable {

    int processorID;
    HashMap<Long, String> threads;
    Resources available;

    public Heartbeat(int processorID, HashMap<Long, String> threads, Resources available) {
        this.processorID = processorID;
        this.threads = threads;
        this.available = available;
    }

    public int getProcessorID() {
        return processorID;
    }

    public void setProcessorID(int processorID) {
        this.processorID = processorID;
    }

    public HashMap<Long, String> getThreads() {
        return threads;
    }

    public void setThreads(HashMap<Long, String> threads) {
        this.threads = threads;
    }

    public Resources getAvailable() {
        return available;
    }

    public void setAvailable(Resources available) {
        this.available = available;
    }
}
