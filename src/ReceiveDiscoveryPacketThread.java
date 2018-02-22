import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ReceiveDiscoveryPacketThread extends Thread {
	
	private ArrayList<Host> listOfHost;
	private InetAddress myAddress;
	private DatagramSocket datagramSocket;
	private int myPort;
	private boolean isUDPServerStarted = false;
	
	/**
	 * Constructor, take the UDP listening port as parameter
	 * @param port UDP listening port
	 * @throws UnknownHostException
	 * @throws SocketException
	 */
	public ReceiveDiscoveryPacketThread (int port) throws UnknownHostException, SocketException {
		super();
		this.myPort = port;
		this.listOfHost = new ArrayList<>();
		myAddress = InetAddress.getLocalHost();
		this.datagramSocket = new DatagramSocket(this.myPort);
	}
	
	
	/**
	 * Display currently discovered hosts
	 */
	public void printDiscoveredHosts () {
		if (this.listOfHost.size() == 0) {
			System.out.println("No hosts, nothing to display");
		} else {
			for (int i = 0; i < this.listOfHost.size(); i++) {
				System.out.println((i + 1) + "-  " + this.listOfHost.get(i).toString());
			}
		}
	}
	
	/**
	 * Return the specified host at the index passed as parameter
	 * @param index the index of the target host in the list
	 * @return the host if it exists
	 */
	public Host getHost (int index) {
		if (this.listOfHost.get(index) != null) {
			return this.listOfHost.get(index);
		} else {
			return null;
		}
	}
	
	/**
	 * Start the UDP server
	 */
	public void startUDPServer () {
		this.isUDPServerStarted = true;
		this.start();
	}
	
	/**
	 * Pause the UDP server
	 */
	public void pauseUDPServer () {
		this.isUDPServerStarted = false;
	}
	
	/**
	 * Resume the server if paused
	 */
	public void resumeUDPServer(){
		if(!this.isUDPServerStarted){
			this.isUDPServerStarted = true;
		}
	}
	
	/**
	 * Try to stop the UDP server
	 */
	public void stopUDPServer () {
		this.isUDPServerStarted = false;
		if (!this.datagramSocket.isClosed()) {
			this.datagramSocket.close();
		}
	}
	
	
	/**
	 * This method will wait for datagram packet to arrive and then add the new hosts to the list if they are not already in it
	 */
	@Override
	public void run () {
		System.out.println("Waiting on port:" + this.myPort);
		while (this.isUDPServerStarted) {
			try {
				checkArrayForExpiredHost();
				DatagramPacket datagramPacket = new DatagramPacket(new byte[1], 0);
				InetAddress receivedAddress = null;
				datagramSocket.receive(datagramPacket);
				receivedAddress = datagramPacket.getAddress();
				if (!receivedAddress.getHostAddress().equals(this.myAddress.getHostAddress())) {
					if (!checkIfArrayContainsIP(receivedAddress.getHostAddress())) {
						this.listOfHost.add(new Host(receivedAddress));
					}
				}
			} catch (IOException e) {
				System.out.println("Socket closed, server is probably paused or stopped");
			}
		}
	}
	
	/**
	 * Check the address IP passed in parameter doesn't already exist in the list
	 * @param ip the IP to check against the list
	 * @return true if the IP address is already in the list
	 */
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
	
	/**
	 * Check the list for expired hosts
	 */
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
	
	/**
	 * Get the size of the hosts list
	 * @return the size of the host list
	 */
	public int getListOfHostsSize(){
		return this.listOfHost.size();
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
	
}
