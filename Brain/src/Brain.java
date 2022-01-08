import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Brain {

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            Registry r = LocateRegistry.createRegistry(port);
            ModelManager manager = new ModelManager(port);
            r.rebind("modelmanager", manager);
            System.out.println("Brain " + port + " is ready!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
