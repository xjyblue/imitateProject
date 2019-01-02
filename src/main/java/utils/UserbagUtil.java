package utils;

import component.good.CollectGood;
import component.good.Equipment;
import component.good.HpMedicine;
import component.good.MpMedicine;
import component.good.parent.PGood;
import io.netty.channel.Channel;
import context.ProjectContext;
import packet.PacketType;
import pojo.User;
import pojo.Userbag;


/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/13 15:58
 */
public class UserbagUtil {

    public static Userbag getUserbagByUserbagId(User user,String userbagId){
        for(Userbag userbag:user.getUserBag()){
            if(userbag.getId().equals(userbagId)){
                return userbag;
            }
        }
        return null;
    }

    public static void refreshUserbagInfo(Channel channel) {
        User user = ProjectContext.session2UserIds.get(channel);
        String bagResp = System.getProperty("line.separator")
                + "按b-物品编号使用蓝药"
                + "  按ww=物品编号装备武器";
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getTypeof().equals(PGood.MPMEDICINE)) {
                MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(userbag.getWid());
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
            } else if (userbag.getTypeof().equals(PGood.EQUIPMENT)) {
                Equipment equipment = ProjectContext.equipmentMap.get(userbag.getWid());
                bagResp += System.getProperty("line.separator")
                        + "[格子id:" + userbag.getId()
                        + "] [物品id:" + equipment.getId()
                        + "] [武器当前耐久度:" + userbag.getDurability()
                        + "] [武器名称:" + equipment.getName()
                        + "] [武器攻击力加成" + equipment.getAddValue()
                        + "] [武器星级" + userbag.getStartlevel()
                        + "] [武器数量:" + userbag.getNum() + "]";
            } else if (userbag.getTypeof().equals(PGood.HPMEDICINE)) {
                HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(userbag.getWid());
                bagResp += System.getProperty("line.separator")
                        + "[格子id:" + userbag.getId()
                        + "] [物品id:" + hpMedicine.getId()
                        + "] [物品名称" + hpMedicine.getName()
                        + "] [物品数量" + userbag.getNum()
                        + "] [物品恢复血量" + hpMedicine.getReplyValue()
                        + "] [物品cd" + hpMedicine.getCd() + "]";
            }else if(userbag.getTypeof().equals(PGood.CHANGEGOOD)){
                CollectGood collectGood = ProjectContext.collectGoodMap.get(userbag.getWid());
                bagResp +=System.getProperty("line.separator")
                        + "[格子id:" + userbag.getId()
                        + "] [物品id:" + collectGood.getId()
                        + "] [物品名称:" + collectGood.getName()
                        + "] [物品数量:" + userbag.getNum()
                        + "] [物品描述:" + collectGood.getDesc() + "]";
            }
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(bagResp, PacketType.USERBAGMSG));
    }
}
