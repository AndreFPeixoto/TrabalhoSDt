import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorManagerInterface {

    HashMap<Integer, Heartbeat> processors = new HashMap<>();
    HashMap<Integer, Thread> counter = new HashMap<>();
    HashMap<Integer, Thread> brains = new HashMap<>();

    protected ProcessorManager() throws RemoteException {
        HeartbeatReceiver heartbeatReceiver = new HeartbeatReceiver();
        heartbeatReceiver.start();
        BrainReceiver brainReceiver = new BrainReceiver();
        brainReceiver.start();
    }

    @Override
    public int requestProcess(Script s) throws RemoteException {
        return getProcessor();
    }

    public class HeartbeatReceiver extends Thread {
        protected MulticastSocket socket;
        protected byte[] buf = new byte[3000];

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
                    if (!processors.containsKey(heartbeat.getProcessorID())) {
                        processors.put(heartbeat.getProcessorID(), heartbeat);
                    } else {
                        Thread hc = counter.get(heartbeat.getProcessorID());
                        hc.stop();
                    }
                    HeartbeatCounter hc = new HeartbeatCounter(heartbeat.getProcessorID());
                    counter.put(heartbeat.getProcessorID(), hc);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public class HeartbeatCounter extends Thread {
        int id;

        public HeartbeatCounter(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            int count = 0;
            while (true) {
                if (count > 30) {
                    processors.remove(id);
                    int newProc = getProcessor();
                    System.out.println("resume processor " + id + " tasks to processor " + newProc);
                    try {
                        ScriptManagerInterface scriptManager = (ScriptManagerInterface) Naming.lookup("rmi://localhost:" + newProc + "/scriptmanager");
                        scriptManager.resumeRequests(id);
                    } catch (NotBoundException | MalformedURLException | RemoteException e) {
                        e.printStackTrace();
                    }
                    this.stop();
                }
                count++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class BrainReceiver extends Thread {
        protected MulticastSocket socket;
        protected byte[] buf = new byte[2000];

        @Override
        public void run() {
            try {
                socket = new MulticastSocket(4446);
                InetAddress group = InetAddress.getByName("233.0.0.0");
                socket.joinGroup(group);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    int brain = (int) Utils.convertFromBytes(buf);
                    if (brains.containsKey(brain)) {
                        Thread bc = brains.get(brain);
                        bc.stop();
                    }
                    BrainCounter counter = new BrainCounter(brain);
                    brains.put(brain, counter);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public class BrainCounter extends Thread {
        int id;

        public BrainCounter(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            int count = 0;
            while (true) {
                if (count > 30) {
                    brains.remove(id);
                    this.stop();
                }
                count++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getProcessor() {
        if (!processors.isEmpty()) {
            double cpu = 100;
            int processor = 0;
            for (Heartbeat h : processors.values()) {
                if (h.getCpu() < cpu) {
                    cpu = h.getCpu();
                    processor = h.getProcessorID();
                }
            }
            return processor;
        } else {
            return 0;
        }
    }
}
