package service.userbagservice.service;

import config.impl.excel.CollectGoodResourceLoad;
import config.impl.excel.EquipmentResourceLoad;
import config.impl.excel.HpMedicineResourceLoad;
import config.impl.excel.MpMedicineResourceLoad;
import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.component.good.CollectGood;
import core.component.good.Equipment;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import core.component.good.parent.BaseGood;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.buffservice.entity.BuffConstant;
import service.caculationservice.service.MpCaculationService;
import utils.ChannelUtil;
import utils.MessageUtil;


/**
 * @ClassName UserbagService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class UserbagService {
    @Autowired
    private MpCaculationService mpCaculationService;

    /**
     * 整理用户背包
     *
     * @param channel
     * @param msg
     */
    public void arrangeUserBag(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        for (Userbag userbag : user.getUserBag()) {
            for (Userbag userbag2 : user.getUserBag()) {
                if (userbag != userbag2 && userbag.getWid().equals(userbag2.getWid()) && !userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
//                      相同物品不同格子叠加
                    userbag.setNum(userbag.getNum() + userbag2.getNum());
                }
            }
        }
    }

    /**
     * 使用用户背包
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ub", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE
            , ChannelStatus.TRADE, ChannelStatus.TEAM, ChannelStatus.LABOURUNION})
    public void useUserbag(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
        Integer key = Integer.parseInt(temp[1]);
        if (!checkGoodInUserbag(user, key)) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("背包中无此物品");
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        if (MpMedicineResourceLoad.mpMedicineMap.containsKey(key)) {
            MpMedicine mpMedicine = MpMedicineResourceLoad.mpMedicineMap.get(key);

//              处理用户背包
            sloveUserbag(user, channel, key);
//          直接蓝药直接回复
            if (mpMedicine.isImmediate()) {
                mpCaculationService.addUserMp(user, mpMedicine.getReplyValue());
            } else {
//              缓慢蓝药修改回蓝buff
                if (user.getBuffMap().get(BuffConstant.MPBUFF).equals(mpMedicine.getId())) {
                    user.getUserBuffEndTimeMap().put(BuffConstant.MPBUFF, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                } else {
                    user.getBuffMap().put(BuffConstant.MPBUFF, mpMedicine.getId());
                    user.getUserBuffEndTimeMap().put(BuffConstant.MPBUFF, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                }
                user.getBuffMap().put(BuffConstant.MPBUFF, mpMedicine.getId());
            }
        } else if (HpMedicineResourceLoad.hpMedicineMap.containsKey(key)) {
//              处理使用红药的过程
            HpMedicine hpMedicine = HpMedicineResourceLoad.hpMedicineMap.get(key);
            if (user.getUserBuffEndTimeMap().get(BuffConstant.TREATMENTBUFF) < System.currentTimeMillis()) {
                user.getBuffMap().put(BuffConstant.TREATMENTBUFF, hpMedicine.getId());
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("你使用了" + hpMedicine.getName() + "回复红量：" + hpMedicine.getReplyValue());
                MessageUtil.sendMessage(channel, builder.build());
//                  处理用户背包
                sloveUserbag(user, channel, key);
            } else {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.UNSKILLCD);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
        } else {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.GOODNOEXIST);
            MessageUtil.sendMessage(channel, builder.build());
        }
    }

    /**
     * 检查背包是否有此物品
     *
     * @param user
     * @param key
     * @return
     */
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
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.GOODNOEXISTBAG);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
    }

    /**
     * 根据用户背包id获取用户背包
     *
     * @param user
     * @param userbagId
     * @return
     */
    public Userbag getUserbagByUserbagId(User user, String userbagId) {
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getId().equals(userbagId)) {
                return userbag;
            }
        }
        return null;
    }

    /**
     * 刷新用户背包
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qb", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE
            , ChannelStatus.TRADE, ChannelStatus.TEAM, ChannelStatus.LABOURUNION})
    public void refreshUserbagInfo(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String bagResp = System.getProperty("line.separator")
                + "按b-物品编号使用蓝药"
                + "  按ww=物品编号装备武器";
        for (Userbag userbag : user.getUserBag()) {
            bagResp = showUserBagInfo(bagResp, userbag);
        }


        ServerPacket.UserbagResp.Builder builder = ServerPacket.UserbagResp.newBuilder();
        builder.setData(bagResp);
        MessageUtil.sendMessage(channel, builder.build());
    }

    private String showUserBagInfo(String bagResp, Userbag userbag) {
        if (userbag.getTypeof().equals(BaseGood.MPMEDICINE)) {
            MpMedicine mpMedicine = MpMedicineResourceLoad.mpMedicineMap.get(userbag.getWid());
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
        } else if (userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(userbag.getWid());
            bagResp += System.getProperty("line.separator")
                    + "[格子id:" + userbag.getId()
                    + "] [物品id:" + equipment.getId()
                    + "] [武器当前耐久度:" + userbag.getDurability()
                    + "] [武器名称:" + equipment.getName()
                    + "] [武器攻击力加成" + equipment.getAddValue()
                    + "] [武器星级" + userbag.getStartlevel()
                    + "] [武器数量:" + userbag.getNum() + "]";
        } else if (userbag.getTypeof().equals(BaseGood.HPMEDICINE)) {
            HpMedicine hpMedicine = HpMedicineResourceLoad.hpMedicineMap.get(userbag.getWid());
            bagResp += System.getProperty("line.separator")
                    + "[格子id:" + userbag.getId()
                    + "] [物品id:" + hpMedicine.getId()
                    + "] [物品名称" + hpMedicine.getName()
                    + "] [物品数量" + userbag.getNum()
                    + "] [物品恢复血量" + hpMedicine.getReplyValue()
                    + "] [物品cd" + hpMedicine.getCd() + "]";
        } else if (userbag.getTypeof().equals(BaseGood.CHANGEGOOD)) {
            CollectGood collectGood = CollectGoodResourceLoad.collectGoodMap.get(userbag.getWid());
            bagResp += System.getProperty("line.separator")
                    + "[格子id:" + userbag.getId()
                    + "] [物品id:" + collectGood.getId()
                    + "] [物品名称:" + collectGood.getName()
                    + "] [物品数量:" + userbag.getNum()
                    + "] [物品描述:" + collectGood.getDesc() + "]";
        }
        return bagResp;
    }

    /**
     * 根据userbag拿物品的名字
     *
     * @param userbag
     * @return
     */
    public String getGoodNameByUserbag(Userbag userbag) {
        if (userbag.getTypeof().equals(BaseGood.MPMEDICINE)) {
            MpMedicine mpMedicine = MpMedicineResourceLoad.mpMedicineMap.get(userbag.getWid());
            return "[蓝药--》] [物品id:" + userbag.getId() + "] [药品名称：" + mpMedicine.getName() + "]" + " [药品数量: " + userbag.getNum() + "]";
        }
        if (userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(userbag.getWid());
            return "[武器--》] [物品id:" + userbag.getId() + "] [武器名称：" + equipment.getName() + "]" + " [武器耐久度：" + userbag.getDurability() + "]" + " [武器数量： " + userbag.getNum() + "]" + "[武器星级：]" + userbag.getStartlevel();
        }
        if (userbag.getTypeof().equals(BaseGood.HPMEDICINE)) {
            HpMedicine hpMedicine = HpMedicineResourceLoad.hpMedicineMap.get(userbag.getWid());
            return "[红药--》] [物品id:" + userbag.getId() + "] [药品名称：" + hpMedicine.getName() + "]" + " [武器数量： " + userbag.getNum() + "]";
        }
        return null;
    }


    /**
     * 校验物品数量是否足够
     *
     * @param userbag
     * @param num
     * @return
     */
    public boolean checkUserbagNum(Userbag userbag, String num) {
        if (userbag.getNum() == null) {
            return false;
        }
        if (userbag.getNum() >= Integer.parseInt(num)) {
            return true;
        } else {
            return false;
        }
    }
}
