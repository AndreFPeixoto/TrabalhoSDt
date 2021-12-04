import java.io.Serializable;

public class Model implements Serializable {

    String requestID;
    StringBuilder output;

    public Model(String requestID, StringBuilder output) {
        this.requestID = requestID;
        this.output = output;
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
