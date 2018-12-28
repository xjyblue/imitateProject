package achievement;

import component.Equipment;
import component.Monster;
import component.NPC;
import component.parent.Good;
import level.Level;
import mapper.AchievementprocessMapper;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.AchievementprocessExample;
import pojo.User;
import pojo.Weaponequipmentbar;
import team.Team;
import utils.AchievementUtil;
import utils.LevelUtil;

import java.math.BigInteger;
import java.util.*;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/12 15:04
 */
@Component
public class AchievementExecutor {

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
            Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
            achievementprocess.setProcesss(s);
            if (achievementprocess.getProcesss().equals(achievement.getTarget())) {
                achievementprocess.setIffinish(true);
//              任务奖励
                sloveAchievementReward(achievement,user);
//              处理父任务
                sloveParentProcess(user, achievement);
                achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
            } else {
                achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
            }
            AchievementUtil.refreshAchievementInfo(user);
        }
    }

    private void sloveParentProcess(User user, Achievement achievement) {
        if (!achievement.getParent().equals("0")) {
            Achievement achievementParent = AchievementUtil.getAchievementById(Integer.parseInt(achievement.getParent()));
            String[] sons = achievementParent.getSons().split("-");
            List<String> sonSet = new ArrayList<>();
//              添加要完成的子任务id
            for (String sonT : sons) {
                sonSet.add(sonT);
            }
//              遍历用户的所有任务,找出所有子任务
            String process = "";
            Achievementprocess achievementprocessParent = null;
            for (Achievementprocess achievementprocessT : user.getAchievementprocesses()) {
//                  父子关联，找出用户的此父进度任务
                if (achievementprocessT.getAchievementid().equals(achievementParent.getAchievementId())) {
                    achievementprocessParent = achievementprocessT;
                }
                if (sonSet.contains(achievementprocessT.getAchievementid() + "") && achievementprocessT.getIffinish()) {
                    sonSet.remove(achievementprocessT.getAchievementid() + "");
                    process += achievementprocessT.getAchievementid() + "-";
                }
            }
            process = process.substring(0, process.length() - 1);
//              所有子任务已完成，更新父任务
            if (sonSet.size() == 0) {
                achievementprocessParent.setIffinish(true);

            } else {
                achievementprocessParent.setProcesss(process);
            }
            achievementprocessMapper.updateByPrimaryKeySelective(achievementprocessParent);
        }
    }

    public void executeLevelUp(Achievementprocess achievementprocess, User user, Achievement achievement) {
        achievementprocess.setProcesss(LevelUtil.getLevelByExperience(user.getExperience()) + "");
        if (Integer.parseInt(achievementprocess.getProcesss()) >= Integer.parseInt(achievement.getTarget())) {
            achievementprocess.setIffinish(true);
            achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
        }
        AchievementUtil.refreshAchievementInfo(user);
    }

    public void executeTalkNPC(Achievementprocess achievementprocess, User user, Achievement achievement, NPC npc) {
        if (achievement.getTarget().equals(npc.getId() + "") && !achievementprocess.getIffinish()) {
            achievementprocess.setProcesss(achievement.getTarget());
            achievementprocess.setIffinish(true);
            achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
            sloveAchievementReward(achievement,user);
        }
        AchievementUtil.refreshAchievementInfo(user);
    }

    private void sloveAchievementReward(Achievement achievement, User user) {
        if (!achievement.getReward().equals("0")) {
            String reward[] = achievement.getReward().split("-");
            for (String rewardT : reward) {
                String rewardArr[] = rewardT.split(":");
//                  增加经验值
                if(rewardArr[0].equals("1")){
                    LevelUtil.upUserLevel(user,rewardArr[1]);
                }
            }
        }
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
                            if (!currentSet.contains(targetTemp)) {
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

    public void executeBossAttack(Achievementprocess achievementprocess, User user, Achievement achievement, Monster monster) {
        if (monster.getId().equals(Integer.parseInt(achievement.getTarget()))) {
//          此任务比较特殊和队伍挂钩
            achievementprocess.setIffinish(true);
            achievementprocess.setProcesss(achievement.getTarget());
            updateAchievementprocessWithOther(user, monster);
        }
        AchievementUtil.refreshAchievementInfo(user);
    }

    public void executeAddFirstFriend(Achievementprocess achievementprocess, User user, User userTarget, String fromUser) {
//      更新用户自己的
        Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
        if (!achievementprocess.getIffinish()) {
            achievementprocess.setProcesss(achievement.getTarget());
            achievementprocess.setIffinish(true);
            achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
        }

//      更新对方的
        if (userTarget != null) {
            for (Achievementprocess achievementprocessT : userTarget.getAchievementprocesses()) {
                if (!achievementprocessT.getIffinish() && achievementprocessT.getType().equals(Achievement.FRIEND)) {
                    achievementprocessT.setProcesss(achievement.getTarget());
                    achievementprocessT.setIffinish(true);
                    achievementprocessMapper.updateByPrimaryKeySelective(achievementprocessT);
                }
            }
            AchievementUtil.refreshAchievementInfo(userTarget);
        } else {
            Achievementprocess achievementprocessT = achievementprocessMapper.selectprocessByUsernameAndAchievementId(fromUser, achievement.getAchievementId());
            if (!achievementprocessT.getIffinish()) {
                achievementprocessT.setProcesss(achievement.getTarget());
                achievementprocessT.setIffinish(true);
                achievementprocessMapper.updateByPrimaryKeySelective(achievementprocessT);
            }
        }

        AchievementUtil.refreshAchievementInfo(user);
    }

    public void executeMoneyAchievement(User user) {
        for (Achievementprocess achievementprocessT : user.getAchievementprocesses()) {
            if (achievementprocessT.getType().equals(Achievement.MONEYFIRST)) {
                Achievement achievement = ProjectContext.achievementMap.get(achievementprocessT.getAchievementid());
                BigInteger targetMoney = new BigInteger(achievement.getTarget());
                BigInteger userMoney = new BigInteger(user.getMoney());
                if (userMoney.compareTo(targetMoney) >= 0) {
                    achievementprocessT.setProcesss(achievement.getTarget());
                    achievementprocessT.setIffinish(true);
                    achievementprocessMapper.updateByPrimaryKeySelective(achievementprocessT);
                }
                return;
            }
        }
        AchievementUtil.refreshAchievementInfo(user);
    }


    public void executeAddUnionFirst(User user, String username) {
        List<Achievementprocess> list = null;
        if (user != null) {
            list = user.getAchievementprocesses();
        } else {
            AchievementprocessExample achievementprocessExample = new AchievementprocessExample();
            AchievementprocessExample.Criteria criteria = achievementprocessExample.createCriteria();
            criteria.andUsernameEqualTo(username);
            list = achievementprocessMapper.selectByExample(achievementprocessExample);
        }
        for (Achievementprocess achievementprocess : list) {
            if (achievementprocess.getType().equals(Achievement.UNIONFIRST) && !achievementprocess.getIffinish()) {
                Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
                achievementprocess.setIffinish(true);
                achievementprocess.setProcesss(achievement.getTarget());
                achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);
            }
        }
        if (user != null) {
            AchievementUtil.refreshAchievementInfo(user);
        }
    }

    //
    private void updateAchievementprocessWithOther(User user, Monster monster) {
        Team team = ProjectContext.teamMap.get(user.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            User userT = entry.getValue();
            for (Achievementprocess achievementprocess : userT.getAchievementprocesses()) {
                Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
                if (achievementprocess.getType().equals(Achievement.FINISHBOSSAREA) && monster.getId().equals(Integer.parseInt(achievement.getTarget()))) {
//                  此任务比较特殊和队伍挂钩
                    achievementprocess.setIffinish(true);
                    achievementprocess.setProcesss(achievement.getTarget());
                }
            }
        }
    }


    public void executeFirstTrade(User userStart, User userTo) {
//      更新一方的交易成就
        updateFirstTradeOnOneUser(userStart);
//      更新另一方的交易成就
        updateFirstTradeOnOneUser(userTo);
        AchievementUtil.refreshAchievementInfo(userStart);
        AchievementUtil.refreshAchievementInfo(userTo);
    }

    private void updateFirstTradeOnOneUser(User user) {
        for (Achievementprocess achievementprocessT : user.getAchievementprocesses()) {
            Achievement achievement = ProjectContext.achievementMap.get(achievementprocessT.getAchievementid());
            if (!achievementprocessT.getIffinish() && achievementprocessT.getType().equals(Achievement.TRADEFIRST)) {
                achievementprocessT.setProcesss(achievement.getTarget());
                achievementprocessT.setIffinish(true);
                achievementprocessMapper.updateByPrimaryKeySelective(achievementprocessT);
            }
        }
    }


    public void executeFirstAddTeam(User user) {
        for (Achievementprocess achievementprocessT : user.getAchievementprocesses()) {
            if (achievementprocessT.getType().equals(Achievement.TEAMFIRST) && !achievementprocessT.getIffinish()) {
                achievementprocessT.setIffinish(true);
                achievementprocessMapper.updateByPrimaryKeySelective(achievementprocessT);
            }
        }
        AchievementUtil.refreshAchievementInfo(user);
    }

    public void executeFirstPKWin(User user) {
        for (Achievementprocess achievementprocessT : user.getAchievementprocesses()) {
            if (achievementprocessT.getType().equals(Achievement.PKFIRST) && !achievementprocessT.getIffinish()) {
                achievementprocessT.setIffinish(true);
                achievementprocessMapper.updateByPrimaryKeySelective(achievementprocessT);
            }
        }
        AchievementUtil.refreshAchievementInfo(user);
    }

    public void executeEquipmentStartLevel(User user) {
        for (Achievementprocess achievementprocessT : user.getAchievementprocesses()) {
            if (achievementprocessT.getType().equals(Achievement.EQUIPMENTSTARTLEVEL) && !achievementprocessT.getIffinish()) {
                Achievement achievement = ProjectContext.achievementMap.get(achievementprocessT.getAchievementid());
                if (checkUserWeaponStartLevel(user, achievement)) {
                    achievementprocessT.setIffinish(true);
                    achievementprocessMapper.updateByPrimaryKeySelective(achievementprocessT);
                }
            }
        }
        AchievementUtil.refreshAchievementInfo(user);
    }

    private boolean checkUserWeaponStartLevel(User user, Achievement achievement) {
        Integer allLevel = 0;
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            allLevel += weaponequipmentbar.getStartlevel();
        }
        Integer tragetLevel = Integer.parseInt(achievement.getTarget());
        if (allLevel >= tragetLevel) {
            return true;
        }
        return false;
    }
}
