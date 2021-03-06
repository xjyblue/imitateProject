package service.teamservice.service;

import config.impl.excel.BossSceneConfigResourceLoad;
import core.channel.ChannelStatus;
import core.annotation.order.OrderRegion;
import core.config.GrobalConfig;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import service.achievementservice.service.AchievementService;
import service.sceneservice.entity.BossScene;
import core.config.MessageConfig;
import io.netty.channel.Channel;
import mapper.TeamapplyinfoMapper;
import core.annotation.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Teamapplyinfo;
import pojo.TeamapplyinfoExample;
import pojo.User;
import service.teamservice.entity.Team;
import service.teamservice.entity.TeamCache;
import utils.ChannelUtil;
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
@OrderRegion
public class TeamService {

    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamapplyinfoMapper teamapplyinfoMapper;

    /**
     * 查看队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.SHOW_TEAM_INFO_ORDER, status = {ChannelStatus.TEAM})
    public void queryTeamInfo(Channel channel, String msg) {
        if (getUser(channel).getTeamId() == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_TEAM_MESSAGE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        } else {
            Team team = getTeam(getUser(channel));
            String resp = "该队伍中有:[";
            if (team != null && team.getUserMap() != null) {
                for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
                    resp += entry.getKey() + "  ";
                }
                resp += "]该队伍队长是[" + team.getLeader().getUsername() + "]";
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(resp);
                MessageUtil.sendMessage(channel, builder.build());
            } else {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NO_TEAM_MESSAGE);
                MessageUtil.sendMessage(channel, builder.build());
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
    @Order(orderMsg = OrderConfig.CREATE_TEAM_ORDER, status = {ChannelStatus.TEAM})
    public void createTeam(Channel channel, String msg) {
        User user = getUser(channel);
        if (getUser(channel).getTeamId() != null && getTeam(getUser(channel)) != null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.IN_TEAM_NO_CREATE_TEAM);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Team team = new Team();
        team.setTeamId(UUID.randomUUID().toString());
        team.setLeader(user);
        team.setTeamName(user.getUsername());
        user.setTeamId(team.getTeamId());
        HashMap<String, User> teamUserMap = new HashMap<>(64);
        teamUserMap.put(user.getUsername(), user);
        team.setUserMap(teamUserMap);
        TeamCache.teamMap.put(user.getTeamId(), team);

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.CREATE_TEAM_SUCCESS_MESSAGE);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 解散队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.REMOVE_TEAM_ALL_MEMBER_ORDER, status = {ChannelStatus.TEAM})
    public void removeTeam(Channel channel, String msg) {
        User user = getUser(channel);
        Team team = getTeam(getUser(channel));
        if (team == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_IN_TEAM_ERROR_MESSAGE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (team.getLeader().getUsername().equals(user.getUsername())) {
            TeamCache.teamMap.remove(user.getTeamId());
            for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
                entry.getValue().setTeamId(null);
            }
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.DISSOLUTION_TEAM);
            MessageUtil.sendMessage(channel, builder.build());
        } else {
            if (team.getUserMap().containsKey(user.getUsername())) {
                team.getUserMap().remove(user.getUsername());
            }
            user.setTeamId(null);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.SIGN_OUT_TEAM);
            MessageUtil.sendMessage(channel, builder.build());
        }
    }

    /**
     * 退出队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.BACK_TEAM_ORDER, status = {ChannelStatus.TEAM})
    public void teamAllRemove(Channel channel, String msg) {
        User user = getUser(channel);
        Team team = getTeam(getUser(channel));
        if (team == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_TEAM_MESSAGE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (team.getUserMap().size() == 1) {
            TeamCache.teamMap.remove(user.getTeamId());

            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ONE_MAN_REMOVE_TEAM);
            MessageUtil.sendMessage(channel, builder.build());
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
    @Order(orderMsg = OrderConfig.REQUEST_NEW_TEAM_ORDER, status = {ChannelStatus.TEAM})
    public void addTeam(Channel channel, String msg) {
        User user = getUser(channel);
        if (user.getTeamId() != null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.YOU_ARE_IN_TEAM);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        String[] temp = msg.split("=");
        User userLeader = userService.getUserByNameFromSession(temp[1]);
        if (userLeader == null || getTeam(userLeader) == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_FOUND_TEAM);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Team team = getTeam(userLeader);
//      加入创建申请单
        if (team == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_FOUND_TEAM);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Teamapplyinfo teamapplyinfo = new Teamapplyinfo();
        teamapplyinfo.setId(UUID.randomUUID().toString());
        teamapplyinfo.setUsername(user.getUsername());
        teamapplyinfo.setTeamid(team.getTeamId());
        teamapplyinfoMapper.insertSelective(teamapplyinfo);

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.SUCCESS_TO_APPLY);
        MessageUtil.sendMessage(channel, builder.build());

        Channel leaderChannel = ChannelUtil.userToChannelMap.get(userLeader);
        builder.setData("[申请编号:" + teamapplyinfo.getId() + "]" + " [申请者:" + teamapplyinfo.getUsername() + "]");
        MessageUtil.sendMessage(leaderChannel, builder.build());

    }

    /**
     * 同意加入队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.AGREE_ADD_NEW_TEAM_ORDER, status = {ChannelStatus.TEAM})
    public void agreeEnterTeam(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
        Teamapplyinfo teamapplyinfo = teamapplyinfoMapper.selectByPrimaryKey(temp[1]);
        if (teamapplyinfo == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_FOUND_TEAM_APPLY_INFO);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User userTarget = userService.getUserByNameFromSession(teamapplyinfo.getUsername());
        if (userTarget == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.USER_NO_ONLINE_TO_ADD_TEAM);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Channel channelTarget = ChannelUtil.userToChannelMap.get(userTarget);
        Team team = TeamCache.teamMap.get(user.getTeamId());
//          加入队伍
        team.getUserMap().put(userTarget.getUsername(), userTarget);
        userTarget.setTeamId(team.getTeamId());

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("您同意了" + userTarget.getUsername() + "加入队伍");
        MessageUtil.sendMessage(channel, builder.build());

        builder.setData(MessageConfig.SUCCESS_ENTER_TEAM);
        MessageUtil.sendMessage(channelTarget, builder.build());

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
    @Order(orderMsg = OrderConfig.SHOW_ALL_TEAM_APPLY_INFO_ORDER, status = {ChannelStatus.TEAM})
    public void queryTeamApplyInfo(Channel channel, String msg) {
//      展示申请者的信息
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (user.getTeamId() == null || !TeamCache.teamMap.containsKey(user.getTeamId())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_TEAM_MESSAGE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        TeamapplyinfoExample teamapplyinfoExample = new TeamapplyinfoExample();
        TeamapplyinfoExample.Criteria criteria = teamapplyinfoExample.createCriteria();
        criteria.andTeamidEqualTo(user.getTeamId());
        List<Teamapplyinfo> teamApplyInfoList = teamapplyinfoMapper.selectByExample(teamapplyinfoExample);
        String resp = "";
        if (teamApplyInfoList.size() == 0) {
            resp = "无用户申请记录";
        }
        for (Teamapplyinfo teamapplyinfo : teamApplyInfoList) {
            resp += "[ID: " + teamapplyinfo.getId() + "] " + "[用户名:" + teamapplyinfo.getUsername() + "]" + System.getProperty("line.separator");
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(resp);
        MessageUtil.sendMessage(channel, builder.build());
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
            Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
            if (entry.getValue() == user) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("你已离开队伍");
                MessageUtil.sendMessage(channelTemp, builder.build());
                entry.getValue().setTeamId(null);
            } else {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("玩家" + user.getUsername() + "已离开队伍");
                MessageUtil.sendMessage(channelTemp, builder.build());
                if (user == team.getLeader() && flag) {
                    flag = false;
                    builder.setData("玩家" + entry.getValue().getUsername() + "已成为新的队长");
                    MessageUtil.sendMessage(channelTemp, builder.build());
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
        if (user.getTeamId() != null && TeamCache.teamMap.containsKey(user.getTeamId())) {
            return TeamCache.teamMap.get(user.getTeamId());
        }
        return null;
    }

    private User getUser(Channel channel) {
        return ChannelUtil.channelToUserMap.get(channel);
    }

    /**
     * 处理玩家离线
     *
     * @param user
     */
    public void handleUserOffline(User user) {
        Map<String, User> userMap = TeamCache.teamMap.get(user.getTeamId()).getUserMap();
//      队伍1个人时
        if (userMap.size() == 1) {
//          普通的移除
            if (!BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
                TeamCache.teamMap.remove(user.getTeamId());
                return;
            }
//          游戏副本特殊处理
            BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
            if (bossScene == null) {
                return;
            }
            bossScene.getUserMap().remove(user.getUsername());
//          移除该队伍
            TeamCache.teamMap.remove(user.getTeamId());
            return;
        }

//      当队长掉线时
        Team team = TeamCache.teamMap.get(user.getTeamId());
//      移除副本中的玩家
        if (BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
            BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
            bossScene.getDamageAll().remove(user);
            BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId()).getUserMap().remove(user.getUsername());
        }
//      移除队伍中的玩家
        removeUserFromTeam(user, team);
    }

    /**
     * 将某个玩家移出队伍
     *
     * @param user
     * @param team
     */
    public void removeUserFromTeam(User user, Team team) {
//      队长退出队伍
        if (user == team.getLeader()) {
            boolean flag = true;
            for (Map.Entry<String, User> entry : TeamCache.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
                Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
                if (entry.getValue() != user) {
                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData(user.getUsername() + "已退出队伍");
                    MessageUtil.sendMessage(channelTemp, builder.build());
                    if (flag) {
                        team.setLeader(entry.getValue());
                        builder.setData(entry.getValue().getUsername() + "已成为新队长");
                        MessageUtil.sendMessage(channelTemp, builder.build());
                        flag = false;
                    }
                }
            }
        } else {
            //      普通玩家退出队伍
            for (Map.Entry<String, User> entry : TeamCache.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
                Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(user.getUsername() + "已退出队伍");
                MessageUtil.sendMessage(channelTemp, builder.build());
            }
        }
        team.getUserMap().remove(user.getUsername());
        user.setTeamId(null);
    }

    @Order(orderMsg = OrderConfig.SHOW_ALL_TEAM_INFO_ORDER, status = {ChannelStatus.TEAM})
    public void queryAllTeam(Channel channel, String msg) {
        if (TeamCache.teamMap.size() == 0) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_TEAM_RECORD);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        String resp = "";
        for (Map.Entry<String, Team> teamEntry : TeamCache.teamMap.entrySet()) {
            Team team = teamEntry.getValue();
            resp += "[队伍的id:" + team.getTeamId() + "]" + " [队伍的名称:" + team.getLeader().getUsername() + "的队伍]" + " [队伍有:";
            for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
                User userT = entry.getValue();
                resp += userT.getUsername() + " ";
            }
            resp += "]" + System.getProperty("line.separator");
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(resp);
        MessageUtil.sendMessage(channel, builder.build());
    }

    @Order(orderMsg = OrderConfig.ENTER_TEAM_VIEW_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void enterTeamView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.TEAM);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.ENTER_TEAM_MANAGER_VIEW);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 退出队伍
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.QUIT_TEAM_VIEW_ORDER, status = {ChannelStatus.TEAM})
    public void outTeamView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.OUT_TEAM_VIEW);
        MessageUtil.sendMessage(channel, builder.build());
    }
}
