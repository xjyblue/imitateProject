package service.achievementservice.util;

import service.achievementservice.entity.Achievement;
import core.config.GrobalConfig;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import packet.PacketType;
import pojo.Achievementprocess;
import pojo.User;
import utils.MessageUtil;

import java.util.Map;


/**
 * Description ：nettySpringServer 刷新成就信息
 * Created by server on 2018/12/12 15:46
 */
public class AchievementUtil {

    public static void refreshAchievementInfo(User user) {
        String resp = "";
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
            if (achievementprocess.getType() == Achievement.ATTACKMONSTER) {
                String taskName = achievement.getName();
                String killNum = achievementprocess.getProcesss().split(":")[1];
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已杀死了" + killNum + "只,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已杀死了" + killNum + "只]" + System.getProperty("line.separator");
                }
            }
            if (achievementprocess.getType() == Achievement.UPLEVEL) {
                String taskName = achievement.getName();
                String level = achievementprocess.getProcesss();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已达到了" + level + "级,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已达到了" + level + "级，任务进行中]" + System.getProperty("line.separator");
                }
            }
            if (achievementprocess.getType() == Achievement.TALKTONPC) {
                String taskName = achievement.getName();
                String npcName = ProjectContext.npcMap.get(Integer.parseInt(achievement.getTarget())).getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已经和" + npcName + "交流,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未和" + npcName + "交流，任务进行中]" + System.getProperty("line.separator");
                }
            }
            if (achievementprocess.getType() == Achievement.COLLECT) {
                String taskName = achievement.getName();
                String goodName = getGoodNameByGoodId(achievementprocess.getProcesss());
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已收集(" + goodName + ")件装备,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已收集(" + goodName + ")件装备，任务进行中]" + System.getProperty("line.separator");
                }
            }
            if (achievementprocess.getType() == Achievement.FINISHBOSSAREA) {
                String taskName = achievement.getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已通关天灵魔殿副本，完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未通关天灵魔殿副本，任务进行中]" + System.getProperty("line.separator");
                }
            }

            if (achievementprocess.getType() == Achievement.FRIEND) {
                String taskName = achievement.getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已添加了第一个好友，完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未添加第一个好友，任务进行中]" + System.getProperty("line.separator");
                }
            }

            if (achievement.getType() == Achievement.UNIONFIRST) {
                String taskName = achievement.getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已加入了工会,完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未加入工会，任务进行中]" + System.getProperty("line.separator");
                }
            }

            if (achievement.getType() == Achievement.TRADEFIRST) {
                String taskName = achievement.getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 和其他玩家进行了第一次交易，完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未和其他玩家进行了第一次交易，任务进行中]" + System.getProperty("line.separator");
                }
            }

            if (achievement.getType() == Achievement.MONEYFIRST) {
                String taskName = achievement.getName();
                String money = achievement.getTarget();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 金币达到了" + money + "，完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 金币未达到" + money + "，任务进行中]" + System.getProperty("line.separator");
                }
            }

            if (achievement.getType() == Achievement.TEAMFIRST) {
                String taskName = achievement.getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已加入过了队伍，完成任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未加入过队伍，任务进行中]" + System.getProperty("line.separator");
                }
            }

            if (achievement.getType() == Achievement.COMBINATION) {
                String taskName = achievement.getName();
                String process = achievementprocess.getProcesss();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已完成了称霸村子和起始之地的任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 任务进度：[已完成：" + getCombinationProcessName(process) + "]任务进行中]" + System.getProperty("line.separator");
                }
            }

            if (achievement.getType() == Achievement.PKFIRST) {
                String taskName = achievement.getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已完成了PK击败一名玩家的任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未完成PK击杀一名玩家的任务，任务进行中]" + System.getProperty("line.separator");
                }
            }

            if (achievement.getType() == Achievement.EQUIPMENTSTARTLEVEL) {
                String taskName = achievement.getName();
                if (achievementprocess.getIffinish()) {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 已完成了星级斗者的任务****]" + System.getProperty("line.separator");
                } else {
                    resp += "[任务名:" + taskName + " ]" + " [进度: 未完成星级斗者的任务，任务进行中]" + System.getProperty("line.separator");
                }
            }
        }
        Channel channel = ProjectContext.userToChannelMap.get(user);
        channel.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.ACHIEVEMENT));
    }

    private static String getCombinationProcessName(String process) {
        if(process.equals(GrobalConfig.NULL)){
            return "";
        }
        String[] proArr = process.split("-");
        String resp = "";
        for (String proT : proArr) {
            resp += ProjectContext.achievementMap.get(Integer.parseInt(proT)).getName() + "-";
        }
        resp = resp.substring(0,resp.length()-1);
        return resp;
    }


    public static Achievement getAchievementById(Integer id) {
        for (Map.Entry<Integer, Achievement> entry : ProjectContext.achievementMap.entrySet()) {
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
            resp += ProjectContext.equipmentMap.get(Integer.parseInt(goodId[i])).getName();
            if (i != goodId.length - 1) {
                resp += "-";
            }
        }
        return resp;
    }

}
