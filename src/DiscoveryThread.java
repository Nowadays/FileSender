import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class DiscoveryThread extends Thread {
	
	private final int INTERVAL_TIME = 10000;//10 secondes
	private final DatagramSocket datagramSocket;
	private int myPort;
	private InetAddress myAddress;
	private HashMap<String, InetAddress> hosts;
	private InetAddress broadCastAddress;
	
	public DiscoveryThread (int myPort) throws UnknownHostException, SocketException {
		super();
		this.myPort = myPort;
		this.myAddress = InetAddress.getLocalHost();
		this.broadCastAddress = InetAddress.getByName("255.255.255.255");
		datagramSocket = new DatagramSocket(this.myPort);
		System.out.println("Starting UDP discovery on port: " + this.myPort);
	}
	
	public static void main (String[] args) {
		try {
			DiscoveryThread discoveryThread = new DiscoveryThread(5556);
			discoveryThread.start();
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
	}
	
	private void sendBeacon () throws IOException {
		byte[] beacon = new byte[1];
		DatagramPacket packet = new DatagramPacket(beacon, beacon.length, this.broadCastAddress, 5557);
		datagramSocket.send(packet);
	}
	
	@Override
	public void run () {
		while (true) {
			try {
				sendBeacon();
				Thread.sleep(INTERVAL_TIME);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
