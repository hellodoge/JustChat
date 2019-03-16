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

	Server (ServerConfiguration serverConfiguration) {
		this.port = serverConfiguration.getPort();
		this.name = serverConfiguration.getName();
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
					//TODO Thread receive
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
		switch (typeOfPacket) {
			case NAME:
				clients.add(new ClientOfServer(packet.getAddress(), packet.getPort(), dataString.substring(2)));
				byte[] content = messenger.network.Protocol.setTypePacketName(name).getBytes();
				packets.add(new DatagramPacket(content, content.length, packet.getAddress(), packet.getPort()));
				break;
			case MESSAGE:
				messages.add(packet);
				break;
		}
	}

	private void messageHandler() {
		messageHandler = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					if (!messages.isEmpty()) {
						DatagramPacket parentPacket = messages.remove();
						String dataString = new String(parentPacket.getData());
						for (ClientOfServer client : clients) {
							if (parentPacket.getAddress().equals(client.getIp()) && parentPacket.getPort() == client.getPort()) {
								//TODO поправить этот ужас
								String clientName = client.getName();
								int clientNameLen = clientName.length();
								clientName = clientName.substring(0,100);
								dataString = (char)27 + "[31m" + name + "@" + clientName + ": " + (char)27 + "[37m" + dataString.substring(2);
								dataString = messenger.network.Protocol.setTypePacketMessage(dataString);
								break;
							}
						}
						for (ClientOfServer client : clients) {
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
			System.out.println(client.getIp().toString().substring(1) + " PORT: " + client.getPort() + " LOGIN: " + client.getName() + " ID: " + client.getId());
		}
	}

	public void shutDown() {
		this.serverIsRunning = false;
		this.socket.close();
	}
	public void addMessage(String message) {
		try {
			this.messages.add(new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName("127.0.0.1"), 0));
		} catch (UnknownHostException e) {}
	}
}
