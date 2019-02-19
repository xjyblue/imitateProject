package service.achievementservice.util;

import config.impl.excel.AchievementResourceLoad;
import config.impl.excel.EquipmentResourceLoad;
import config.impl.excel.NpcResourceLoad;
import core.config.MessageConfig;
import core.packet.ServerPacket;
import service.achievementservice.entity.Achievement;
import core.config.GrobalConfig;
import io.netty.channel.Channel;
import pojo.Achievementprocess;
import pojo.User;
import service.achievementservice.entity.AchievementConfig;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.Map;


/**
 * @ClassName AchievementUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class AchievementUtil {
    /**
     * 成就提示
     *
     * @param user
     */
    public static void refreshAchievementInfo(User user) {
        String finishResp = "";
        String doingResp = "";
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            Achievement achievement = AchievementResourceLoad.achievementMap.get(achievementprocess.getAchievementid());
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                finishResp = refreshAchivement(finishResp, achievementprocess, achievement);
            } else {
                doingResp = refreshAchivement(doingResp, achievementprocess, achievement);
            }
        }
        Channel channel = ChannelUtil.userToChannelMap.get(user);
        ServerPacket.AchievementResp.Builder builder3 = ServerPacket.AchievementResp.newBuilder();
        builder3.setData(MessageConfig.FINISH_TASK + finishResp + MessageConfig.MESSAGE_MID + System.getProperty("line.separator") + MessageConfig.DOING_TASK + doingResp);
        MessageUtil.sendMessage(channel, builder3.build());
    }

    private static boolean checkAchievementProcessIfFinish(Achievementprocess achievementprocess) {
        if (achievementprocess.getIffinish().equals(AchievementConfig.COMPLETE_TASK)) {
            return true;
        }
        if (achievementprocess.getIffinish().equals(AchievementConfig.COMPLETE_AND_GIVE_TASK)) {
            return true;
        }
        return false;
    }

    private static String refreshAchivement(String resp, Achievementprocess achievementprocess, Achievement achievement) {
        if (achievementprocess.getType() == AchievementConfig.ATTACKMONSTER) {
            String taskName = achievement.getName();
            String killNum = achievementprocess.getProcesss().split(":")[1];
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已杀死了" + killNum + "只,完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已杀死了" + killNum + "只]" + System.getProperty("line.separator");
            }
        }
        if (achievementprocess.getType() == AchievementConfig.UPLEVEL) {
            String taskName = achievement.getName();
            String level = achievementprocess.getProcesss();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已达到了" + level + "级,完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已达到了" + level + "级，任务进行中]" + System.getProperty("line.separator");
            }
        }
        if (achievementprocess.getType() == AchievementConfig.TALKTONPC) {
            String taskName = achievement.getName();
            String npcName = NpcResourceLoad.npcMap.get(Integer.parseInt(achievement.getTarget())).getName();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已经和" + npcName + "交流,完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 未和" + npcName + "交流，任务进行中]" + System.getProperty("line.separator");
            }
        }
        if (achievementprocess.getType() == AchievementConfig.COLLECT) {
            String taskName = achievement.getName();
            String goodName = getGoodNameByGoodId(achievementprocess.getProcesss());
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已收集(" + goodName + ")件装备,完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已收集(" + goodName + ")件装备，任务进行中]" + System.getProperty("line.separator");
            }
        }
        if (achievementprocess.getType() == AchievementConfig.FINISHBOSSAREA) {
            String taskName = achievement.getName();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已通关天灵魔殿副本，完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 未通关天灵魔殿副本，任务进行中]" + System.getProperty("line.separator");
            }
        }

        if (achievementprocess.getType() == AchievementConfig.FRIEND) {
            String taskName = achievement.getName();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已添加了第一个好友，完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 未添加第一个好友，任务进行中]" + System.getProperty("line.separator");
            }
        }

        if (achievement.getType() == AchievementConfig.UNIONFIRST) {
            String taskName = achievement.getName();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已加入了工会,完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 未加入工会，任务进行中]" + System.getProperty("line.separator");
            }
        }

        if (achievement.getType() == AchievementConfig.TRADEFIRST) {
            String taskName = achievement.getName();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 和其他玩家进行了第一次交易，完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 未和其他玩家进行了第一次交易，任务进行中]" + System.getProperty("line.separator");
            }
        }

        if (achievement.getType() == AchievementConfig.MONEYFIRST) {
            String taskName = achievement.getName();
            String money = achievement.getTarget();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 金币达到了" + money + "，完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 金币未达到" + money + "，任务进行中]" + System.getProperty("line.separator");
            }
        }

        if (achievement.getType() == AchievementConfig.TEAMFIRST) {
            String taskName = achievement.getName();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已加入过了队伍，完成任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 未加入过队伍，任务进行中]" + System.getProperty("line.separator");
            }
        }

        if (achievement.getType() == AchievementConfig.COMBINATION) {
            String taskName = achievement.getName();
            String process = achievementprocess.getProcesss();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已完成了称霸村子和起始之地的任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 任务进度：[已完成：" + getCombinationProcessName(process) + "]任务进行中]" + System.getProperty("line.separator");
            }
        }

        if (achievement.getType() == AchievementConfig.PKFIRST) {
            String taskName = achievement.getName();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已完成了PK击败一名玩家的任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 未完成PK击杀一名玩家的任务，任务进行中]" + System.getProperty("line.separator");
            }
        }

        if (achievement.getType() == AchievementConfig.EQUIPMENTSTARTLEVEL) {
            String taskName = achievement.getName();
            if (checkAchievementProcessIfFinish(achievementprocess)) {
                resp += "[任务名:" + taskName + " ]" + " [进度: 已完成了星级斗者的任务****]" + System.getProperty("line.separator");
            } else {
                resp += "[任务名:" + taskName + " ]" + " [进度: 未完成星级斗者的任务，任务进行中]" + System.getProperty("line.separator");
            }
        }
        return resp;
    }

    private static String getCombinationProcessName(String process) {
        if (process.equals(GrobalConfig.NULL)) {
            return "";
        }
        String[] proArr = process.split("-");
        String resp = "";
        for (String proT : proArr) {
            resp += AchievementResourceLoad.achievementMap.get(Integer.parseInt(proT)).getName() + "-";
        }
        resp = resp.substring(0, resp.length() - 1);
        return resp;
    }


    public static Achievement getAchievementById(Integer id) {
        for (Map.Entry<Integer, Achievement> entry : AchievementResourceLoad.achievementMap.entrySet()) {
            if (entry.getValue().getAchievementId().equals(id)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String getGoodNameByGoodId(String process) {
        if (process.equals(GrobalConfig.NULL)) {
            return GrobalConfig.NULL;
        }
        String[] goodId = process.split("-");
        String resp = "";
        for (int i = 0; i < goodId.length; i++) {
            resp += EquipmentResourceLoad.equipmentMap.get(Integer.parseInt(goodId[i])).getName();
            if (i != goodId.length - 1) {
                resp += "-";
            }
        }
        return resp;
    }

}
