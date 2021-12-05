import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

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
}
