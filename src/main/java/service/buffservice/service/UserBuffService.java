package service.buffservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import service.caculationservice.service.HpCaculationService;
import service.sceneservice.entity.BossScene;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import service.buffservice.entity.BuffConstant;
import core.config.GrobalConfig;
import core.ChannelStatus;
import core.component.monster.Monster;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import core.packet.PacketType;
import pojo.User;
import service.buffservice.entity.Buff;
import service.teamservice.entity.Team;
import service.attackservice.util.AttackUtil;
import service.levelservice.service.LevelService;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.Map;

/**
 * @ClassName UserBuffService
 * @Description 心跳持续更新用户buff产生效果
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Slf4j
public class UserBuffService {
    @Autowired
    private LevelService levelService;

    private BigInteger add = new BigInteger(GrobalConfig.MPSECONDVALUE);

    private HpCaculationService hpCaculationService;

    public UserBuffService(HpCaculationService hpCaculationService) {
        this.hpCaculationService = hpCaculationService;
    }

    /**
     * 刷新用户buff
     * @param user
     */
    public void refreshUserBuff(User user) {
        try {
            Channel channel = ProjectContext.userToChannelMap.get(user);
            Monster monster = null;
            if (ProjectContext.userToMonsterMap.containsKey(user)) {
                for (Map.Entry<Integer, Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
                    monster = monsterEntry.getValue();
                }
            }

//          拿到用户的所有buff依次进行更新buff
            Map<String, Integer> bufferMap = user.getBuffMap();
            for (Map.Entry<String, Integer> entrySecond : bufferMap.entrySet()) {
//              战斗的buff只在战斗中更新
                if (ProjectContext.eventStatus.get(channel).equals(ChannelStatus.ATTACK) || ProjectContext.eventStatus.get(channel).equals(ChannelStatus.BOSSSCENE)) {
//              更新用户防御buff
                    if (entrySecond.getKey().equals(BuffConstant.DEFENSEBUFF) && entrySecond.getValue() != 3000) {
                        refreshUserDefenseBuff(user);
                    }
//              更新用户被击晕buff
                    if (entrySecond.getKey().equals(BuffConstant.SLEEPBUFF) && entrySecond.getValue() != 5000) {
                        refreshUserSleepBuff(user);
                    }
//              更新用户中毒buff
                    if (entrySecond.getKey().equals(BuffConstant.POISONINGBUFF) && entrySecond.getValue() != 2000) {
                        refreshUserPoisoningBuff(user, entrySecond.getValue());
                    }

//               更新用户召唤师buff
                    if (entrySecond.getKey().equals(BuffConstant.BABYBUF) && entrySecond.getValue() != 7000) {
                        refreshUserBabyBuff(user, monster, entrySecond.getValue());
                    }
                }
//               非战斗的buff随时随地都要更新
//               更新用户回血buff
                if (entrySecond.getKey().equals(BuffConstant.TREATMENTBUFF) && entrySecond.getValue() != 6000) {
                    refreshUserHpBuff(user, entrySecond.getValue());
                }
//               更新用户回蓝buff
                if (entrySecond.getKey().equals(BuffConstant.MPBUFF)) {
                    refreshUserMpBuff(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新用户mpbuff
     * @param user
     */
    private void refreshUserMpBuff(User user) {
        //                 自动回蓝
        BigInteger userMp = new BigInteger(user.getMp());
        BigInteger maxMp = new BigInteger(levelService.getMaxHp(user));
        if (userMp.compareTo(maxMp) < 0) {
            if (user.getBuffMap().get(BuffConstant.MPBUFF).equals(GrobalConfig.MP_DEFAULTVALUE)) {
                userMp = userMp.add(add);
                user.setMp(userMp.toString());
            } else {
                Long endTime = null;
                MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(user.getBuffMap().get(BuffConstant.MPBUFF));
                if (!ProjectContext.userBuffEndTime.containsKey(user)) {
                    endTime = System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000;
                    ProjectContext.userBuffEndTime.get(user).put(BuffConstant.MPBUFF, endTime);
                } else {
                    endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.MPBUFF);
                }
                Long currentTime = System.currentTimeMillis();
                if (endTime > currentTime) {
                    userMp = userMp.add(new BigInteger(mpMedicine.getSecondValue()));
                    if (userMp.compareTo(maxMp) >= 0) {
                        userMp = maxMp;
                    }
                    user.setMp(userMp.toString());
                } else {
                    user.getBuffMap().put(BuffConstant.MPBUFF, 1000);
                    userMp = userMp.add(new BigInteger("10"));
                    if (userMp.compareTo(maxMp) >= 0) {
                        userMp = maxMp;
                    }
                    user.setMp(userMp.toString());
                }
            }
        }
        System.out.println(user.getUsername() + "--的蓝量--" + userMp.toString());
    }

    /**
     * 回血buff处理
     *
     * @param user
     * @param buffValue
     */
    private void refreshUserHpBuff(User user, Integer buffValue) {
//      红药buff处理
        Channel channel = ProjectContext.userToChannelMap.get(user);
        if (ProjectContext.hpMedicineMap.containsKey(buffValue)) {
            HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(buffValue);
            if (hpMedicine.isImmediate()) {
                Long endTime = hpMedicine.getCd() * 1000 + System.currentTimeMillis();
                ProjectContext.userBuffEndTime.get(user).put(BuffConstant.TREATMENTBUFF, endTime);
                hpCaculationService.addUserHp(user, hpMedicine.getReplyValue());
                user.getBuffMap().put(BuffConstant.TREATMENTBUFF, 6000);
            }
            return;
        }
//     技能回血buff处理
//     群体
        Buff buff = ProjectContext.buffMap.get(buffValue);
        if (user.getTeamId() != null) {
            Team team = ProjectContext.teamMap.get(user.getTeamId());
            for (Map.Entry<String, User> entryUser : team.getUserMap().entrySet()) {
                hpCaculationService.addUserHp(entryUser.getValue(), buff.getRecoverValue());
                Channel channelTemp = ProjectContext.userToChannelMap.get(entryUser.getValue());
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "使用了全体回血技能,全体回复血量:" + buff.getRecoverValue()));
            }
//                  单人
        } else {
            hpCaculationService.addUserHp(user, buff.getRecoverValue());
            channel.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "使用了全体回血技能,全体回复血量:" + buff.getRecoverValue()));
        }
        user.getBuffMap().put(BuffConstant.TREATMENTBUFF, 6000);
    }

    /**
     * 星宠师宠物技能buff
     *
     * @param user
     * @param monster
     * @param buffValue
     */
    private void refreshUserBabyBuff(User user, Monster monster, Integer buffValue) {
        Buff buff = ProjectContext.buffMap.get(buffValue);
        Channel channel = ProjectContext.userToChannelMap.get(user);
        Long endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.BABYBUF);
        if (System.currentTimeMillis() > endTime) {
            user.getBuffMap().put(BuffConstant.BABYBUF, 7000);
        } else {
            if (ProjectContext.userToMonsterMap.containsKey(user)) {
                for (Map.Entry<Integer, Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
                    monster = monsterEntry.getValue();
                }

                hpCaculationService.subMonsterHp(monster, buff.getAddSecondValue());
                if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
                    monster.setStatus(GrobalConfig.DEAD);
                    user.getBuffMap().put(BuffConstant.BABYBUF, 7000);
                    if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
                        AttackUtil.killBossMessageToAll(user, monster);
                    }
                }
                channel.writeAndFlush(MessageUtil.turnToPacket("你的召唤兽[" + buff.getName() + "]正在对" + monster.getName() + "造成" + buff.getAddSecondValue() + "点攻击", PacketType.ATTACKMSG));
            }
        }
    }

    /**
     * 更新用户中毒buff
     *
     * @param user
     * @param buffValue
     */
    private void refreshUserPoisoningBuff(User user, Integer buffValue) {
        Long endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.POISONINGBUFF);
        Channel channel = ProjectContext.userToChannelMap.get(user);
        if (System.currentTimeMillis() > endTime) {
            user.getBuffMap().put(BuffConstant.POISONINGBUFF, 2000);
        } else {
            Buff buff = ProjectContext.buffMap.get(buffValue);
            channel.writeAndFlush(MessageUtil.turnToPacket("你受到了怪物的中毒攻击，产生中毒伤害为:" + buff.getAddSecondValue() + "人物剩余血量" + user.getHp(), PacketType.USERBUFMSG));
        }
    }

    /**
     * 更新用户击晕buff
     *
     * @param user
     */
    private void refreshUserSleepBuff(User user) {
        Long endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.SLEEPBUFF);
        if (System.currentTimeMillis() > endTime) {
            user.getBuffMap().put(BuffConstant.SLEEPBUFF, 5000);
        }
    }

    /**
     * 更新用户防御buff
     *
     * @param user
     */
    private void refreshUserDefenseBuff(User user) {
        Long endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.DEFENSEBUFF);
        if (System.currentTimeMillis() > endTime) {
            user.getBuffMap().put(BuffConstant.DEFENSEBUFF, 3000);
        }
    }

}