import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ScriptManagerInterface extends Remote {

    String processScript(Script s) throws RemoteException;
}
