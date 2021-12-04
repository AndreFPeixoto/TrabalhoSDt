import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    public static void main(String[] args) {
        try {
            String fileName = "example.bat";
            if (uploadScript(fileName)) {
                System.out.println("File uploaded to Loader");
                ScriptManagerInterface scriptManager = (ScriptManagerInterface) Naming.lookup("rmi://localhost:2100/scriptmanager");
                Script s = new Script(fileName);
                String id = scriptManager.processScript(s);
                System.out.println("Process request sent to Processor");
            } else {
                System.out.println("Failed to upload script");
            }
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public static boolean uploadScript(String name) {
        try {
            SftpConnection sftp = new SftpConnection();
            ChannelSftp channel = sftp.setupJsch();
            channel.connect();
            channel.put("scripts/" + name, "scripts/" + name);
            channel.disconnect();
            sftp.closeSession();
            return true;
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
            return false;
        }
    }
}
