package utils;

import common.PacketProto;

import static common.PacketProto.Packet.newBuilder;

public class MessageUtil {
	
	public static PacketProto.Packet turnToPacket(String data) {
		PacketProto.Packet.Builder builder = newBuilder();
		builder.setPacketType(PacketProto.Packet.PacketType.DATA);
		builder.setData(data);
		PacketProto.Packet packetResp = builder.build();
		return packetResp;
	}

}