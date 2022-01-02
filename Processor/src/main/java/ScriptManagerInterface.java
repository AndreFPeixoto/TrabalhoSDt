import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ScriptManagerInterface extends Remote {

    String processScript(Script s) throws RemoteException;

    void resumeRequests(int processor) throws RemoteException;
}
