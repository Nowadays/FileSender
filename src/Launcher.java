import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Launcher {
	
	private static Scanner scanner;
	private ReceiveDiscoveryPacketThread receiveDiscoveryPacketThread;
	private DiscoveryThread discoveryThread;
	
	
	private Launcher() throws UnknownHostException, SocketException {
		this.receiveDiscoveryPacketThread = new ReceiveDiscoveryPacketThread(5557);//Destination UDP port
		this.discoveryThread = new DiscoveryThread(5556);//UDP port
		this.discoveryThread.start();
		this.receiveDiscoveryPacketThread.start();
	}
	
	private static void displayMenu() {
		System.out.println("1- Send a file");
		System.out.println("2- Receive a file");
		System.out.println("3- Print discovered hosts");
		System.out.println("0- exit program");
	}
	
	private void sendFile() {
		scanner = new Scanner(System.in);
		this.receiveDiscoveryPacketThread.printDiscoveredHosts();
		int choice;
		
		System.out.println("Choose a destination:");
		choice = scanner.nextInt();
		
		System.out.println("Input file name:");
		String filename = scanner.nextLine();
		File fileToSend = new File(filename);
		try {
			Sender sender = new Sender(this.receiveDiscoveryPacketThread.getHost(choice - 1));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveFile() {
		Receiver receiver = new Receiver(5555);
		try {
			receiver.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printHosts() {
		this.receiveDiscoveryPacketThread.printDiscoveredHosts();
	}
	
	public static void main(String[] args) {
		try {
			Launcher launcher = new Launcher();
			
			int choice = Integer.MAX_VALUE;
			
			scanner = new Scanner(System.in);
			while (choice != 0) {
				displayMenu();
				choice = scanner.nextInt();
				switch (choice) {
					case 1:
						launcher.sendFile();
						break;
					case 2:
						launcher.receiveFile();
						break;
					case 3:
						launcher.printHosts();
						break;
					case 0:
						break;
				}
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Couldn't bind to port 4444");
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		
	}
}
