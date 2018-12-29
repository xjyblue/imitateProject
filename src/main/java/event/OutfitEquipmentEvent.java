package event;

import achievement.Achievement;
import achievement.AchievementExecutor;
import caculation.MoneyCaculation;
import caculation.UserbagCaculation;
import component.CollectGood;
import component.Equipment;
import component.Monster;
import component.parent.PGood;
import config.GrobalConfig;
import io.netty.channel.Channel;
import mapper.UserMapper;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import utils.LevelUtil;
import utils.MessageUtil;

import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/22 10:27
 */
@Component("outfitEquipmentEvent")
public class OutfitEquipmentEvent {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private UserbagCaculation userbagCaculation;
    @Autowired
    private MoneyCaculation moneyCaculation;

    public void getGoods(Channel channel, Monster monster) {
        User user = getUser(channel);
        int num = (int) (Math.random() * 100);
        if (monster.getType().equals(Monster.TYPEOFCOMMONMONSTER)) {
            String reward = monster.getReward();
            if (!reward.equals(GrobalConfig.NULL)) {
                for (String rewardT : reward.split("-")) {
                    String rewardArr[] = rewardT.split(":");
                    if (num > Integer.parseInt(rewardArr[2])) {
                        if (rewardArr[0].equals(GrobalConfig.NULL)) {
                            String money = rewardArr[3];
                            moneyCaculation.addMoneyToUser(user, money);
                            channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + money + "金币,当前人物金币为[" + user.getMoney() + "]"));
                        } else {
                            String goodType = rewardArr[0];
                            Integer goodId = Integer.parseInt(rewardArr[1]);
                            goodToUser(goodType,goodId, user, channel);
                        }
                        break;
                    }
                }
            }

        } else if (monster.getType().equals(Monster.TYPEOFBOSS)) {
            if(!monster.getReward().equals(GrobalConfig.NULL)){
                String reward = monster.getReward();
                for (String rewardT : reward.split("-")) {
                    String rewardArr[] = rewardT.split(":");
                    if (rewardArr[0].equals(GrobalConfig.NULL)) {
                        String money = rewardArr[3];
                        moneyCaculation.addMoneyToUser(user, money);
                        channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + money + "金币,当前人物金币为[" + user.getMoney() + "]"));
                    } else {
                        String goodType = rewardArr[0];
                        Integer goodId = Integer.parseInt(rewardArr[1]);
                        goodToUser(goodType,goodId, user, channel);
                    }
                    break;
                }
            }
        }

//      升级
        LevelUtil.upUserLevel(user,monster.getExperience().toString());

//      触发打怪任务事件
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
            if (!achievementprocess.getIffinish() && achievementprocess.getType().equals(Achievement.ATTACKMONSTER)) {
                achievementExecutor.executeKillMonster(user, achievementprocess, monster.getId());
            }
            if (!achievementprocess.getIffinish() && achievementprocess.getType().equals(Achievement.FINISHBOSSAREA) && monster.getType().equals(Monster.TYPEOFBOSS)) {
                achievementExecutor.executeBossAttack(achievementprocess, user, achievement, monster);
            }
        }
    }

    private void goodToUser(String goodType,Integer goodId, User user, Channel channel) {
       if(goodType.equals(PGood.EQUIPMENT)){
           Equipment equipment = ProjectContext.equipmentMap.get(goodId);
           Userbag userbag = new Userbag();
           userbag.setId(UUID.randomUUID().toString());
           userbag.setName(equipment.getName());
           userbag.setNum(1);
           userbag.setTypeof(PGood.EQUIPMENT);
           userbag.setName(equipment.getName());
           userbag.setStartlevel(equipment.getStartLevel());
           userbag.setWid(equipment.getId());
           userbag.setDurability(equipment.getDurability());
           userbagCaculation.addUserBagForUser(user, userbag);
           channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + equipment.getName() + "增加攻击力为" + equipment.getAddValue()));
       }
       if(goodType.equals(PGood.CHANGEGOOD)){
           CollectGood collectGood = ProjectContext.collectGoodMap.get(goodId);
           Userbag userbag = new Userbag();
           userbag.setId(UUID.randomUUID().toString());
           userbag.setName(user.getUsername());
           userbag.setNum(1);
           userbag.setWid(goodId);
           userbag.setTypeof(PGood.CHANGEGOOD);
           userbagCaculation.addUserBagForUser(user, userbag);
           channel.writeAndFlush(MessageUtil.turnToPacket("恭喜你获得" + collectGood.getName()));
       }
    }

    private User getUser(Channel channel) {
        return ProjectContext.session2UserIds.get(channel);
    }

    public void extraBonus(User user, Channel channel) {
        moneyCaculation.addMoneyToUser(user, "200");
        channel.writeAndFlush(MessageUtil.turnToPacket("************最终击杀者额外奖励200金币*************"));
    }
}
