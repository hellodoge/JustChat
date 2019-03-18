package messenger.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Protocol {
	public enum TypesOfPackets {
		MESSAGE, NAME, ID, PING, PASSWORD, CLIENTS_LIST, UNDEFINED
	}
	private final static String messagePrefix = "&M";
	private final static String namePrefix = "&N";
	private final static String idPrefix = "&I";
	private final static String pingPrefix = "&P";
	private final static String passwordPrefix = "&K";
	private final static String clientsListPrefix = "&L";

	public static TypesOfPackets getPacketType(DatagramPacket packet) {
		switch (new String(packet.getData()).substring(0,messagePrefix.length())) {
			case messagePrefix:
				return TypesOfPackets.MESSAGE;
			case namePrefix:
				return TypesOfPackets.NAME;
			case idPrefix:
				return TypesOfPackets.ID;
			case pingPrefix:
				return TypesOfPackets.PING;
			case passwordPrefix:
				return TypesOfPackets.PASSWORD;
			case clientsListPrefix:
				return TypesOfPackets.CLIENTS_LIST;
			default:
				return TypesOfPackets.UNDEFINED;
		}
	}

	public static String setTypePacketMessage(String content) {
		return messagePrefix + content;
	}
	public static String setTypePacketName(String content) {
		return namePrefix + content;
	}
	public static String setTypePacketId(String content) {
		return idPrefix + content;
	}
	public static String setTypePacketPassword(String content) {
		return passwordPrefix + content;
	}
	public static String setTypePacketClientsList(String content) {
		return clientsListPrefix + content;
	}
	public static void sendPing(DatagramSocket socket, InetAddress ip, int port) {
		try {
			socket.send(new DatagramPacket(pingPrefix.getBytes(), pingPrefix.getBytes().length, ip, port));
		} catch (IOException e) {}
	}
	public static void sendClientsListRequest(DatagramSocket socket, InetAddress ip, int port) {
		try {
			socket.send(new DatagramPacket(clientsListPrefix.getBytes(), clientsListPrefix.getBytes().length, ip, port));
		} catch (IOException e) {}
	}
}
