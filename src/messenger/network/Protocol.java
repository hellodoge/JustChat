package messenger.network;

import java.net.DatagramPacket;

public class Protocol {
	public enum TypesOfPackets {
		MESSAGE, NAME, ID, PING, UNDEFINED
	}
	private final static String messagePrefix = "&M";
	private final static String namePrefix = "&N";
	private final static String idPrefix = "&I";
	private final static String pingPrefix = "&P";

	public static TypesOfPackets getPacketType(DatagramPacket packet) {
		switch (new String(packet.getData()).substring(0,messagePrefix.length())) {
			case messagePrefix:
				return TypesOfPackets.MESSAGE;
			case namePrefix:
				return TypesOfPackets.NAME;
			case idPrefix:
				return TypesOfPackets.ID;
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
	public static String getPing() {
		return pingPrefix;
	}
}
