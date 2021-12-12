import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class Utils {

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
