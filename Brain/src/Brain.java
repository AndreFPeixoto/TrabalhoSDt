import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Brain {

    public static void main(String[] args) {
        try {
            Registry r = LocateRegistry.createRegistry(2200);
            ModelManager manager = new ModelManager();
            r.rebind("modelmanager", manager);
            System.out.println("Brain is ready!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
