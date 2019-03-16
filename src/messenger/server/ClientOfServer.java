package messenger.server;

import java.net.InetAddress;

public class ClientOfServer {

	private InetAddress ip;
	private int port;
	private final int id;
	private static int idCounter = 0;
	private String name;

	ClientOfServer (InetAddress ip, int port, String name) {
		this.ip = ip;
		this.port = port;
		this.id = idCounter++;
		this.name = name;

	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
