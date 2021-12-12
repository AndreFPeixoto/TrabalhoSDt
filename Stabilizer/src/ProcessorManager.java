import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorManagerInterface {

    HashMap<Integer, Heartbeat> processors = new HashMap<>();

    protected ProcessorManager() throws RemoteException {
        HeartbeatReceiver heartbeatReceiver = new HeartbeatReceiver();
        heartbeatReceiver.start();
    }

    @Override
    public int requestProcess(Script s) throws RemoteException {
        if (!processors.isEmpty()) {
            double cpu = 100;
            double ram = 0;
            double disk = 0;
            int processor = 0;
            for (Heartbeat h : processors.values()) {
                if (h.getAvailable().getCpu() < cpu) {
                    cpu = h.getAvailable().getCpu();
                    processor = h.getProcessorID();
                }
                System.out.println("Stabilizer: I know processor " + h.getProcessorID());
            }
            return processor;
        } else {
            return 0;
        }
    }

    public class HeartbeatReceiver extends Thread {
        protected MulticastSocket socket;
        protected byte[] buf = new byte[2000];

        @Override
        public void run() {
            try {
                socket = new MulticastSocket(4446);
                InetAddress group = InetAddress.getByName("230.0.0.0");
                socket.joinGroup(group);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    Heartbeat heartbeat = (Heartbeat) Utils.convertFromBytes(buf);
                    processors.put(heartbeat.getProcessorID(), heartbeat);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
