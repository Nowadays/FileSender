import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Receiver extends Thread {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private InputStream inputStream;
	private InetAddress myAddress;
	private int tcpPort;
	private boolean isServerStarted = false;
	
	
	public Receiver (int tcpPort) {
		this.tcpPort = tcpPort;
		try {
			this.myAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run () {
		try {
			while(true){
				if (this.isServerStarted) {
					startConnection();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startConnection () throws IOException {
		if (this.serverSocket == null) {
			this.serverSocket = new ServerSocket(this.tcpPort);//TCP port
		}
		System.out.println("Waiting on tcp port: " + this.tcpPort);
		this.socket = this.serverSocket.accept();
		this.inputStream = this.socket.getInputStream();
		receive();
	}
	
	private void receive () throws IOException {
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		try {
			
			dataInputStream = new DataInputStream(this.inputStream);
			byte arrayOfBytes[] = new byte[8192];
			
			int numberOfBytesRead = 0;
			int totalNumberOfBytesReceived = 0;
			String fileName = dataInputStream.readUTF();
			
			System.out.println("File to save: " + fileName);
			dataOutputStream = new DataOutputStream(new FileOutputStream(fileName));
			while ((numberOfBytesRead = dataInputStream.read(arrayOfBytes)) > 0) {
				totalNumberOfBytesReceived += numberOfBytesRead;
				dataOutputStream.write(arrayOfBytes, 0, numberOfBytesRead);
			}
			System.out.println("File :" + fileName + " succesfully saved on disk, number of bytes received: " + totalNumberOfBytesReceived);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dataInputStream.close();
			dataOutputStream.close();
			this.socket.close();
			this.serverSocket.close();
		}
	}
	
	public void startServer(){
		this.isServerStarted = true;
		this.start();
	}
	
	public void stopServer(){
		this.isServerStarted = false;
	}
	
	public static void main (String[] args) throws IOException {
		Receiver receiver = new Receiver(5555);
		receiver.startConnection();
	}
}
