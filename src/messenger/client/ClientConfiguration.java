package messenger.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientConfiguration {
	private int port = 8080;
	private String name = "Anonymous";
	private InetAddress ip = null;
	private String password = null;
	private int timeout = 10000;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
