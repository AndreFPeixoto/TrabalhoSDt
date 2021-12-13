import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

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

    public static boolean downloadScript(String name) {
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

    public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}
