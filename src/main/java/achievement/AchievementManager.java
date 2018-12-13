package achievement;

import component.Equipment;
import component.NPC;
import component.parent.Good;
import mapper.AchievementprocessMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import utils.AchievementUtil;
import utils.LevelUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/12 15:04
 */
@Component
public class AchievementManager {

    @Autowired
    private AchievementprocessMapper achievementprocessMapper;

    public void executeKillMonster(User user, Achievementprocess achievementprocess, Integer monsterId) {
        if (achievementprocess.getType().equals(Achievement.ATTACKMONSTER)) {
            String temp[] = achievementprocess.getProcesss().split("-");
            String s = "";
            for (int i = 0; i < temp.length; i++) {
                String temp1 = temp[i];
                String[] temp2 = temp1.split(":");
                if (temp2[0].equals(monsterId + "")) {
                    temp2[1] = Integer.parseInt(temp2[1]) + 1 + "";
                    temp1 = temp2[0] + ":" + temp2[1];
                }
                s += temp1;
                if (i != temp.length - 1) {
                    s += "-";
                }
            }
            Achievement achievement = NettyMemory.achievementMap.get(achievementprocess.getAchievementid());
            achievementprocess.setProcesss(s);
            if (achievementprocess.getProcesss().equals(achievement.getTarget())) {
                achievementprocess.setIffinish(true);
            }
            achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
            AchievementUtil.refreshAchievementInfo(user);
        }
    }

    public void executeLevelUp(Achievementprocess achievementprocess, User user, Achievement achievement) {
        achievementprocess.setProcesss(LevelUtil.getLevelByExperience(user.getExperience()) + "");
        if (Integer.parseInt(achievementprocess.getProcesss()) >= Integer.parseInt(achievement.getTarget())) {
            achievementprocess.setIffinish(true);
        }
        achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
        AchievementUtil.refreshAchievementInfo(user);
    }

    public void executeTalkNPC(Achievementprocess achievementprocess, User user, Achievement achievement, NPC npc) {
        if (achievement.getTarget().equals(npc.getId() + "") && !achievementprocess.getIffinish()) {
            achievementprocess.setProcesss(achievement.getTarget());
            achievementprocess.setIffinish(true);
        }
        achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
        AchievementUtil.refreshAchievementInfo(user);
    }

    public void executeCollect(Achievementprocess achievementprocess, Good good, User user, Achievement achievement) {
        if (!achievementprocess.getIffinish()) {
            if (good instanceof Equipment) {
                Equipment equipment = (Equipment) good;

                String target[] = achievement.getTarget().split("-");
                Set<String> targetSet = new HashSet<>();
                for (String targetTemp : target) {
                    targetSet.add(targetTemp);
                }

                if (targetSet.contains(equipment.getId() + "")) {
//                 包含目标才处理
                    if (achievementprocess.getProcesss().equals("0")) {
                        achievementprocess.setProcesss(equipment.getId() + "");
                    } else {
//                       检查里面有没有该物品，没有再添加，添加完再校验
                        String current[] = achievementprocess.getProcesss().split("-");
                        Set<String> currentSet = new HashSet<>();
                        for (String currentTemp : current) {
                            currentSet.add(currentTemp);
                        }
//                      进度不包含才处理
                        if (!currentSet.contains(equipment.getId() + "")) {
                            achievementprocess.setProcesss(achievementprocess.getProcesss() + "-" + equipment.getId());
                            currentSet.add(equipment.getId() + "");
                        }
                        for (String targetTemp : target) {
                            if(!currentSet.contains(targetTemp)){
                                AchievementUtil.refreshAchievementInfo(user);
                                return;
                            }
                        }
                        achievementprocess.setIffinish(true);
                    }
                }
                achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
                AchievementUtil.refreshAchievementInfo(user);
            }
        }
    }
}
