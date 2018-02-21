import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender {
	
	private InetAddress myAddress;
	private Socket socket;
	private OutputStream outputStream;
	
	public Sender (Host destination) throws IOException {
		this.myAddress = InetAddress.getLocalHost();
		this.establishConnexion(destination.getAddress(), 5555);//TCP port
	}
	
	private void establishConnexion (InetAddress destinationAddress, int destinationPortNumber) throws IOException {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(destinationAddress, destinationPortNumber);
		this.socket = new Socket();
		this.socket.connect(inetSocketAddress);
		this.outputStream = this.socket.getOutputStream();
		System.out.println("Connexion established");
	}
	
	
	public void writeFileToOutput (File fileToSend) throws IOException {
		DataOutputStream dataOutputStream = null;
		DataInputStream dataInputStream = null;
		try {
			dataOutputStream = new DataOutputStream(new BufferedOutputStream(this.outputStream));
			String fileName = fileToSend.getName();
			long fileSize = fileToSend.length();
			System.out.println("Filename to send: " + fileName);
			int numberOfBytesRead = 0;
			byte arrayOfByte[] = new byte[524288];
			dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileToSend)));
			if (this.outputStream != null) {
				System.out.println("Number of bytes to send: " + fileSize);
				System.out.println("Writing file to output...");
				dataOutputStream.writeUTF(fileName);
				
				while ((numberOfBytesRead = dataInputStream.read(arrayOfByte)) > 0) {
					dataOutputStream.write(arrayOfByte, 0, numberOfBytesRead);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataInputStream != null) {
				dataInputStream.close();
			}
			if (dataOutputStream != null) {
				dataOutputStream.close();
			}
		}
	}
	
	public void closeSocket(){
		if(this.socket != null && !this.socket.isClosed()){
			try {
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString () {
		return this.myAddress.getHostAddress();
	}
	
}
