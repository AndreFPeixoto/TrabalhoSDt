import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Processor {

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            Registry r = LocateRegistry.createRegistry(port);
            ScriptManager manager = new ScriptManager(port);
            r.rebind("scriptmanager", manager);
            System.out.println("Processor " + port + " is ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
