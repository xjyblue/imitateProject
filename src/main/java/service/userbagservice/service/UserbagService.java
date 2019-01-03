package service.userbagservice.service;

import core.component.good.CollectGood;
import core.component.good.Equipment;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import core.component.good.parent.PGood;
import core.config.MessageConfig;
import core.context.ProjectContext;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.User;
import pojo.Userbag;
import service.buffservice.entity.BuffConstant;
import utils.MessageUtil;

import java.math.BigInteger;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2019/1/2 16:13
 */
@Component
public class UserbagService {

    //  整理背包物品
    public void arrangeUserBag(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        for (Userbag userbag : user.getUserBag()) {
            for (Userbag userbag2 : user.getUserBag()) {
                if (userbag != userbag2 && userbag.getWid() == userbag2.getWid() && !userbag.getTypeof().equals(PGood.EQUIPMENT)) {
//                      相同物品不同格子叠加
                    userbag.setNum(userbag.getNum() + userbag2.getNum());
                }
            }
        }
    }

    //  使用背包物品
    public void useUserbag(Channel channel, String msg) {
        String temp[] = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
        Integer key = Integer.parseInt(temp[1]);

        if (!checkGoodInUserbag(user, key)) {
            channel.writeAndFlush(MessageUtil.turnToPacket("背包中无此物品"));
            return;
        }

        if (ProjectContext.mpMedicineMap.containsKey(key)) {
            MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(key);

//              处理用户背包
            sloveUserbag(user, channel, key);

            if (mpMedicine.isImmediate()) {
                BigInteger userMp = new BigInteger(user.getMp());
                userMp = userMp.add(new BigInteger(mpMedicine.getReplyValue()));
                BigInteger maxMp = new BigInteger("10000");
                if (userMp.compareTo(maxMp) >= 0) {
                    user.setMp(maxMp.toString());
                } else {
                    user.addMp(mpMedicine.getReplyValue());
                }
            } else {
                if (user.getBuffMap().get(BuffConstant.MPBUFF) == mpMedicine.getId()) {
                    ProjectContext.userBuffEndTime.get(user).put(BuffConstant.MPBUFF, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                } else {
                    user.getBuffMap().put(BuffConstant.MPBUFF, mpMedicine.getId());
                    ProjectContext.userBuffEndTime.get(user).put(BuffConstant.MPBUFF, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                }
                user.getBuffMap().put(BuffConstant.MPBUFF, mpMedicine.getId());
            }
        } else if (ProjectContext.hpMedicineMap.containsKey(key)) {
//              处理使用红药的过程
            HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(key);
            if (ProjectContext.userBuffEndTime.get(user).get(BuffConstant.TREATMENTBUFF) < System.currentTimeMillis()) {
                user.getBuffMap().put(BuffConstant.TREATMENTBUFF, hpMedicine.getId());
                channel.writeAndFlush(MessageUtil.turnToPacket("你使用了" + hpMedicine.getName() + "回复红量：" + hpMedicine.getReplyValue()));
//                  处理用户背包
                sloveUserbag(user, channel, key);
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
                return;
            }
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
        }
    }

    private boolean checkGoodInUserbag(User user, Integer key) {
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getWid().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private void sloveUserbag(User user, Channel channel, Integer key) {
        Userbag userbagNow = null;
        Userbag userbagRemove = null;
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getWid().equals(key)) {
                userbagNow = userbag;
                userbag.setNum(userbag.getNum() - 1);
                if (userbag.getNum() == 0) {
                    userbagRemove = userbag;
                }
            }
        }

        if (userbagRemove != null) {
            user.getUserBag().remove(userbagRemove);
        }

        if (userbagNow == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXISTBAG));
            return;
        }
    }

    //  根据用户背包id获得用户背包
    public  Userbag getUserbagByUserbagId(User user, String userbagId) {
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getId().equals(userbagId)) {
                return userbag;
            }
        }
        return null;
    }

    //  刷新用户背包
    public void refreshUserbagInfo(Channel channel,String msg) {
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
            } else if (userbag.getTypeof().equals(PGood.CHANGEGOOD)) {
                CollectGood collectGood = ProjectContext.collectGoodMap.get(userbag.getWid());
                bagResp += System.getProperty("line.separator")
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
