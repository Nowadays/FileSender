import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ReceiveDiscoveryPacketThread extends Thread {

    private ArrayList<Host> listOfHost;
    private InetAddress myAddress;
    private DatagramSocket datagramSocket;

    public ReceiveDiscoveryPacketThread() throws UnknownHostException, SocketException {
        super();
        this.listOfHost = new ArrayList<>();
        myAddress = InetAddress.getLocalHost();
        this.datagramSocket = new DatagramSocket(5557);
    }

    public void printDiscoveredHosts() {
        if(this.listOfHost.size() == 0){
            System.out.println("No hosts, nothing to display");
        }else{
            for (int i = 0; i < this.listOfHost.size(); i++) {
                System.out.println(i+1 + this.listOfHost.get(i).toString());
            }
        }
    }

    public Host getHost(int index){
        if(this.listOfHost.get(index) != null){
            return this.listOfHost.get(index);
        }else{
            return null;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[1], 0);
                InetAddress receivedAddress = null;
                datagramSocket.receive(datagramPacket);
                System.out.println("Packet received");
                receivedAddress = datagramPacket.getAddress();
                if (!receivedAddress.getHostAddress().equals(this.myAddress.getHostAddress())) {
                    int sourcePort = datagramPacket.getPort();
                    if (!this.listOfHost.contains(receivedAddress.getHostAddress())) {
                        this.listOfHost.add(new Host(receivedAddress.getHostName(), receivedAddress, sourcePort));
                    }
                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        try {
            ReceiveDiscoveryPacketThread receiveDiscoveryPacketThread = new ReceiveDiscoveryPacketThread();
            receiveDiscoveryPacketThread.start();
        } catch (UnknownHostException e) {
            System.out.println("Can't bind");
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
