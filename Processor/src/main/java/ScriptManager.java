import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ScriptManager extends UnicastRemoteObject implements ScriptManagerInterface {

    int port;
    List<Script> scripts = new ArrayList<>();
    List<Integer> cpu = new ArrayList<>();
    HashMap<Integer, Heartbeat> heartbeats = new HashMap<>();
    HashMap<Integer, Thread> brains = new HashMap<>();

    protected ScriptManager(int port) throws RemoteException {
        this.port = port;
        ResourcesThread resourcesThread = new ResourcesThread();
        resourcesThread.start();
        HeartbeatReceiver heartbeatReceiver = new HeartbeatReceiver();
        heartbeatReceiver.start();
        BrainReceiver brainReceiver = new BrainReceiver();
        brainReceiver.start();
        HeartbeatSender heartbeatSender = new HeartbeatSender();
        heartbeatSender.start();
    }

    @Override
    public String processScript(Script s) throws RemoteException {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        s.setId(id);
        if (scripts.isEmpty()) {
            if (getAverage() > 25) {
                runScript(s);
            } else {
                scripts.add(s);
                ScriptsThread scriptsThread = new ScriptsThread();
                scriptsThread.start();
            }
        } else {
            ScriptsThread scriptsThread = new ScriptsThread();
            scriptsThread.start();
        }
        return id;
    }

    @Override
    public void resumeRequests(int processor) throws RemoteException {
        try {
            List<Script> pending = heartbeats.get(processor).getTasks();
            ModelManagerInterface modelManager = (ModelManagerInterface) Naming.lookup("rmi://localhost:2200/modelmanager");
            List<String> requests = modelManager.getCompletedRequests(processor);
            List<Script> found = new ArrayList<>();
            for (String r : requests) {
                for (Script p : pending) {
                    if (p.getId().equals(r)) {
                        found.add(p);
                    }
                }
            }
            pending.removeAll(found);
            executeUnfinishedReq(pending);
        } catch (NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<Integer, Heartbeat> recovery() throws RemoteException {
        return heartbeats;
    }

    public class ResourcesThread extends Thread {
        @Override
        public void run() {
            int aux = 0;
            while (true) {
                if (aux == 9) {
                    aux = 0;
                }
                cpu.add(aux, ResourcesManager.getFreeCpu());
                aux++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ScriptsThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (!scripts.isEmpty()) {
                    if (getAverage() > 25) {
                        Script s = scripts.get(0);
                        runScript(s);
                        scripts.remove(0);
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
                    heartbeats.put(heartbeat.getProcessorID(), heartbeat);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public class HeartbeatSender extends Thread {
        protected DatagramSocket socket;
        byte[] data;

        @Override
        public void run() {
            while (true) {
                double cpu = getAverage();
                /*
                HashMap<Long, String> threads = new HashMap<>();
                Thread.getAllStackTraces().keySet().forEach((t) -> {
                            threads.put(t.getId(), t.getState().toString());
                            System.out.println(t.getId() + "/" + t.getName() + "/" + t.getState());
                        }
                );*/
                Heartbeat heartbeat = new Heartbeat(port, scripts, cpu);
                try {
                    socket = new DatagramSocket();
                    InetAddress group = InetAddress.getByName("230.0.0.0");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(heartbeat);
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

    public void runScript(Script s) {
        try {
            if (Utils.downloadScript(s.getName())) {
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
                    int b = (int) (Math.random() * (brains.size() - 1 + 1) + 0);
                    Model m = new Model(port, s.getId(), output);
                    ModelManagerInterface modelManager = (ModelManagerInterface) Naming.lookup("rmi://localhost:" + brains.keySet().toArray()[b] + "/modelmanager");
                    modelManager.sendModel(m);
                }
            } else {
                System.out.println("Error downloading the script");
            }
        } catch (NotBoundException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getAverage() {
        int total = 0;

        for (Integer i : cpu) {
            total += i;
        }
        return total / cpu.size();
    }

    public void executeUnfinishedReq(List<Script> scripts) {
        scripts.addAll(scripts);
    }
}
