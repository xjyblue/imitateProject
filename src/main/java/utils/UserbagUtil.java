package utils;

import component.Equipment;
import component.MpMedicine;
import component.parent.Good;
import io.netty.channel.Channel;
import memory.NettyMemory;
import packet.PacketType;
import pojo.User;
import pojo.Userbag;


/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/13 15:58
 */
public class UserbagUtil {

    public static void refreshUserbag(Channel channel) {
        User user = NettyMemory.session2UserIds.get(channel);
        String bagResp = System.getProperty("line.separator")
                + "按b-物品编号使用蓝药"
                + "  按ww=物品编号装备武器";
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getTypeof().equals(Good.MPMEDICINE)) {
                MpMedicine mpMedicine = NettyMemory.mpMedicineMap.get(userbag.getWid());
                bagResp += System.getProperty("line.separator")
                        + "[格子id:" + userbag.getId()
                        + "] [物品id:" + mpMedicine.getId();
                bagResp += "] [数量:" + userbag.getNum() + "]";
                bagResp += " [名字:" + mpMedicine.getName() + "]";
                if (!mpMedicine.isImmediate()) {
                    bagResp += " [每秒恢复" + mpMedicine.getSecondValue() + "] [持续" + mpMedicine.getKeepTime() + "秒]";
                } else {
                    bagResp += " [即时回复]";
                }
            } else if (userbag.getTypeof().equals(Good.EQUIPMENT)) {
                Equipment equipment = NettyMemory.equipmentMap.get(userbag.getWid());
                bagResp += System.getProperty("line.separator")
                        + "[格子id:" + userbag.getId()
                        + "] [物品id:" + equipment.getId()
                        + "] [武器当前耐久度:" + userbag.getDurability()
                        + "] [武器名称:" + equipment.getName()
                        + "] [武器攻击力加成" + equipment.getAddValue()
                        + "] [武器数量:" + userbag.getNum() + "]";
            }
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(bagResp, PacketType.USERBAGMSG));
    }
}
