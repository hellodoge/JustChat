package messenger.server;

import java.net.InetAddress;

public class ClientOfServer {

	private InetAddress ip;
	private int port;
	private final int id;
	private static int idCounter = 1;
	private String name;
	private long lastAccessed;
	private boolean connected;
	private boolean accessIsAllowed;

	ClientOfServer (InetAddress ip, int port, String name) {
		this.ip = ip;
		this.port = port;
		this.id = idCounter++;
		this.accessIsAllowed = false;
		this.lastAccessed = System.currentTimeMillis();
		this.name = name.split(" ")[0].substring(0, Integer.parseInt(name.split(" ")[1]));
		this.connected = true;
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

	public void setName(String name) {
		this.name = name;
	}

	public long getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(long lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public void connected(boolean value) {
		this.connected = value;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setAccessIsAllowed(boolean value) {
		this.accessIsAllowed = value;
	}

	public boolean isAccessIsAllowed() {
		return accessIsAllowed;
	}
}
