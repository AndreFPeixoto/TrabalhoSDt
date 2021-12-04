import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class Loader {

    public static void main(String[] args) {
        try {
            SshServer sshd = SshServer.setUpDefaultServer();
            sshd.setHost("localhost");
            sshd.setPort(2000);
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser")));
            sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
            sshd.setPasswordAuthenticator((username, password, session) -> username.equals("test") && password.equals("password"));
            sshd.start();
            System.out.println("SFTP server start on " + sshd.getHost() + ":" + sshd.getPort());
            while (true) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
