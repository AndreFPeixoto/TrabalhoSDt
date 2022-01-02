import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelManager extends UnicastRemoteObject implements ModelManagerInterface {

    HashMap<String, Model> models = new HashMap<>();

    protected ModelManager() throws RemoteException {
    }

    @Override
    public void sendModel(Model m) throws RemoteException {
        models.put(m.getRequestID(), m);
    }

    @Override
    public Model getModel(String requestID) throws RemoteException {
        return models.get(requestID);
    }

    @Override
    public List<String> getCompletedRequests(int processor) throws RemoteException {
        List<String> modelsId = new ArrayList<>();
        for (Model m : models.values()) {
            if (m.getProcessorID() == processor) {
                modelsId.add(m.getRequestID());
            }
        }
        return modelsId;
    }
}
