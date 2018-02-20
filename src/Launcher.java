import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Launcher {
	
	private static Scanner scanner;
	private ReceiveDiscoveryPacketThread receiveDiscoveryPacketThread;
	private DiscoveryThread discoveryThread;
	private Receiver receiver;
	
	
	Launcher () throws UnknownHostException, SocketException {
		this.receiveDiscoveryPacketThread = new ReceiveDiscoveryPacketThread(5557);//Destination UDP port
		this.discoveryThread = new DiscoveryThread(5556);//UDP port
		this.receiver = new Receiver(5555);
		
		this.discoveryThread.startUDPClient();
		this.receiveDiscoveryPacketThread.startUDPServer();
		
		this.receiver.startServer();
	}
	
	private static void displayMenu () {
		System.out.println("1- Send a file");
		System.out.println("2- Stop server");
		System.out.println("3- Print discovered hosts");
		System.out.println("0- exit program");
	}
	
	private void sendFile () {
		scanner = new Scanner(System.in);
		this.receiveDiscoveryPacketThread.printDiscoveredHosts();
		int choice;
		
		System.out.println("Choose a destination:");
		choice = Integer.parseInt(scanner.nextLine());
		
		System.out.println("Input file name:");
		String filename = scanner.nextLine();
		File fileToSend = new File(filename);
		try {
			Sender sender = new Sender(this.receiveDiscoveryPacketThread.getHost(choice - 1));
			sender.writeFileToOutput(fileToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printHosts () {
		this.receiveDiscoveryPacketThread.printDiscoveredHosts();
	}
	
	public static void main (String[] args) {
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
						launcher.receiver.stopServer();
						break;
					case 3:
						launcher.printHosts();
						break;
					case 0: {
						try {
							launcher.receiver.stopServer();
							launcher.discoveryThread.stopUDPClient();
							launcher.receiveDiscoveryPacketThread.stopUDPServer();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				}
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Error launching the application");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
