import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScriptManager extends UnicastRemoteObject implements ScriptManagerInterface {

    int port;
    List<Script> scripts = new ArrayList<>();

    protected ScriptManager(int port) throws RemoteException {
        this.port = 2100;
    }

    @Override
    public String processScript(Script s) throws RemoteException {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        s.setId(id);
        try {
            if (ResourcesManager.hasResources()) {
                if (downloadScript(s.getName())) {
                    Process p = Runtime.getRuntime().exec("scripts/" + s.getName());
                    StringBuilder output = new StringBuilder();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                    int exitVal = p.waitFor();
                    if (exitVal == 0) {
                        System.out.println("Success!");
                        Model m = new Model(port, id, output);
                        ModelManagerInterface modelManager = (ModelManagerInterface) Naming.lookup("rmi://localhost:2200/modelmanager");
                        modelManager.sendModel(m);
                    }
                } else {
                    System.out.println("Error downloading the script");
                }
            } else {
                System.out.println("I don't have any resources");
                scripts.add(s);
            }
        } catch (IOException | InterruptedException | NotBoundException e) {
            e.printStackTrace();
        }
        return id;
    }

    private boolean downloadScript(String name) {
        try {
            SftpConnection sftp = new SftpConnection();
            ChannelSftp channel = sftp.setupJsch();
            channel.connect();
            channel.get("scripts/" + name, "scripts/" + name);
            channel.disconnect();
            sftp.closeSession();
            return true;
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
            return false;
        }
    }
}
