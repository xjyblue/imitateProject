package event;

import component.Equipment;
import component.MpMedicine;
import component.parent.Good;
import config.MessageConfig;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/20 17:46
 */
@Component("shopEvent")
public class ShopEvent {

    public void shop(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
        if (msg.equals("s")) {
            String resp = System.getProperty("line.separator")
                    + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
                    + System.getProperty("line.separator")
                    + "您好，欢迎来到不充钱就不能玩的商店领域";
//          回显可购买的武器
            resp += System.getProperty("line.separator") + "可购买的武器有:" + System.getProperty("line.separator");
            for (Map.Entry<Integer, Equipment> equipmentEntry : NettyMemory.equipmentMap.entrySet()) {
                resp += "----物品id:" + equipmentEntry.getValue().getId()
                        + "----武器名称:" + equipmentEntry.getValue().getName()
                        + "----武器增加的伤害值:" + equipmentEntry.getValue().getAddValue()
                        + "----武器的价值:" + equipmentEntry.getValue().getBuyMoney()
                        + System.getProperty("line.separator");
            }
//          回显可购买的蓝药
            resp += System.getProperty("line.separator") + "可购买的蓝药有:" + System.getProperty("line.separator");
            for (Map.Entry<Integer, MpMedicine> mpMedicineEntry : NettyMemory.mpMedicineMap.entrySet()) {
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
            resp += "[购买武器请输入s-物品编号-数量 即可购买]" + System.getProperty("line.separator");
            resp += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
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
            if (!checkUserMoneyEnough(temp[2],temp[1], channel)) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMONEY));
                return;
            }
//           处理蓝药购买逻辑
            if (NettyMemory.mpMedicineMap.containsKey(Integer.parseInt(temp[1]))) {
                MpMedicine mpMedicine = NettyMemory.mpMedicineMap.get(Integer.parseInt(temp[1]));
                if(checkUserBagContainsGood(user,mpMedicine.getId())){
                    for(Userbag userbag:user.getUserBag()){
                        if(userbag.getWid().equals(mpMedicine.getId())){
                            userbag.setNum(userbag.getNum()+ Integer.parseInt(temp[2]));
                            break;
                        }
                    }
                }else {
                    Userbag userbag = new Userbag();
                    userbag.setWid(mpMedicine.getId());
                    userbag.setName(mpMedicine.getName());
                    userbag.setNum(Integer.parseInt(temp[2]));
                    userbag.setTypeof(Good.MPMEDICINE);
                    user.getUserBag().add(userbag);
                }
                String goodAllMoney = changeUserMoney(mpMedicine.getBuyMoney(),temp[2],user);
                channel.writeAndFlush(MessageUtil.turnToPacket("您已购买了"+mpMedicine.getName()+temp[2]+"件"+"[花费:"+goodAllMoney+"]"+"[用户剩余金币:"+user.getMoney()+"]"));
            }
//            处理装备购买逻辑
            if (NettyMemory.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
                Equipment equipment = NettyMemory.equipmentMap.get(Integer.parseInt(temp[1]));
//              装备不支持叠加，一个格子一个装备
                int count = Integer.parseInt(temp[2]);
                while(count>0){
                    Userbag userbag = new Userbag();
                    userbag.setName(equipment.getName());
                    userbag.setNum(1);
                    userbag.setTypeof(Good.EQUIPMENT);
                    userbag.setName(equipment.getName());
                    userbag.setWid(equipment.getId());
                    userbag.setDurability(equipment.getDurability());
                    user.getUserBag().add(userbag);
                    count--;
                }
                String goodAllMoney = changeUserMoney(equipment.getBuyMoney(),temp[2],user);
                channel.writeAndFlush(MessageUtil.turnToPacket("您已购买了"+equipment.getName()+temp[2]+"件"+"[花费:"+goodAllMoney+"]"+"[用户剩余金币:"+user.getMoney()+"]"));
            }
        }
    }

    private String changeUserMoney(String goodMoney, String num,User user) {
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger goodAllMoney = new BigInteger(goodMoney).multiply(new BigInteger(num));
        userMoney = userMoney.subtract(goodAllMoney);
        user.setMoney(userMoney.toString());
        return goodAllMoney.toString();
    }

    private boolean checkUserBagContainsGood(User user,Integer wid) {
        for(Userbag userbag:user.getUserBag()){
            if(userbag.getWid().equals(wid)){
                return true;
            }
        }
        return false;
    }

    private boolean checkUserMoneyEnough(String num,String s, Channel channel) {
        User user = NettyMemory.session2UserIds.get(channel);
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger goodMoney = new BigInteger("0");
        if (NettyMemory.equipmentMap.containsKey(Integer.parseInt(s))) {
            goodMoney = new BigInteger(NettyMemory.equipmentMap.get(Integer.parseInt(s)).getBuyMoney()).multiply(new BigInteger(num));
        } else if (NettyMemory.mpMedicineMap.containsKey(Integer.parseInt(s))) {
            goodMoney = new BigInteger(NettyMemory.mpMedicineMap.get(Integer.parseInt(s)).getBuyMoney()).multiply(new BigInteger(num));
        }
        if (userMoney.compareTo(goodMoney) >= 0) {
            return true;
        }
        return false;
    }

    private boolean checkIfGoodId(String s) {
        if (NettyMemory.mpMedicineMap.containsKey(Integer.parseInt(s))) {
            return true;
        }
        if (NettyMemory.equipmentMap.containsKey(Integer.parseInt(s))) {
            return true;
        }
        return false;
    }
}
