package event;

import achievement.AchievementExecutor;
import caculation.UserbagCaculation;
import component.Equipment;
import component.HpMedicine;
import component.MpMedicine;
import component.parent.Good;
import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.UserbagMapper;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/20 17:46
 */
@Component("shopEvent")
public class ShopEvent {
    @Autowired
    private UserbagMapper userbagMapper;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private UserbagCaculation userbagCaculation;

    public void shop(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (msg.equals("s")) {
            String resp = System.getProperty("line.separator")
                    + MessageConfig.MESSAGESTART
                    + System.getProperty("line.separator")
                    + "您好，欢迎来到不充钱就不能玩的商店领域";
//          回显可购买的武器
            resp += System.getProperty("line.separator") + "可购买的武器有:" + System.getProperty("line.separator");
            for (Map.Entry<Integer, Equipment> equipmentEntry : ProjectContext.equipmentMap.entrySet()) {
                resp += "----物品id:" + equipmentEntry.getValue().getId()
                        + "----武器名称:" + equipmentEntry.getValue().getName()
                        + "----武器增加的伤害值:" + equipmentEntry.getValue().getAddValue()
                        + "----武器的价值:" + equipmentEntry.getValue().getBuyMoney()
                        + System.getProperty("line.separator");
            }
//          回显可购买的蓝药
            resp += System.getProperty("line.separator") + "可购买的蓝药有:" + System.getProperty("line.separator");
            for (Map.Entry<Integer, MpMedicine> mpMedicineEntry : ProjectContext.mpMedicineMap.entrySet()) {
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
            for (Map.Entry<Integer, HpMedicine> hpMedicineEntry : ProjectContext.hpMedicineMap.entrySet()) {
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
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
        }

        if (msg.startsWith("s-")) {
            String temp[] = msg.split("-");
            if (temp.length != 3) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                return;
            }
//          校验是否为有效的物品id
            if (!checkIfGoodId(temp[1])) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FAILGOODID));
                return;
            }
//          校验用户的金钱是否足够
            if (!checkUserMoneyEnough(temp[2], temp[1], channel)) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMONEY));
                return;
            }
//           处理蓝药购买逻辑
            if (ProjectContext.mpMedicineMap.containsKey(Integer.parseInt(temp[1]))) {
                MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(Integer.parseInt(temp[1]));
                Userbag userbag = new Userbag();
                userbag.setWid(mpMedicine.getId());
                userbag.setName(user.getUsername());
                userbag.setNum(Integer.parseInt(temp[2]));
                userbag.setId(UUID.randomUUID().toString());
                userbag.setTypeof(Good.MPMEDICINE);
                userbagCaculation.addUserBagForUser(user, userbag);
                String goodAllMoney = changeUserMoney(mpMedicine.getBuyMoney(), temp[2], user);
                channel.writeAndFlush(MessageUtil.turnToPacket("您已购买了" + mpMedicine.getName() + temp[2] + "件" + "[花费:" + goodAllMoney + "]" + "[用户剩余金币:" + user.getMoney() + "]"));
            }
//            处理装备购买逻辑
            if (ProjectContext.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
                Equipment equipment = ProjectContext.equipmentMap.get(Integer.parseInt(temp[1]));
//              装备不支持叠加，一个格子一个装备
                int count = Integer.parseInt(temp[2]);
                while (count > 0) {
                    Userbag userbag = new Userbag();
                    userbag.setName(equipment.getName());
                    userbag.setNum(1);
                    userbag.setStartlevel(equipment.getStartLevel());
                    userbag.setTypeof(Good.EQUIPMENT);
                    userbag.setName(user.getUsername());
                    userbag.setStartlevel(equipment.getStartLevel());
                    userbag.setId(UUID.randomUUID().toString());
                    userbag.setWid(equipment.getId());
                    userbag.setDurability(equipment.getDurability());
                    userbagCaculation.addUserBagForUser(user, userbag);
                    count--;
                }
                String goodAllMoney = changeUserMoney(equipment.getBuyMoney(), temp[2], user);
                channel.writeAndFlush(MessageUtil.turnToPacket("您已购买了" + equipment.getName() + temp[2] + "件" + "[花费:" + goodAllMoney + "]" + "[用户剩余金币:" + user.getMoney() + "]"));
            }
            //处理红药购买逻辑
            if(ProjectContext.hpMedicineMap.containsKey(Integer.parseInt(temp[1]))){
                HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(Integer.parseInt(temp[1]));
                Userbag userbag = new Userbag();
                userbag.setWid(hpMedicine.getId());
                userbag.setName(user.getUsername());
                userbag.setNum(Integer.parseInt(temp[2]));
                userbag.setId(UUID.randomUUID().toString());
                userbag.setTypeof(Good.HPMEDICINE);
                userbagCaculation.addUserBagForUser(user, userbag);
                String goodAllMoney = changeUserMoney(hpMedicine.getBuyMoney(), temp[2], user);
                channel.writeAndFlush(MessageUtil.turnToPacket("您已购买了" + hpMedicine.getName() + temp[2] + "件" + "[花费:" + goodAllMoney + "]" + "[用户剩余金币:" + user.getMoney() + "]"));
            }
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
        User user = ProjectContext.session2UserIds.get(channel);
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger goodMoney = new BigInteger("0");
        if (ProjectContext.equipmentMap.containsKey(Integer.parseInt(s))) {
            goodMoney = new BigInteger(ProjectContext.equipmentMap.get(Integer.parseInt(s)).getBuyMoney()).multiply(new BigInteger(num));
        } else if (ProjectContext.mpMedicineMap.containsKey(Integer.parseInt(s))) {
            goodMoney = new BigInteger(ProjectContext.mpMedicineMap.get(Integer.parseInt(s)).getBuyMoney()).multiply(new BigInteger(num));
        }
        if (userMoney.compareTo(goodMoney) >= 0) {
            return true;
        }
        return false;
    }

    private boolean checkIfGoodId(String s) {
        if (ProjectContext.mpMedicineMap.containsKey(Integer.parseInt(s))) {
            return true;
        }
        if (ProjectContext.equipmentMap.containsKey(Integer.parseInt(s))) {
            return true;
        }
        if(ProjectContext.hpMedicineMap.containsKey(Integer.parseInt(s))){
            return true;
        }
        return false;
    }
}
