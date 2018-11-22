package event;

import component.Equipment;
import component.Monster;
import component.parent.Good;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import utils.MessageUtil;

import java.math.BigInteger;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/22 10:27
 */
@Component("qutfitEquipmentEvent")
public class OutfitEquipmentEvent {

    public void getGoods(Channel channel, String msg, Monster monster) {
        User user = getUser(channel);
        int num = (int) (Math.random() * 100);
        if (monster.getType().equals(Monster.TYPEOFCOMMONMONSTER)) {
//       这里可以引入装备爆率表
            if (num < 10) {
//             多一把武器
                Equipment equipment = NettyMemory.equipmentMap.get(3006);
                equipMentToUser(equipment, user, channel);
            } else {
                BigInteger addMoney = new BigInteger("200000");
                user.addMoney(addMoney);
                channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + addMoney.toString() + "金币,当前人物金币为[" + user.getMoney() + "]"));
            }
        } else if (monster.getType().equals(Monster.TYPEOFBOSS)) {
            if (num < 10) {
//             多一把武器
                Equipment equipment = NettyMemory.equipmentMap.get(3007);
                equipMentToUser(equipment, user, channel);
            } else {
                BigInteger addMoney = new BigInteger("20000000");
                user.addMoney(addMoney);
                channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + addMoney.toString() + "金币,当前人物金币为[" + user.getMoney() + "]"));
            }
        }
    }

    private void equipMentToUser(Equipment equipment, User user, Channel channel) {
        Userbag userbag = new Userbag();
        userbag.setName(equipment.getName());
        userbag.setNum(1);
        userbag.setTypeof(Good.EQUIPMENT);
        userbag.setName(equipment.getName());
        userbag.setWid(equipment.getId());
        userbag.setDurability(equipment.getDurability());
        user.getUserBag().add(userbag);
        channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + equipment.getName() + "增加攻击力为" + equipment.getAddValue()));
    }

    private User getUser(Channel channel) {
        return NettyMemory.session2UserIds.get(channel);
    }
}
