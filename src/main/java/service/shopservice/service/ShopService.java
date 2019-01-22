package service.shopservice.service;

import config.impl.excel.EquipmentResourceLoad;
import config.impl.excel.HpMedicineResourceLoad;
import config.impl.excel.MpMedicineResourceLoad;
import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.packet.ServerPacket;
import service.caculationservice.service.UserbagCaculationService;
import core.component.good.Equipment;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import core.component.good.parent.BaseGood;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName ShopService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class ShopService {
    @Autowired
    private UserbagCaculationService userbagCaculationService;

    /**
     * 展示商城信息
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qshop", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.LABOURUNION,
            ChannelStatus.TRADE, ChannelStatus.BOSSSCENE, ChannelStatus.AUCTION})
    public void queryShopGood(Channel channel, String msg) {
        String resp = System.getProperty("line.separator")
                + MessageConfig.MESSAGESTART
                + System.getProperty("line.separator")
                + "您好，欢迎来到不充钱就不能玩的商店领域";
//          回显可购买的武器
        resp += System.getProperty("line.separator") + "可购买的武器有:" + System.getProperty("line.separator");
        for (Map.Entry<Integer, Equipment> equipmentEntry : EquipmentResourceLoad.equipmentMap.entrySet()) {
            resp += "----物品id:" + equipmentEntry.getValue().getId()
                    + "----武器名称:" + equipmentEntry.getValue().getName()
                    + "----武器增加的伤害值:" + equipmentEntry.getValue().getAddValue()
                    + "----武器的价值:" + equipmentEntry.getValue().getBuyMoney()
                    + System.getProperty("line.separator");
        }
//          回显可购买的蓝药
        resp += System.getProperty("line.separator") + "可购买的蓝药有:" + System.getProperty("line.separator");
        for (Map.Entry<Integer, MpMedicine> mpMedicineEntry : MpMedicineResourceLoad.mpMedicineMap.entrySet()) {
            resp += "----物品id:" + mpMedicineEntry.getValue().getId()
                    + "----蓝药名称:" + mpMedicineEntry.getValue().getName();
            if (!mpMedicineEntry.getValue().isImmediate()) {
                resp += "----蓝药每秒回复蓝量:" + mpMedicineEntry.getValue().getSecondValue()
                        + "----持续时间" + mpMedicineEntry.getValue().getKeepTime();
            } else {
                resp += "----蓝药回复总蓝量:" + mpMedicineEntry.getValue().getReplyValue();
            }
            resp += "----蓝药价值:" + mpMedicineEntry.getValue().getBuyMoney()
                    + System.getProperty("line.separator");
        }
        resp += System.getProperty("line.separator") + "可购买的红药有:" + System.getProperty("line.separator");
        for (Map.Entry<Integer, HpMedicine> hpMedicineEntry : HpMedicineResourceLoad.hpMedicineMap.entrySet()) {
            HpMedicine hpMedicine = hpMedicineEntry.getValue();
            resp += "----物品id:" + hpMedicine.getId()
                    + "----物品名称" + hpMedicine.getName()
                    + "----物品回复血量" + hpMedicine.getReplyValue()
                    + "----物品cd" + hpMedicine.getCd()
                    + "----物品价值" + hpMedicine.getReplyValue()
                    + System.getProperty("line.separator");
        }
        resp += "[购买武器请输入s-物品编号-数量 即可购买]" + System.getProperty("line.separator");
        resp += MessageConfig.MESSAGEEND;

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(resp);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 购买商城物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "bshop", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.LABOURUNION,
            ChannelStatus.TRADE, ChannelStatus.BOSSSCENE, ChannelStatus.AUCTION})
    public void buyShopGood(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//          校验是否为有效的物品id
        if (!checkIfGoodId(temp[1])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.FAILGOODID);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//          校验用户的金钱是否足够
        if (!checkUserMoneyEnough(temp[GrobalConfig.TWO], temp[1], channel)) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.UNENOUGHMONEY);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//           处理蓝药购买逻辑
        if (MpMedicineResourceLoad.mpMedicineMap.containsKey(Integer.parseInt(temp[1]))) {
            MpMedicine mpMedicine = MpMedicineResourceLoad.mpMedicineMap.get(Integer.parseInt(temp[1]));
            Userbag userbag = new Userbag();
            userbag.setWid(mpMedicine.getId());
            userbag.setName(user.getUsername());
            userbag.setNum(Integer.parseInt(temp[2]));
            userbag.setId(UUID.randomUUID().toString());
            userbag.setTypeof(BaseGood.MPMEDICINE);
            userbagCaculationService.addUserBagForUser(user, userbag);
            String goodAllMoney = changeUserMoney(mpMedicine.getBuyMoney(), temp[2], user);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("您已购买了" + mpMedicine.getName() + temp[2] + "件" + "[花费:" + goodAllMoney + "]" + "[用户剩余金币:" + user.getMoney() + "]");
            MessageUtil.sendMessage(channel, builder.build());
        }
//            处理装备购买逻辑
        if (EquipmentResourceLoad.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(Integer.parseInt(temp[1]));
//              装备不支持叠加，一个格子一个装备
            int count = Integer.parseInt(temp[2]);
            while (count > 0) {
                Userbag userbag = new Userbag();
                userbag.setName(equipment.getName());
                userbag.setNum(1);
                userbag.setStartlevel(equipment.getStartLevel());
                userbag.setTypeof(BaseGood.EQUIPMENT);
                userbag.setName(user.getUsername());
                userbag.setStartlevel(equipment.getStartLevel());
                userbag.setId(UUID.randomUUID().toString());
                userbag.setWid(equipment.getId());
                userbag.setDurability(equipment.getDurability());
                userbagCaculationService.addUserBagForUser(user, userbag);
                count--;
            }
            String goodAllMoney = changeUserMoney(equipment.getBuyMoney(), temp[2], user);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("您已购买了" + equipment.getName() + temp[2] + "件" + "[花费:" + goodAllMoney + "]" + "[用户剩余金币:" + user.getMoney() + "]");
            MessageUtil.sendMessage(channel, builder.build());
        }
        //处理红药购买逻辑
        if (HpMedicineResourceLoad.hpMedicineMap.containsKey(Integer.parseInt(temp[1]))) {
            HpMedicine hpMedicine = HpMedicineResourceLoad.hpMedicineMap.get(Integer.parseInt(temp[1]));
            Userbag userbag = new Userbag();
            userbag.setWid(hpMedicine.getId());
            userbag.setName(user.getUsername());
            userbag.setNum(Integer.parseInt(temp[2]));
            userbag.setId(UUID.randomUUID().toString());
            userbag.setTypeof(BaseGood.HPMEDICINE);
            userbagCaculationService.addUserBagForUser(user, userbag);
            String goodAllMoney = changeUserMoney(hpMedicine.getBuyMoney(), temp[2], user);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("您已购买了" + hpMedicine.getName() + temp[2] + "件" + "[花费:" + goodAllMoney + "]" + "[用户剩余金币:" + user.getMoney() + "]");
            MessageUtil.sendMessage(channel, builder.build());
        }
    }


    private String changeUserMoney(String goodMoney, String num, User user) {
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger goodAllMoney = new BigInteger(goodMoney).multiply(new BigInteger(num));
        userMoney = userMoney.subtract(goodAllMoney);
        user.setMoney(userMoney.toString());
        return goodAllMoney.toString();
    }


    private boolean checkUserMoneyEnough(String num, String s, Channel channel) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger goodMoney = new BigInteger(GrobalConfig.MINVALUE);
        if (EquipmentResourceLoad.equipmentMap.containsKey(Integer.parseInt(s))) {
            goodMoney = new BigInteger(EquipmentResourceLoad.equipmentMap.get(Integer.parseInt(s)).getBuyMoney()).multiply(new BigInteger(num));
        } else if (MpMedicineResourceLoad.mpMedicineMap.containsKey(Integer.parseInt(s))) {
            goodMoney = new BigInteger(MpMedicineResourceLoad.mpMedicineMap.get(Integer.parseInt(s)).getBuyMoney()).multiply(new BigInteger(num));
        }
        if (userMoney.compareTo(goodMoney) >= 0) {
            return true;
        }
        return false;
    }

    private boolean checkIfGoodId(String s) {
        if (MpMedicineResourceLoad.mpMedicineMap.containsKey(Integer.parseInt(s))) {
            return true;
        }
        if (EquipmentResourceLoad.equipmentMap.containsKey(Integer.parseInt(s))) {
            return true;
        }
        if (HpMedicineResourceLoad.hpMedicineMap.containsKey(Integer.parseInt(s))) {
            return true;
        }
        return false;
    }
}
