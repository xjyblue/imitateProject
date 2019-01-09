package service.teamservice.service;

import core.ChannelStatus;
import core.config.GrobalConfig;
import service.achievementservice.service.AchievementService;
import service.sceneservice.entity.BossScene;
import core.config.MessageConfig;
import io.netty.channel.Channel;
import mapper.TeamapplyinfoMapper;
import core.context.ProjectContext;
import core.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Teamapplyinfo;
import pojo.TeamapplyinfoExample;
import pojo.User;
import service.teamservice.entity.Team;
import utils.MessageUtil;
import service.userservice.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName TeamService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class TeamService {
    @Autowired
    private TeamapplyinfoMapper teamapplyinfoMapper;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserService userService;

    /**
     * 查看队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "team")
    public void queryTeamInfo(Channel channel, String msg) {
        if (getUser(channel).getTeamId() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTEAMMESSAGE));
            return;
        } else {
            Team team = getTeam(getUser(channel));
            String resp = "该队伍中有:[";
            if (team != null && team.getUserMap() != null) {
                for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
                    resp += entry.getKey() + "  ";
                }
                resp += "]该队伍队长是[" + team.getLeader().getUsername() + "]";
                channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTEAMMESSAGE));
                return;
            }
        }
    }

    /**
     * 创建队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "t-create")
    public void createTeam(Channel channel, String msg) {
        User user = getUser(channel);
        if (getUser(channel).getTeamId() != null && getTeam(getUser(channel)) != null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.INTEAMNOCREATETEAM));
            return;
        } else {
            Team team = new Team();
            team.setTeamId(UUID.randomUUID().toString());
            team.setLeader(user);
            user.setTeamId(team.getTeamId());
            HashMap<String, User> teamUserMap = new HashMap<>(64);
            teamUserMap.put(user.getUsername(), user);
            team.setUserMap(teamUserMap);
            ProjectContext.teamMap.put(user.getTeamId(), team);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.CREATETEAMSUCCESSMESSAGE));
            return;
        }
    }

    /**
     * 解散队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "t-remove")
    public void removeTeam(Channel channel, String msg) {
        User user = getUser(channel);
        Team team = getTeam(getUser(channel));
        if (team == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOINTEAMERRORMESSAGE));
            return;
        }
        if (team.getLeader().getUsername().equals(user.getUsername())) {
            ProjectContext.teamMap.remove(user.getTeamId());
            for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
                entry.getValue().setTeamId(null);
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.DISSOLUTIONTEAM));
        } else {
            if (team.getUserMap().containsKey(user.getUsername())) {
                team.getUserMap().remove(user.getUsername());
            }
            user.setTeamId(null);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SIGNOUTTEAM));
        }
    }

    /**
     * 退出队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "t-back")
    public void teamAllRemove(Channel channel, String msg) {
        User user = getUser(channel);
        Team team = getTeam(getUser(channel));
        if (team == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTEAMMESSAGE));
            return;
        }
        if (team.getUserMap().size() == 1) {
            ProjectContext.teamMap.remove(user.getTeamId());
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ONLEAVEFORREMOVE));
            user.setTeamId(null);
            return;
        }
        sendMessageToAll(user, team);
    }

    /**
     * 加入队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "t-add")
    public void addTeam(Channel channel, String msg) {
        User user = getUser(channel);
        if (user.getTeamId() != null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.YOUARENINTEAM));
            return;
        }
        String[] temp = msg.split("-");
        User userLeader = userService.getUserByNameFromSession(temp[2]);
        if (userLeader == null || getTeam(userLeader) == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDTEAM));
            return;
        } else {
            Team team = getTeam(userLeader);
//              加入创建申请单
            if (team == null) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDTEAM));
                return;
            }
            Teamapplyinfo teamapplyinfo = new Teamapplyinfo();
            teamapplyinfo.setId(UUID.randomUUID().toString());
            teamapplyinfo.setUsername(user.getUsername());
            teamapplyinfo.setTeamid(team.getTeamId());
            teamapplyinfoMapper.insertSelective(teamapplyinfo);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSTOAPPLY));

        }
    }

    /**
     * 同意加入队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "t=y")
    public void agreeEnterTeam(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = ProjectContext.channelToUserMap.get(channel);
        Teamapplyinfo teamapplyinfo = teamapplyinfoMapper.selectByPrimaryKey(temp[2]);
        if (teamapplyinfo == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDTEAMAPPLYINFO));
            return;
        }
        User userTarget = userService.getUserByNameFromSession(teamapplyinfo.getUsername());
        if (userTarget == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.USERNOONLINETOADDTEAM));
            return;
        }
        Channel channelTarget = ProjectContext.userToChannelMap.get(userTarget);
        Team team = ProjectContext.teamMap.get(user.getTeamId());
//          加入队伍
        team.getUserMap().put(userTarget.getUsername(), userTarget);
        userTarget.setTeamId(team.getTeamId());
        channel.writeAndFlush(MessageUtil.turnToPacket("您同意了" + userTarget.getUsername() + "加入队伍"));
        channelTarget.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSENTERTEAM));
//          加入后销毁申请记录，队伍应该数据库持久化的。。。
        teamapplyinfoMapper.deleteByPrimaryKey(teamapplyinfo.getId());
//          触发第一次组队的任务
        achievementService.executeFirstAddTeam(userTarget);
        return;
    }

    /**
     * 展示队伍申请者信息
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "t-lu")
    public void queryTeamApplyInfo(Channel channel, String msg) {
//      展示申请者的信息
        User user = ProjectContext.channelToUserMap.get(channel);
        if (user.getTeamId() == null || !ProjectContext.teamMap.containsKey(user.getTeamId())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTEAMMESSAGE));
            return;
        }
        TeamapplyinfoExample teamapplyinfoExample = new TeamapplyinfoExample();
        TeamapplyinfoExample.Criteria criteria = teamapplyinfoExample.createCriteria();
        criteria.andTeamidEqualTo(user.getTeamId());
        List<Teamapplyinfo> teamApplyInfoList = teamapplyinfoMapper.selectByExample(teamapplyinfoExample);
        String resp = "";
        for (Teamapplyinfo teamapplyinfo : teamApplyInfoList) {
            resp += "[ID: " + teamapplyinfo.getId() + "] " + "[用户名:" + teamapplyinfo.getUsername() + "]" + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(resp));
        return;
    }

    /**
     * 全队发送消息
     *
     * @param user
     * @param team
     */
    private void sendMessageToAll(User user, Team team) {
        boolean flag = true;
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            if (entry.getValue() == user) {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket("你已离开队伍"));
                entry.getValue().setTeamId(null);
            } else {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket("玩家" + user.getUsername() + "已离开队伍"));
                if (user == team.getLeader() && flag) {
                    flag = false;
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket("玩家" + entry.getValue().getUsername() + "已成为新的队长"));
                    team.setLeader(entry.getValue());
                }
            }
        }
        team.getUserMap().remove(user.getUsername());
    }

    /**
     * 获得用户所在的队伍
     *
     * @param user
     * @return
     */
    private Team getTeam(User user) {
        if (user.getTeamId() != null && ProjectContext.teamMap.containsKey(user.getTeamId())) {
            return ProjectContext.teamMap.get(user.getTeamId());
        }
        return null;
    }

    private User getUser(Channel channel) {
        return ProjectContext.channelToUserMap.get(channel);
    }

    /**
     * 处理玩家离线
     *
     * @param user
     */
    public void handleUserOffline(User user) {
        Map<String, User> userMap = ProjectContext.teamMap.get(user.getTeamId()).getUserMap();
        if (userMap.size() == 1) {
//          普通的移除
            if (!ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
                ProjectContext.teamMap.remove(user.getTeamId());
                return;
            }

//          游戏副本特殊处理
            BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
            if (bossScene == null) {
                return;
            }
            bossScene.getUserMap().remove(user.getUsername());
//          移除该队伍
            ProjectContext.teamMap.remove(user.getTeamId());
            return;
        }

//      当队长掉线时
        Team team = ProjectContext.teamMap.get(user.getTeamId());
        if (user == team.getLeader()) {
            boolean flag = true;
            for (Map.Entry<String, User> entry : ProjectContext.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
                Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
                if (entry.getValue() != user) {
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "已离线退出队伍"));
                    if (flag) {
                        team.setLeader(entry.getValue());
                        channelTemp.writeAndFlush(MessageUtil.turnToPacket(entry.getValue().getUsername() + "已成为新队长"));
                        flag = false;
                    }
                }
            }
            team.getUserMap().remove(user.getUsername());
            if (!ProjectContext.bossAreaMap.containsKey(team.getTeamId())) {
                return;
            }
            BossScene bossScene = ProjectContext.bossAreaMap.get(team.getTeamId());
            bossScene.getDamageAll().remove(user);
            ProjectContext.bossAreaMap.get(user.getTeamId()).getUserMap().remove(user.getUsername());
            return;
        }

//      普通玩家掉线
        for (Map.Entry<String, User> entry : ProjectContext.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "已离线退出队伍"));
        }
        //    处理玩家打副本
        if (ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
            BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
//          移除玩家的伤害统计
            bossScene.getDamageAll().remove(user);
        }

        ProjectContext.teamMap.get(user.getTeamId()).getUserMap().remove(user.getUsername());
        ProjectContext.bossAreaMap.get(user.getTeamId()).getUserMap().remove(user.getUsername());
    }


    public void enterTeamView(Channel channel, String msg) {
        ProjectContext.channelStatus.put(channel, ChannelStatus.TEAM);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERTEAMMANAGERVIEW));
    }

    /**
     * 退出队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qt")
    public void outTeamView(Channel channel, String msg) {
        ProjectContext.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUTTEAMVIEW));
    }
}
