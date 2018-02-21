import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Launcher {
	
	private static Scanner scanner;
	private ReceiveDiscoveryPacketThread receiveDiscoveryPacketThread;
	private DiscoveryThread discoveryThread;
	private Receiver receiver;
	private Frame mainFrame;
	private FileDialog fileDialog;
	
	
	Launcher () throws UnknownHostException, SocketException {
		this.receiveDiscoveryPacketThread = new ReceiveDiscoveryPacketThread(5557);//Destination UDP port
		this.discoveryThread = new DiscoveryThread(5556);//UDP port
		this.receiver = new Receiver(5555);
		
		this.discoveryThread.startUDPClient();
		this.receiveDiscoveryPacketThread.startUDPServer();
		
		this.receiver.startServer();
		this.mainFrame = new Frame();
		this.mainFrame.setSize(new Dimension(500, 500));
		this.fileDialog = new FileDialog(this.mainFrame, "Choose file to send");
		this.fileDialog.setMode(FileDialog.LOAD);
		this.fileDialog.setMultipleMode(true);
	}
	
	private static void displayMenu () {
		System.out.println("1- Send a file");
		System.out.println("2- Stop server");
		System.out.println("3- Print discovered hosts");
		System.out.println("0- exit program");
	}
	
	private void sendFile () throws IOException {
		assert this.fileDialog != null;
		int choice;
		
		if (this.receiveDiscoveryPacketThread.getListOfHostsSize() > 0) {
			scanner = new Scanner(System.in);
			this.receiveDiscoveryPacketThread.printDiscoveredHosts();
			
			System.out.println("Choose a destination:");
			choice = Integer.parseInt(scanner.nextLine());
			
			Sender sender = new Sender(this.receiveDiscoveryPacketThread.getHost(choice - 1));
			
			this.fileDialog.setLocationRelativeTo(null);
			this.fileDialog.setVisible(true);
			
			File listOfFileToSend[] = this.fileDialog.getFiles();
			this.fileDialog.dispose();
			for (File file : listOfFileToSend) {
				sender.writeFileToOutput(file, this.receiveDiscoveryPacketThread.getHost(choice - 1));
			}
			sender.closeSocket();
		}else{
			System.out.println("No hosts detected, choose a destination manually:");
			String ipAddress = scanner.nextLine();
			Sender sender = new Sender(new Host(InetAddress.getByName(ipAddress)));
			
			this.fileDialog.setLocationRelativeTo(null);
			this.fileDialog.setVisible(true);
			
			File listOfFileToSend[] = this.fileDialog.getFiles();
			this.fileDialog.dispose();
			for (File file : listOfFileToSend) {
				sender.writeFileToOutput(file, new Host(InetAddress.getByName(ipAddress)));
			}
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
				choice = Integer.parseInt(scanner.nextLine());
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
