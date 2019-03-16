package messenger.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

public class Client {
	private int port;
	private String name;
	private InetAddress ip;
	private DatagramSocket socket;
	private Thread clientRun, packetSender, packetListener;
	private volatile boolean clientIsRunning = false;

	private volatile Queue <String> messageQueue = new LinkedList<>();

	private String serverName;

	Client (ClientConfiguration configuration) {
		this.port = configuration.getPort();
		this.ip = configuration.getIp();
		this.serverName = configuration.getIp().toString();
		this.name = configuration.getName();
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("Socket assignment failure");
			System.exit(1);
		}
		this.clientRun = new Thread(new Runnable() {
			@Override
			public void run() {
				clientIsRunning = true;
				messageQueue.add(messenger.network.Protocol.setTypePacketName(name));
				packetSender();
				packetListener();
			}
		}, "ServerRunThread");
		this.clientRun.start();
	}
	private void packetSender() {
		packetSender = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					if (!messageQueue.isEmpty()) {
						byte[] data = messageQueue.remove().getBytes();
						DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
						try {
							socket.send(packet);
						} catch (IOException e) {
							System.out.println("Unable to send this message");
						}
					} else {
						Thread.yield();
					}
				} while (clientIsRunning);
			}
		}, "packetSenderThread");
		this.packetSender.start();
	}
	private void packetListener() {
		packetListener = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {}
					packetHandler(packet);
				} while (clientIsRunning);
			}
		}, "PacketHandlerThread");
		packetListener.start();
	}
	private void packetHandler(DatagramPacket packet) {
		String dataString = new String(packet.getData());
		messenger.network.Protocol.TypesOfPackets typeOfPacket;
		typeOfPacket = messenger.network.Protocol.getPacketType(packet);
		switch (typeOfPacket) {
			case NAME:
				serverName = dataString.substring(2);
				break;
			case MESSAGE:
				System.out.println("\n" + dataString.substring(2));
				printConsolePrefix();
				break;
		}
	}
	protected synchronized void addMessage(String message) {
		this.messageQueue.add(message);
	}

	public boolean isClientIsRunning() {
		return clientIsRunning;
	}
	protected void shutDown() {
		this.clientIsRunning = false;
		this.socket.close();
	}
	protected String getName() {
		return name;
	}
	protected void printConsolePrefix() {
		System.out.print((char)27 + "[32m" + serverName + "@" + name + (char)27 + "[37m" + ": ");
	}
}
