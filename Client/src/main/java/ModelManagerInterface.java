import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ModelManagerInterface extends Remote {

    void sendModel(Model m) throws RemoteException;

    Model getModel(String requestID) throws RemoteException;

    List<String> getCompletedRequests(int processor) throws RemoteException;
}
