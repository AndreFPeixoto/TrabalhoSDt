import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            Processor.main(new String[]{"2101"});
            Processor.main(new String[]{"2102"});
            Processor.main(new String[]{"2103"});
        });
        thread.start();
        Thread.sleep(7000);
        try {
            String fileName = "example.bat";
            if (Utils.uploadScript(fileName)) {
                System.out.println("File uploaded to Loader");
                Script s = new Script(fileName);
                System.out.println("send request to stabilizer");
                ProcessorManagerInterface processorManager = (ProcessorManagerInterface) Naming.lookup("rmi://localhost:2500/processormanager");
                int processorID = processorManager.requestProcess(s);
                System.out.println("Processor port: " + processorID);
                if (processorID != 0) {
                    System.out.println("send request to Processor");
                    ScriptManagerInterface scriptManager = (ScriptManagerInterface) Naming.lookup("rmi://localhost:" + processorID + "/scriptmanager");
                    String id = scriptManager.processScript(s);
                    System.out.println("send request to Brain");
                    ModelManagerInterface modelManager = (ModelManagerInterface) Naming.lookup("rmi://localhost:2200/modelmanager");
                    Model model = modelManager.requestModel(id);
                    System.out.println(model.getOutput());
                } else {
                    System.out.println("no processor available");
                }
            } else {
                System.out.println("Failed to upload script");
            }
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
