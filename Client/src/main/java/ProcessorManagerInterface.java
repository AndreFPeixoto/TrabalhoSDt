import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessorManagerInterface extends Remote {

    int requestProcess(Script s) throws RemoteException;
}
