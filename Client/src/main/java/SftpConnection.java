import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpConnection {

    private static String remoteHost = "localhost";
    private static String username = "test";
    private static String password = "password";
    private static Session session;

    public ChannelSftp setupJsch() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts("C:/Users/andre/.ssh/known_hosts");
        session = jsch.getSession(username, remoteHost);
        session.setPort(2000);
        session.setPassword(password);
        session.connect();
        return (ChannelSftp) session.openChannel("sftp");
    }

    public void closeSession() {
        if (session!=null) {
            session.disconnect();
        }
    }
}
