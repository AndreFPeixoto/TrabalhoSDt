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
        Thread.sleep(10000);
        try {
            String fileName = "example.bat";
            if (Utils.uploadScript(fileName)) {
                System.out.println("Client upload file to Loader");
                Script s = new Script(fileName);
                System.out.println("Client send request to Stabilizer");
                ProcessorManagerInterface processorManager = (ProcessorManagerInterface) Naming.lookup("rmi://localhost:2500/processormanager");
                int processorID = processorManager.requestProcess(s);
                System.out.println("Stabilizer response Processor " + processorID);
                if (processorID != 0) {
                    System.out.println("Client send request to processor " + processorID);
                    ScriptManagerInterface scriptManager = (ScriptManagerInterface) Naming.lookup("rmi://localhost:" + processorID + "/scriptmanager");
                    String id = scriptManager.processScript(s);
                    System.out.println("Processor assigned id " + id + " to Client request");
                    System.out.println("Client request Brain to Stabilizer");
                    int brainID = processorManager.requestBrain();
                    if (brainID != 0) {
                        System.out.println("Stabilizer response Brain " + brainID);
                        System.out.println("Client send request to Brain " + brainID);
                        ModelManagerInterface modelManager = (ModelManagerInterface) Naming.lookup("rmi://localhost:" + brainID + "/modelmanager");
                        Model model = modelManager.requestModel(id);
                        if (model != null) {
                            System.out.println(model.getOutput());
                        } else {
                            System.out.println("Não foi possivel obter resposta");
                        }
                    }
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
