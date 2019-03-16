package messenger.server;

public class ServerConfiguration {
	private int port = 8080;
	private String name = "default";

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
}
