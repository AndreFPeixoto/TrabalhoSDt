import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ScriptManagerInterface extends Remote {

    String processScript(Script s) throws RemoteException;

    void resumeRequests(int processor) throws RemoteException;

    HashMap<Integer, Heartbeat> recovery() throws RemoteException;
}
