import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Stabilizer {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            Brain.main(new String[]{"2200"});
            Brain.main(new String[]{"2201"});
            Brain.main(new String[]{"2202"});
        });
        thread.start();
        Thread.sleep(5000);
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
