package messenger.server;

public class ServerConfiguration {
	private int port = 8080;
	private String name = "default";
	private int timeout = 5000;
	private int timeoutKick = 15000;

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getTimeoutKick() {
		return timeoutKick;
	}

	public void setTimeoutKick(int timeoutKick) {
		this.timeoutKick = timeoutKick;
	}
}
