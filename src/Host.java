import java.net.InetAddress;

public class Host {
    private String hostName;
    private InetAddress address;

    public Host(String hostName, InetAddress address, int listeningPort) {
        this.hostName = hostName;
        this.address = address;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == String.class && ((String) obj.toString()).equals(this.hostName);
    }

    @Override
    public String toString() {
        return "Host name: " + this.hostName + ", with ip address: " + this.address.getHostAddress();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
