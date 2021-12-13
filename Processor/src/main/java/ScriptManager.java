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

    protected ScriptManager(int port) throws RemoteException {
        this.port = port;
        ResourcesThread resourcesThread = new ResourcesThread();
        resourcesThread.start();
        HeartbeatReceiver heartbeatReceiver = new HeartbeatReceiver();
        heartbeatReceiver.start();
        HeartbeatSender heartbeatSender = new HeartbeatSender();
        heartbeatSender.start();
    }

    @Override
    public String processScript(Script s) throws RemoteException {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        s.setId(id);
        if (scripts.isEmpty()) {
            if (getAverage() < 80) {
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

    public class ResourcesThread extends Thread {
        @Override
        public void run() {
            int aux = 0;
            while (true) {
                if (aux == 9) {
                    aux = 0;
                }
                cpu.add(aux, ResourcesManager.getCPU());
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
                    if (getAverage() < 80) {
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
                System.out.println(cpu);
                double ram = 43;
                double disk = 0;
                HashMap<Long, String> threads = new HashMap<>();
                Resources resources = new Resources(disk, ram, cpu);
                Thread.getAllStackTraces().keySet().forEach((t) ->
                        threads.put(t.getId(), t.getState().toString())
                );
                Heartbeat heartbeat = new Heartbeat(port, threads, resources);
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
                    System.out.println("Success!");
                    Model m = new Model(port, s.getId(), output);
                    ModelManagerInterface modelManager = (ModelManagerInterface) Naming.lookup("rmi://localhost:2200/modelmanager");
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
}
