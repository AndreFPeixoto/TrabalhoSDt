import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ModelManagerInterface extends Remote {

    void sendModel(Model m) throws RemoteException;

    Model getModel(String requestID) throws RemoteException;
}
