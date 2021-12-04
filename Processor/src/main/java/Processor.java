import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Processor {

    public static void main(String[] args) {
        try {
            int port = 2100;
            Registry r = LocateRegistry.createRegistry(port);
            ScriptManager manager = new ScriptManager(args[0]);
            r.rebind("scriptmanager", manager);
            System.out.println("Processor " + port + " is ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
