package utils;

import core.packet.PacketProto;
import core.packet.PacketType;

import static core.packet.PacketProto.Packet.newBuilder;
/**
 * @ClassName MessageUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
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
