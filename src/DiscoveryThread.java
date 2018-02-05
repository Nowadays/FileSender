import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class DiscoveryThread extends Thread {

    private int myPort;
    private InetAddress myAddress;
    private HashMap<String, InetAddress> hosts;
    private final int intervalTime = 2000;
    private InetAddress broadCastAddress;
    private final DatagramSocket datagramSocket;

    public DiscoveryThread(int myPort) throws UnknownHostException, SocketException {
        super();
        this.myPort = myPort;
        this.myAddress = InetAddress.getLocalHost();
        this.broadCastAddress = InetAddress.getByName("192.168.1.255");
        datagramSocket = new DatagramSocket(this.myPort);

    }

    private void sendBeacon() throws IOException {
        byte[] beacon = new byte[1];
        DatagramPacket packet = new DatagramPacket(beacon, beacon.length, this.broadCastAddress, 5557);
        datagramSocket.send(packet);
    }


    @Override
    public void run() {
        while (true){
            try {
                sendBeacon();
                Thread.sleep(intervalTime);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        try {
            DiscoveryThread discoveryThread = new DiscoveryThread(5556);
            discoveryThread.start();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

}
