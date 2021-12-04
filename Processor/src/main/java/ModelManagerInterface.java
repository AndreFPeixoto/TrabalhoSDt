import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ModelManagerInterface extends Remote {

    void sendModel(Model m, int processorID) throws RemoteException;
}
