package event;

import achievement.Achievement;
import achievement.AchievementExecutor;
import caculation.UserbagCaculation;
import component.Equipment;
import component.Monster;
import component.parent.Good;
import io.netty.channel.Channel;
import mapper.UserMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import utils.LevelUtil;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/22 10:27
 */
@Component("qutfitEquipmentEvent")
public class OutfitEquipmentEvent {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private UserbagCaculation userbagCaculation;

    public void getGoods(Channel channel, Monster monster) {
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

        int levelStart = LevelUtil.getLevelByExperience(user.getExperience());
        user.setExperience(user.getExperience() + monster.getExperience());
        int levelEnd = LevelUtil.getLevelByExperience(user.getExperience());
        userMapper.updateByPrimaryKeySelective(user);
        if (levelEnd > levelStart) {
            channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你升级啦，升到了万众瞩目的[" + levelEnd + "]级"));
        }

//      触发任务事件
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            Achievement achievement = NettyMemory.achievementMap.get(achievementprocess.getAchievementid());
            if (!achievementprocess.getIffinish() && achievementprocess.getType().equals(Achievement.ATTACKMONSTER)) {
                achievementExecutor.executeKillMonster(user, achievementprocess, monster.getId());
            }
            if (!achievementprocess.getIffinish() && achievementprocess.getType().equals(Achievement.UPLEVEL)) {
                achievementExecutor.executeLevelUp(achievementprocess, user, achievement);
            }
            if (!achievementprocess.getIffinish() && achievementprocess.getType().equals(Achievement.FINISHBOSSAREA) && monster.getType().equals(Monster.TYPEOFBOSS)) {
                achievementExecutor.executeBossAttack(achievementprocess, user, achievement,monster);
            }
        }

        channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + monster.getExperience() + "经验"));

    }

    private void equipMentToUser(Equipment equipment, User user, Channel channel) {
        Userbag userbag = new Userbag();
        userbag.setId(UUID.randomUUID().toString());
        userbag.setName(equipment.getName());
        userbag.setNum(1);
        userbag.setTypeof(Good.EQUIPMENT);
        userbag.setName(equipment.getName());
        userbag.setWid(equipment.getId());
        userbag.setDurability(equipment.getDurability());
        userbagCaculation.addUserBagForUser(user, userbag);
        channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + equipment.getName() + "增加攻击力为" + equipment.getAddValue()));
    }

    private User getUser(Channel channel) {
        return NettyMemory.session2UserIds.get(channel);
    }
}
