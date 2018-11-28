package utils;

import packet.PacketProto;
import packet.PacketType;

import static packet.PacketProto.Packet.newBuilder;

public class MessageUtil {

	public static PacketProto.Packet turnToPacket(String data){
		return turnToPacket(data,null);
	}

    public static PacketProto.Packet turnToPacket(String data,String type) {
		PacketProto.Packet.Builder builder = newBuilder();
		builder.setPacketType(PacketProto.Packet.PacketType.DATA);
		builder.setData(data);
		if(type == null){
			builder.setType(PacketType.NORMALMSG);
		}else {
			builder.setType(type);
		}
		PacketProto.Packet packetResp = builder.build();
		return packetResp;
	}

}
