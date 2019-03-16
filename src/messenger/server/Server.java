package messenger.server;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class Server {
	private int port;
	private volatile String name;
	private DatagramSocket socket;
	protected Thread serverRun, receive, manage, messageHandler, packetSender;
	private volatile Queue<DatagramPacket> messages = new LinkedList<>();
	private volatile LinkedList<ClientOfServer> clients = new LinkedList<>();
	private volatile Queue<DatagramPacket> packets = new LinkedList<>();
	private volatile boolean serverIsRunning = false;
	private final int timeout;
	private final int timeoutKick;

	Server (ServerConfiguration serverConfiguration) {
		this.port = serverConfiguration.getPort();
		this.name = serverConfiguration.getName();
		this.timeout = serverConfiguration.getTimeout();
		this.timeoutKick = serverConfiguration.getTimeoutKick();
		try {
			this.socket = new DatagramSocket(this.port);
		} catch (SocketException e) {
			System.out.println("Port " + this.port + " can't be used");
			System.exit(1);
		}
		this.serverRun = new Thread(new Runnable() {
			@Override
			public void run() {
				serverIsRunning = true;
				manage();
				receive();
				messageHandler();
				packetSender();
			}
		}, "ServerRunThread");
		this.serverRun.start();
	}

	private void manage() {
		manage = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					for (int i = 0; i < clients.size(); i++) {
						ClientOfServer client = clients.get(i);
						if (!client.isConnected()) continue;
						if (System.currentTimeMillis() - client.getLastAccessed() > (long) timeoutKick) {
							addMessage(messenger.network.Protocol.setTypePacketMessage(client.getName() + " disconnected"));
							client.connected(false);
						} else if (System.currentTimeMillis() - client.getLastAccessed() > (long) timeout){
							messenger.network.Protocol.sendPing(socket, client.getIp(), client.getPort());
						}
						try {
							Thread.sleep(2000);
						} catch (Exception e) {}
					}
				} while (serverIsRunning);
			}
		}, "ServerManageThread");
		manage.start();
	}

	private void receive() {
		receive = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {}
					packetHandler(packet);
				} while (serverIsRunning);
			}
		}, "ServerReceiveThread");
		receive.start();
	}

	private void packetHandler(DatagramPacket packet) {
		String dataString = new String(packet.getData());
		messenger.network.Protocol.TypesOfPackets typeOfPacket;
		typeOfPacket = messenger.network.Protocol.getPacketType(packet);
		ClientOfServer client = findClient(packet);
		if (client != null) {
			client.setLastAccessed(System.currentTimeMillis());
			if (!client.isConnected()) {
				client.connected(true);
				addMessage(messenger.network.Protocol.setTypePacketMessage(client.getName() + " connected"));
			}
		}
		switch (typeOfPacket) {
			case NAME:
				if (isConnected(packet)) {
					client.setName(dataString.substring(2));
				} else {
					clients.add(new ClientOfServer(packet.getAddress(), packet.getPort(), dataString.substring(2)));
					byte[] content = new String(messenger.network.Protocol.setTypePacketMessage(findClient(packet).getName() + " joined")).getBytes();
					messages.add(new DatagramPacket(content, content.length, packet.getAddress(), packet.getPort()));
				}
				byte[] content = messenger.network.Protocol.setTypePacketName(name).getBytes();
				packets.add(new DatagramPacket(content, content.length, packet.getAddress(), packet.getPort()));
				break;
			case MESSAGE:
				messages.add(packet);
				break;
		}
	}

	private boolean isConnected(DatagramPacket packet) {
		for (ClientOfServer client : clients) {
			if (client.getIp().equals(packet.getAddress()) && client.getPort() == packet.getPort()) {
				return true;
			}
		}
		return false;
	}

	private ClientOfServer findClient(DatagramPacket packet) {
		for (ClientOfServer client : clients) {
			if (client.getIp().equals(packet.getAddress()) && client.getPort() == packet.getPort()) {
				return client;
			}
		}
		return null;
	}

	private void messageHandler() {
		messageHandler = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					if (!messages.isEmpty()) {
						DatagramPacket parentPacket;
						try {
							parentPacket = messages.remove();
						} catch (Exception e) {
							continue;
						}
						String dataString = new String(parentPacket.getData());
						for (ClientOfServer client : clients) {
							if (parentPacket.getAddress().equals(client.getIp()) && parentPacket.getPort() == client.getPort()) {
								dataString = (char)27 + "[31m" + name + "@" + client.getName() + ": " + (char)27 + "[37m" + dataString.substring(2);
								dataString = messenger.network.Protocol.setTypePacketMessage(dataString);
								break;
							}
						}
						for (ClientOfServer client : clients) {
							if (!client.isConnected()) continue;
							if (parentPacket.getAddress().equals(client.getIp()) && parentPacket.getPort() == client.getPort()) continue;
							DatagramPacket packet = new DatagramPacket(dataString.getBytes(), dataString.getBytes().length, client.getIp(), client.getPort());
							try {
								socket.send(packet);
							} catch (IOException e) {}
						}
					} else {
						Thread.yield();
					}
				} while (serverIsRunning);
			}
		}, "MessageHandlerThread");
		messageHandler.start();
	}

	private void packetSender() {
		packetSender = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					if (!packets.isEmpty()) {
						try {
							socket.send(packets.remove());
						} catch (IOException e) {}
					}
				} while (serverIsRunning);
			}
		}, "PacketSenderThread");
		packetSender.start();
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isServerIsRunning() {
		return serverIsRunning;
	}

	protected void printMessages() {
		for (DatagramPacket message : messages) {
			System.out.println(new String(message.getData()));
		}
	}

	protected void printClients() {
		for (ClientOfServer client : clients) {
			if (!client.isConnected()) continue;
			System.out.println(client.getIp().toString().substring(1) + " PORT: " + client.getPort() + " LOGIN: " + client.getName() + " ID: " + client.getId());
		}
	}

	public void shutDown() {
		this.serverIsRunning = false;
		this.manage.interrupt();
		this.socket.close();
	}
	public void addMessage(String message) {
		try {
			this.messages.add(new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName("127.0.0.1"), 0));
		} catch (UnknownHostException e) {}
	}
}
