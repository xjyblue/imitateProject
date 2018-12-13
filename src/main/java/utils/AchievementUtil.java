package utils;

import achievement.Achievement;
import config.MessageConfig;
import io.netty.channel.Channel;
import memory.NettyMemory;
import packet.PacketType;
import pojo.Achievementprocess;
import pojo.User;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/12 15:46
 */
public class AchievementUtil {

    public static void refreshAchievementInfo(User user) {
        String resp = "";
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            Achievement achievement = NettyMemory.achievementMap.get(achievementprocess.getAchievementid());
            if (achievementprocess.getType() == Achievement.ATTACKMONSTER) {
                String taskName = NettyMemory.achievementMap.get(achievementprocess.getAchievementid()).getName();
                String killNum = achievementprocess.getProcesss().split(":")[1];
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已杀死了" + killNum + "只,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已杀死了" + killNum + "只]" + System.getProperty("line.separator");
                }
            }
            if (achievementprocess.getType() == Achievement.UPLEVEL) {
                String taskName = NettyMemory.achievementMap.get(achievementprocess.getAchievementid()).getName();
                String level = achievementprocess.getProcesss();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已达到了" + level + "级,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已达到了" + level + "级，任务进行中]" + System.getProperty("line.separator");
                }
            }
            if (achievementprocess.getType() == Achievement.TALKTONPC) {
                String taskName = NettyMemory.achievementMap.get(achievementprocess.getAchievementid()).getName();
                String npcName = NettyMemory.npcMap.get(Integer.parseInt(achievement.getTarget())).getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已经和" + npcName + "交流,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未和" + npcName + "交流，任务进行中]" + System.getProperty("line.separator");
                }
            }
            if (achievementprocess.getType() == Achievement.COLLECT) {
                String taskName = NettyMemory.achievementMap.get(achievementprocess.getAchievementid()).getName();
                String goodName = getGoodNameByGoodId(achievementprocess.getProcesss());
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已收集(" + goodName + ")件装备,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已收集(" + goodName + ")件装备，任务进行中]" + System.getProperty("line.separator");
                }
            }
        }
        Channel channel = NettyMemory.userToChannelMap.get(user);
        channel.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.ACHIEVEMENT));
    }

    private static String getGoodNameByGoodId(String process) {
        if (process.equals("0")) {
            return "0";
        }
        String[] goodId = process.split("-");
        String resp = "";
        for (int i = 0; i < goodId.length; i++) {
            resp += NettyMemory.equipmentMap.get(Integer.parseInt(goodId[i])).getName();
            if(i!=goodId.length-1){
                resp += "-";
            }
        }
        return resp;
    }

}
