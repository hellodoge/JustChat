package messenger.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientConfiguration {
	private int port = 8080;
	private String name = "Anonymous";
	private InetAddress ip = null;
	private int nameLen = name.length();

	public void setPort(int port) {
		this.port = port;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIp(String ip) {
		try {
			this.ip = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("Server ip assignment failure...");
			System.exit(1);
		}
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public InetAddress getIp() {
		return ip;
	}
}
