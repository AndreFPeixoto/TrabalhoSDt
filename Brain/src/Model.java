import java.io.Serializable;

public class Model implements Serializable {

    int processorID;
    String requestID;
    StringBuilder output;

    public Model(int processorID, String requestID, StringBuilder output) {
        this.processorID = processorID;
        this.requestID = requestID;
        this.output = output;
    }

    public int getProcessorID() {
        return processorID;
    }

    public void setProcessorID(int processorID) {
        this.processorID = processorID;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public StringBuilder getOutput() {
        return output;
    }

    public void setOutput(StringBuilder output) {
        this.output = output;
    }
}
