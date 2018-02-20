import java.net.InetAddress;
import java.util.Objects;

public class Host {
	private InetAddress address;
	private long timeWhenReceived = System.currentTimeMillis();
	
	
	public Host(InetAddress address) {
		this.address = address;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	public long getTimeWhenReceived() {
		return timeWhenReceived;
	}
	
	public void setTimeWhenReceived(long timeWhenReceived) {
		this.timeWhenReceived = timeWhenReceived;
	}
	
	public boolean checkTimeToLive(long currentTime) {
		long deltaTime = currentTime - this.timeWhenReceived;
		return deltaTime > 500000;
	}
	
	@Override
	public boolean equals(Object o) {
		System.out.println("test");
		if (this.address.getHostAddress() == o) return true;
		if (o == null || this.address.getHostAddress().getClass() != o.getClass()) return false;
		Host host = (Host) o;
		return Objects.equals(address.getHostAddress(), host.address.getHostAddress());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.address.getHostAddress());
	}
	
	@Override
	public String toString() {
		return "Host name: " + this.address.getHostName() + ", with ip address: " + this.address.getHostAddress();
	}
	
}
