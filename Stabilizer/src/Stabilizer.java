import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Stabilizer {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(2500);
            ProcessorManager manager = new ProcessorManager();
            registry.rebind("processormanager", manager);
            System.out.println("Stabilizer is ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
