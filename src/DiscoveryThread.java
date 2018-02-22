import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class DiscoveryThread extends Thread {
	
	private final int INTERVAL_TIME = 2000;//10 secondes
	private final DatagramSocket datagramSocket;
	private int myPort;
	private InetAddress myAddress;
	private HashMap<String, InetAddress> hosts;
	private InetAddress broadCastAddress;
	private boolean isUdpClientStarted = false;
	
	public DiscoveryThread (int myPort) throws UnknownHostException, SocketException {
		super();
		this.myPort = myPort;
		this.myAddress = InetAddress.getLocalHost();
		this.broadCastAddress = InetAddress.getByName("255.255.255.255");
		datagramSocket = new DatagramSocket(this.myPort);
		System.out.println("Starting UDP discovery on port: " + this.myPort);
	}
	
	private void sendBeacon () throws IOException {
		byte[] beacon = new byte[1];
		DatagramPacket packet = new DatagramPacket(beacon, beacon.length, this.broadCastAddress, 5557);
		datagramSocket.send(packet);
	}
	
	public void startUDPClient () {
		this.isUdpClientStarted = true;
		this.start();
	}
	
	public void pauseUDPClient () {
		this.isUdpClientStarted = false;
	}
	
	public void stopUDPClient () {
		this.isUdpClientStarted = false;
		if (!this.datagramSocket.isClosed()) {
			this.datagramSocket.close();
		}
	}
	
	@Override
	public void run () {
		while (this.isUdpClientStarted) {
			try {
				sendBeacon();
				Thread.sleep(INTERVAL_TIME);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
