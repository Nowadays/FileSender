import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ReceiveDiscoveryPacketThread extends Thread {
	
	private ArrayList<Host> listOfHost;
	private InetAddress myAddress;
	private DatagramSocket datagramSocket;
	private int myPort;
	
	public ReceiveDiscoveryPacketThread (int port) throws UnknownHostException, SocketException {
		super();
		this.myPort = port;
		this.listOfHost = new ArrayList<>();
		myAddress = InetAddress.getLocalHost();
		this.datagramSocket = new DatagramSocket(this.myPort);
	}
	
	public static void main (String[] args) {
		try {
			ReceiveDiscoveryPacketThread receiveDiscoveryPacketThread = new ReceiveDiscoveryPacketThread(5557);
			receiveDiscoveryPacketThread.start();
		} catch (UnknownHostException e) {
			System.out.println("Can't bind");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void printDiscoveredHosts () {
		if (this.listOfHost.size() == 0) {
			System.out.println("No hosts, nothing to display");
		} else {
			for (int i = 0; i < this.listOfHost.size(); i++) {
				System.out.println((i + 1) + "-  " + this.listOfHost.get(i).toString());
			}
		}
	}
	
	public Host getHost (int index) {
		if (this.listOfHost.get(index) != null) {
			return this.listOfHost.get(index);
		} else {
			return null;
		}
	}
	
	@Override
	public void run () {
		System.out.println("Waiting on port:" + this.myPort);
		while (true) {
			try {
				checkArrayForExpiredHost();
				DatagramPacket datagramPacket = new DatagramPacket(new byte[1], 0);
				InetAddress receivedAddress = null;
				datagramSocket.receive(datagramPacket);
				receivedAddress = datagramPacket.getAddress();
				if (!receivedAddress.getHostAddress().equals(this.myAddress.getHostAddress())) {
					int sourcePort = datagramPacket.getPort();
					if (!checkIfArrayContainsIP(receivedAddress.getHostAddress())) {
						this.listOfHost.add(new Host(receivedAddress));
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkIfArrayContainsIP (String ip) {
		boolean isFound = false;
		int i = 0;
		while (i < this.listOfHost.size() && !isFound) {
			if (this.listOfHost.get(i).getAddress().getHostAddress().equalsIgnoreCase(ip)) {
				isFound = true;
			}
			++i;
		}
		return isFound;
	}
	
	private void checkArrayForExpiredHost () {
		if (this.listOfHost.size() > 0) {
			Iterator<Host> iterator = this.listOfHost.iterator();
			Host currentHost;
			
			while (iterator.hasNext()) {
				currentHost = iterator.next();
				if (currentHost.checkTimeToLive(System.currentTimeMillis())) {
					iterator.remove();
				}
			}
		}
	}
}
