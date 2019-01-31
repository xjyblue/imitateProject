package service.rewardservice.service;

import config.impl.excel.AchievementResourceLoad;
import config.impl.excel.CollectGoodResourceLoad;
import config.impl.excel.EquipmentResourceLoad;
import core.packet.ServerPacket;
import service.achievementservice.entity.Achievement;
import service.achievementservice.entity.AchievementConfig;
import service.achievementservice.service.AchievementService;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import core.component.good.CollectGood;
import core.component.good.Equipment;
import core.component.monster.Monster;
import core.component.good.parent.BaseGood;
import core.config.GrobalConfig;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import service.levelservice.service.LevelService;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.Random;
import java.util.UUID;

/**
 * @ClassName RewardService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class RewardService {
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserbagCaculationService userbagCaculationService;
    @Autowired
    private MoneyCaculationService moneyCaculationService;
    @Autowired
    private LevelService levelService;

    /**
     * 获得奖励
     *
     * @param channel
     * @param monster
     */
    public void getGoods(Channel channel, Monster monster) {
        User user = getUser(channel);
        Random random = new Random();
        int num = random.nextInt(100) + 1;
        if (monster.getType().equals(Monster.TYPEOFCOMMONMONSTER)) {
            String reward = monster.getReward();
            if (!reward.equals(GrobalConfig.NULL)) {
                for (String rewardT : reward.split("-")) {
                    String[] rewardArr = rewardT.split(":");
                    if (num > Integer.parseInt(rewardArr[2])) {
                        if (rewardArr[0].equals(GrobalConfig.NULL)) {
                            String money = rewardArr[3];
                            moneyCaculationService.addMoneyToUser(user, money);
                            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                            builder.setData("恭喜你获得" + money + "金币,当前人物金币为[" + user.getMoney() + "]");
                            MessageUtil.sendMessage(channel, builder.build());
                        } else {
                            String goodType = rewardArr[0];
                            Integer goodId = Integer.parseInt(rewardArr[1]);
                            goodToUser(goodType, goodId, user, channel);
                        }
                        break;
                    }
                }
            }

        } else if (monster.getType().equals(Monster.TYPEOFBOSS)) {
            if (!monster.getReward().equals(GrobalConfig.NULL)) {
                String reward = monster.getReward();
                for (String rewardT : reward.split("-")) {
                    String[] rewardArr = rewardT.split(":");
                    if (rewardArr[0].equals(GrobalConfig.NULL)) {
                        String money = rewardArr[3];
                        moneyCaculationService.addMoneyToUser(user, money);
                        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                        builder.setData("恭喜你获得" + money + "金币,当前人物金币为[" + user.getMoney() + "]");
                        MessageUtil.sendMessage(channel, builder.build());
                    } else {
                        String goodType = rewardArr[0];
                        Integer goodId = Integer.parseInt(rewardArr[1]);
                        goodToUser(goodType, goodId, user, channel);
                    }
                    break;
                }
            }
        }

//      升级
        levelService.upUserLevel(user, monster.getExperience().toString());

//      触发打怪任务事件
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            Achievement achievement = AchievementResourceLoad.achievementMap.get(achievementprocess.getAchievementid());
            if (achievementprocess.getIffinish().equals(AchievementConfig.DOING_TASK) && achievementprocess.getType().equals(AchievementConfig.ATTACKMONSTER)) {
                achievementService.executeKillMonster(user, achievementprocess, monster.getId());
            }
            if (achievementprocess.getIffinish().equals(AchievementConfig.DOING_TASK) && achievementprocess.getType().equals(AchievementConfig.FINISHBOSSAREA) && monster.getType().equals(Monster.TYPEOFBOSS)) {
                achievementService.executeBossAttack(achievementprocess, user, achievement, monster);
            }
        }
    }

    /**
     * 奖励物品给用户
     *
     * @param goodType
     * @param goodId
     * @param user
     * @param channel
     */
    private void goodToUser(String goodType, Integer goodId, User user, Channel channel) {
        if (goodType.equals(BaseGood.EQUIPMENT)) {
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(goodId);
            Userbag userbag = new Userbag();
            userbag.setId(UUID.randomUUID().toString());
            userbag.setName(equipment.getName());
            userbag.setNum(1);
            userbag.setTypeof(BaseGood.EQUIPMENT);
            userbag.setName(equipment.getName());
            userbag.setStartlevel(equipment.getStartLevel());
            userbag.setWid(equipment.getId());
            userbag.setDurability(equipment.getDurability());
            userbagCaculationService.addUserBagForUser(user, userbag);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("恭喜你获得" + equipment.getName() + "增加攻击力为" + equipment.getAddValue());
            MessageUtil.sendMessage(channel, builder.build());
        }
        if (goodType.equals(BaseGood.CHANGEGOOD)) {
            CollectGood collectGood = CollectGoodResourceLoad.collectGoodMap.get(goodId);
            Userbag userbag = new Userbag();
            userbag.setId(UUID.randomUUID().toString());
            userbag.setName(user.getUsername());
            userbag.setNum(1);
            userbag.setWid(goodId);
            userbag.setTypeof(BaseGood.CHANGEGOOD);
            userbagCaculationService.addUserBagForUser(user, userbag);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("恭喜你获得" + collectGood.getName());
            MessageUtil.sendMessage(channel, builder.build());
        }
    }

    private User getUser(Channel channel) {
        return ChannelUtil.channelToUserMap.get(channel);
    }

    /**
     * 最后一击奖励
     * @param user
     * @param channel
     */
    public void extraBonus(User user, Channel channel) {
        moneyCaculationService.addMoneyToUser(user, "200");
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("************最终击杀者额外奖励200金币*************");
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 成就奖励
     *
     * @param achievement
     * @param user
     */
    public void sloveAchievementReward(Achievement achievement, User user) {
        if(!achievement.isGetTask()){
            if (!achievement.getReward().equals(GrobalConfig.NULL)) {
                String[] reward = achievement.getReward().split("-");
                for (String rewardT : reward) {
                    String[] rewardArr = rewardT.split(":");
//                  增加经验值
                    if ("1".equals(rewardArr[0])) {
                        levelService.upUserLevel(user, rewardArr[1]);
                    }
                }
            }
            achievement.setGetTask(true);
        }
    }
}
