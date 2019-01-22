package service.buffservice.service;

import config.impl.excel.BuffResourceLoad;
import config.impl.excel.HpMedicineResourceLoad;
import config.impl.excel.MpMedicineResourceLoad;
import core.packet.ServerPacket;
import lombok.extern.slf4j.Slf4j;
import service.caculationservice.service.HpCaculationService;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import service.buffservice.entity.BuffConstant;
import core.config.GrobalConfig;
import core.channel.ChannelStatus;
import core.component.monster.Monster;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.buffservice.entity.Buff;
import service.petservice.service.PetService;
import service.teamservice.entity.Team;
import service.levelservice.service.LevelService;
import service.teamservice.entity.TeamCache;
import utils.ChannelUtil;
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
    @Autowired
    private PetService petService;

    private BigInteger add = new BigInteger(GrobalConfig.MPSECONDVALUE);

    private HpCaculationService hpCaculationService;

    public UserBuffService(HpCaculationService hpCaculationService) {
        this.hpCaculationService = hpCaculationService;
    }

    /**
     * 刷新用户buff
     *
     * @param user
     */
    public void refreshUserBuff(User user) {
        try {
            Channel channel = ChannelUtil.userToChannelMap.get(user);
            Monster monster = null;
            if (user.getUserToMonsterMap().size() > 0) {
                for (Map.Entry<Integer, Monster> monsterEntry : user.getUserToMonsterMap().entrySet()) {
                    monster = monsterEntry.getValue();
                }
            }

//          拿到用户的所有buff依次进行更新buff
            Map<String, Integer> bufferMap = user.getBuffMap();
            for (Map.Entry<String, Integer> entrySecond : bufferMap.entrySet()) {
//              战斗的buff只在战斗中更新
                if (ChannelUtil.channelStatus.get(channel).equals(ChannelStatus.ATTACK) || ChannelUtil.channelStatus.get(channel).equals(ChannelStatus.BOSSSCENE)) {
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
//                更新用户嘲讽buff
                    if (entrySecond.getKey().equals(BuffConstant.TAUNTBUFF) && entrySecond.getValue() != 9000) {
                        refreshUserTauntBuff(user);
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

    private void refreshUserTauntBuff(User user) {
        Long endTime = user.getUserBuffEndTimeMap().get(BuffConstant.TAUNTBUFF);
        if (System.currentTimeMillis() > endTime) {
            user.getBuffMap().put(BuffConstant.TAUNTBUFF, 9000);
        }
    }

    /**
     * 刷新用户mpbuff
     *
     * @param user
     */
    private void refreshUserMpBuff(User user) {
//      自动回蓝
        BigInteger userMp = new BigInteger(user.getMp());
        BigInteger maxMp = new BigInteger(levelService.getMaxHp(user));
        if (userMp.compareTo(maxMp) < 0) {
            if (user.getBuffMap().get(BuffConstant.MPBUFF).equals(GrobalConfig.MP_DEFAULTVALUE)) {
                userMp = userMp.add(add);
                user.setMp(userMp.toString());
            } else {
                Long endTime = user.getUserBuffEndTimeMap().get(BuffConstant.MPBUFF);
                MpMedicine mpMedicine = MpMedicineResourceLoad.mpMedicineMap.get(user.getBuffMap().get(BuffConstant.MPBUFF));
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
        Channel channel = ChannelUtil.userToChannelMap.get(user);
        if (HpMedicineResourceLoad.hpMedicineMap.containsKey(buffValue)) {
            HpMedicine hpMedicine = HpMedicineResourceLoad.hpMedicineMap.get(buffValue);
            if (hpMedicine.isImmediate()) {
                Long endTime = hpMedicine.getCd() * 1000 + System.currentTimeMillis();
                user.getUserBuffEndTimeMap().put(BuffConstant.TREATMENTBUFF, endTime);
                hpCaculationService.addUserHp(user, hpMedicine.getReplyValue());
                user.getBuffMap().put(BuffConstant.TREATMENTBUFF, 6000);
            }
            return;
        }
//     技能回血buff处理
//     群体
        Buff buff = BuffResourceLoad.buffMap.get(buffValue);
        if (user.getTeamId() != null) {
            Team team = TeamCache.teamMap.get(user.getTeamId());
            for (Map.Entry<String, User> entryUser : team.getUserMap().entrySet()) {
                hpCaculationService.addUserHp(entryUser.getValue(), buff.getRecoverValue());
                Channel channelTemp = ChannelUtil.userToChannelMap.get(entryUser.getValue());
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(user.getUsername() + "使用了全体回血技能,全体回复血量:" + buff.getRecoverValue());
                MessageUtil.sendMessage(channelTemp, builder.build());
            }
//                  单人
        } else {
            hpCaculationService.addUserHp(user, buff.getRecoverValue());
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(user.getUsername() + "使用了全体回血技能,全体回复血量:" + buff.getRecoverValue());
            MessageUtil.sendMessage(channel, builder.build());
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
        Buff buff = BuffResourceLoad.buffMap.get(buffValue);
        Channel channel = ChannelUtil.userToChannelMap.get(user);
        if (user.getUserToMonsterMap().size() > 0) {
            for (Map.Entry<Integer, Monster> monsterEntry : user.getUserToMonsterMap().entrySet()) {
                monster = monsterEntry.getValue();
            }
            petService.attackMonster(monster, channel);
        }
    }

    /**
     * 更新用户中毒buff
     *
     * @param user
     * @param buffValue
     */
    private void refreshUserPoisoningBuff(User user, Integer buffValue) {
        Long endTime = user.getUserBuffEndTimeMap().get(BuffConstant.POISONINGBUFF);
        Channel channel = ChannelUtil.userToChannelMap.get(user);
        if (System.currentTimeMillis() > endTime) {
            user.getBuffMap().put(BuffConstant.POISONINGBUFF, 2000);
        } else {
            Buff buff = BuffResourceLoad.buffMap.get(buffValue);
            ServerPacket.UserbufResp.Builder builder = ServerPacket.UserbufResp.newBuilder();
            builder.setData("你受到了怪物的中毒攻击，产生中毒伤害为:" + buff.getAddSecondValue() + "人物剩余血量" + user.getHp());
            MessageUtil.sendMessage(channel,builder.build());
        }
    }

    /**
     * 更新用户击晕buff
     *
     * @param user
     */
    private void refreshUserSleepBuff(User user) {
        Long endTime = user.getUserBuffEndTimeMap().get(BuffConstant.SLEEPBUFF);
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
        Long endTime = user.getUserBuffEndTimeMap().get(BuffConstant.DEFENSEBUFF);
        if (System.currentTimeMillis() > endTime) {
            user.getBuffMap().put(BuffConstant.DEFENSEBUFF, 3000);
        }
    }

}
