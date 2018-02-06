import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender {

    private InetAddress myAddress;
    private Socket socket;
    private OutputStream outputStream;

    public Sender(Host destination) throws IOException {
        this.myAddress = InetAddress.getLocalHost();
        this.establishConnexion(destination.getAddress(), 5555);//TCP port
    }

    private void establishConnexion(InetAddress destinationAddress, int destinationPortNumber) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(destinationAddress, destinationPortNumber);
        this.socket = new Socket();
        this.socket.connect(inetSocketAddress);
        this.outputStream = this.socket.getOutputStream();
        System.out.println("Connexion established");
    }


    private void writeFileToOutput(File fileToSend) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.outputStream);
        DataInputStream dataInputStream = null;
        String fileName = fileToSend.getName();
        long fileSize = fileToSend.length();
        System.out.println("Filename to send: " + fileName);
        int numberOfBytesRead = 0;
        byte arrayOfByte[] = new byte[8192];
        dataInputStream = new DataInputStream(new FileInputStream(fileToSend));
        if (this.outputStream != null) {
            System.out.println("Number of bytes to send: " + fileSize);
            System.out.println("Writing file to output...");
            dataOutputStream.writeUTF(fileName);
            while ((numberOfBytesRead = dataInputStream.read(arrayOfByte)) > 0) {
                dataOutputStream.write(arrayOfByte, 0, numberOfBytesRead);
            }
            dataInputStream.close();
            dataOutputStream.close();
        }
    }

    @Override
    public String toString() {
        return this.myAddress.getHostAddress();
    }

}
