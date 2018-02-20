import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private InputStream inputStream;
	private InetAddress myAddress;
	private int tcpPort;
	
	
	public Receiver(int tcpPort) {
		this.tcpPort = tcpPort;
	}
	
	public void open() throws IOException {
		this.serverSocket = new ServerSocket(this.tcpPort);//TCP port
		this.myAddress = InetAddress.getLocalHost();
		System.out.println(this.myAddress.getHostAddress());
		System.out.println("Waiting on tcp port: " + this.tcpPort);
		this.socket = this.serverSocket.accept();
		this.inputStream = this.socket.getInputStream();
		receive();
	}
	
	private void receive() throws IOException {
		DataInputStream dataInputStream;
		DataOutputStream dataOutputStream;
		
		dataInputStream = new DataInputStream(this.inputStream);
		byte arrayOfBytes[] = new byte[8192];
		
		int numberOfBytesRead = 0;
		int totalNumberOfBytesReceived = 0;
		String fileName = dataInputStream.readUTF();
		
		System.out.println("File to save: " + fileName);
		dataOutputStream = new DataOutputStream(new FileOutputStream("../" + fileName));
		while ((numberOfBytesRead = dataInputStream.read(arrayOfBytes)) > 0) {
			totalNumberOfBytesReceived += numberOfBytesRead;
			dataOutputStream.write(arrayOfBytes, 0, numberOfBytesRead);
		}
		dataInputStream.close();
		dataOutputStream.close();
		System.out.println("File :" + fileName + " succesfully saved on disk, number of bytes received: " + totalNumberOfBytesReceived);
	}
	
	public static void main(String[] args) throws IOException {
		Receiver receiver = new Receiver(5555);
		receiver.open();
	}
}
