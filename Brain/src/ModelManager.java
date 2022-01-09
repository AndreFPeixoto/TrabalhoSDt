import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelManager extends UnicastRemoteObject implements ModelManagerInterface {

    int port;
    HashMap<String, Model> models = new HashMap<>();
    HashMap<Integer, Thread> brains = new HashMap<>();

    protected ModelManager(int port) throws RemoteException {
        this.port = port;
        BrainReceiver brainReceiver = new BrainReceiver();
        brainReceiver.start();
        BrainSender brainSender = new BrainSender();
        brainSender.start();
    }

    @Override
    public void sendModel(Model m) throws RemoteException {
        setModel(m);
        shareModel(m);
    }

    @Override
    public Model requestModel(String requestID) throws RemoteException {
        if (models.containsKey(requestID)) {
            return models.get(requestID);
        } else {
            return askModel(requestID);
        }
    }

    @Override
    public List<String> getCompletedRequests(int processor) throws RemoteException {
        List<String> modelsId = new ArrayList<>();
        for (Model m : models.values()) {
            if (m.getProcessorID() == processor) {
                modelsId.add(m.getRequestID());
            }
        }
        return modelsId;
    }

    @Override
    public void setModel(Model m) throws RemoteException {
        models.put(m.getRequestID(), m);
    }

    @Override
    public Model getModel(String requestID) throws RemoteException {
        return models.get(requestID);
    }

    public class BrainSender extends Thread {
        protected DatagramSocket socket;
        byte[] data;

        @Override
        public void run() {
            while (true) {
                try {
                    socket = new DatagramSocket();
                    InetAddress group = InetAddress.getByName("233.0.0.0");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(port);
                    oos.flush();
                    data = bos.toByteArray();
                    DatagramPacket packet = new DatagramPacket(data, data.length, group, 4446);
                    socket.send(packet);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(15000);
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
                    int brain = (int) convertFromBytes(buf);
                    if (brain != port) {
                        BrainCounter counter = new BrainCounter(brain);
                        brains.put(brain, counter);
                    }
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

    public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    public void shareModel(Model m) {
        try {
            for (int b : brains.keySet()) {
                ModelManagerInterface manager = (ModelManagerInterface) Naming.lookup("rmi://localhost:" + b + "/modelmanager");
                manager.setModel(m);
            }
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public Model askModel(String requestID) {
        try {
            for (int b : brains.keySet()) {
                ModelManagerInterface manager = (ModelManagerInterface) Naming.lookup("rmi://localhost:" + b + "/modelmanager");
                Model m = manager.getModel(requestID);
                if (m != null) {
                    return m;
                }
            }
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
