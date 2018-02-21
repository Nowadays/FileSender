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
	private DataInputStream dataInputStream = null;
	private DataOutputStream dataOutputStream = null;
	
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
			while (this.isServerStarted) {
				this.startConnection();
				receive();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startConnection () throws IOException {
		this.serverSocket = new ServerSocket(this.tcpPort);//TCP port
	}
	
	private void receive () throws IOException {
		
		try {
			System.out.println("Waiting on tcp port: " + this.tcpPort);
			this.socket = this.serverSocket.accept();
			this.inputStream = this.socket.getInputStream();
			dataInputStream = new DataInputStream(new BufferedInputStream(this.inputStream));
			byte arrayOfBytes[] = new byte[524288];
			
			int numberOfBytesRead = 0;
			int totalNumberOfBytesReceived = 0;
			String fileName = dataInputStream.readUTF();
			
			System.out.println("File to save: " + fileName);
			dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
			while ((numberOfBytesRead = dataInputStream.read(arrayOfBytes)) > 0) {
				totalNumberOfBytesReceived += numberOfBytesRead;
				dataOutputStream.write(arrayOfBytes, 0, numberOfBytesRead);
			}
			System.out.println("File :" + fileName + " succesfully saved on disk, number of bytes received: " + totalNumberOfBytesReceived);
		} catch (IOException e) {
			System.out.println("Socket has been closed");
		} finally {
			this.closeStreams();
			this.serverSocket.close();
		}
	}
	
	public void startServer () {
		this.isServerStarted = true;
		this.start();
	}
	
	public void pauseServer () {
		this.isServerStarted = false;
	}
	
	public void stopServer () throws IOException {
		this.isServerStarted = false;
		if (this.serverSocket != null) {
			this.serverSocket.close();
		}
	}
	
	public void closeStreams() throws IOException {
		if(this.inputStream != null && this.dataOutputStream != null){
			this.inputStream.close();
			this.dataOutputStream.close();
		}
	}
	
	public static void main (String[] args) throws IOException {
		Receiver receiver = new Receiver(5555);
		receiver.startConnection();
	}
}
