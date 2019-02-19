package service.bossservice.service;

import com.google.common.collect.Sets;
import config.impl.excel.BossSceneConfigResourceLoad;
import config.impl.excel.SceneResourceLoad;
import config.impl.thread.ThreadPeriodTaskLoad;
import core.annotation.order.OrderRegion;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import service.sceneservice.entity.BossScene;
import core.component.monster.Monster;
import service.sceneservice.entity.Scene;
import core.config.MessageConfig;
import core.config.GrobalConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import core.annotation.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.teamservice.entity.Team;
import service.levelservice.service.LevelService;
import service.teamservice.entity.TeamCache;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName BossService
 * @Description boss副本服务
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@OrderRegion
public class BossService {
    @Autowired
    private LevelService levelService;

    /**
     * 进入boss副本
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.ENTER_BOSSAREA_CONFIG, status = {ChannelStatus.COMMONSCENE})
    public void enterBossArea(Channel channel, String msg) {
        String[] temp = msg.split("=");
//      输入校验
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
//      10级以下无法进入副本
        if (levelService.getLevelByExperience(user.getExperience()) < GrobalConfig.MIN_ENTER_BOSSSCENE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_LEVEL_TO_MOVE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Team team = null;
//      处理用户死亡后重连副本逻辑,死亡后无法重连副本，只能等待副本结束
        if (user.getTeamId() != null && BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.DEAD_NOALLOW_CONNECT_BOSSAREA);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      处理用户一个人加入副本的逻辑
        if (user.getTeamId() == null) {
            team = new Team();
            team.setTeamId(UUID.randomUUID().toString());
            team.setLeader(user);
            user.setTeamId(team.getTeamId());
            HashMap<String, User> teamUserMap = new HashMap<>(64);
            teamUserMap.put(user.getUsername(), user);
            team.setUserMap(teamUserMap);
            TeamCache.teamMap.put(user.getTeamId(), team);
        } else {
//      进入副本队员死亡检查
            team = TeamCache.teamMap.get(user.getTeamId());
            if (checkAllManAlive(team)) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.SOMEBODY_DEAD);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
        }

//      进入副本队长检查
        if (!team.getLeader().getUsername().equals(user.getUsername())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.YOU_ARE_NO_LEADER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

//      队伍一个人直接进入
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        if (team.getUserMap().size() == GrobalConfig.ONE) {
            allMenEnterNewBossArea(temp[1], team);
        } else {
            builder.setData(MessageConfig.ENTER_BOSSAREA_BEGIN_VOTE);
            MessageUtil.sendMessage(channel, builder.build());
//       触发投票检查
            team.setBossAreaVoteTarget(temp[1]);
            team.getVoteSet().add(user.getUsername());
            bossAreaVoteMessageToAll(team);
        }
    }

    /**
     * 投票进副本
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.AGREE_ENTER_BOSSAREA_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void enterBossAreaVote(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        Team team = TeamCache.teamMap.get(user.getTeamId());
        ServerPacket.NormalResp.Builder normalResp = ServerPacket.NormalResp.newBuilder();
        if (team.getVoteSet().contains(user.getUsername())) {
            normalResp.setData(MessageConfig.YOU_ALREADY_AGREE_VOTE);
            MessageUtil.sendMessage(channel, normalResp.build());
            return;
        }
        team.getVoteSet().add(user.getUsername());
        bossAreaVoteMessageToAll(team);
//      校验是否达到开启副本的同意投票数
        if (team.getVoteSet().size() == team.getUserMap().size()) {
//          全队进入副本
            allMenEnterNewBossArea(team.getBossAreaVoteTarget(), team);
        }
    }


    /**
     * 投票提示
     *
     * @param team
     */
    private void bossAreaVoteMessageToAll(Team team) {
        ServerPacket.NormalResp.Builder normalResp = ServerPacket.NormalResp.newBuilder();
//      展示已投票通过的人
        String resp = "";
        for (String agreeMan : team.getVoteSet()) {
            resp += (agreeMan + " ");
        }
//      通知所有人
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelT = ChannelUtil.userToChannelMap.get(entry.getValue());
            User user = ChannelUtil.channelToUserMap.get(channelT);
            if (team.getVoteSet().contains(user.getUsername())) {
                normalResp.setData(MessageConfig.ALREADY_ENTER_BOSSAREA + " [已投票通过的人有：" + resp + "]");
                MessageUtil.sendMessage(channelT, normalResp.build());
            } else {
                normalResp.setData(MessageConfig.IF_ENTER_BOSSAREA + " [已投票通过的人有：" + resp + "]");
                MessageUtil.sendMessage(channelT, normalResp.build());
            }
        }
    }

    /**
     * 进入副本的逻辑
     *
     * @param team
     */
    private void allMenEnterNewBossArea(String bossAreaId, Team team) {
//      初始化队伍投票机制
        team.setVoteSet(Sets.newHashSet());
        team.setBossAreaVoteTarget(null);
//      生成新副本
        BossScene bossScene = new BossScene(team.getTeamId(), bossAreaId);
        BossSceneConfigResourceLoad.bossAreaMap.put(team.getTeamId(), bossScene);
//      改变用户渠道状态
        changeChannelStatus(team, bossScene);
//      开启副本场景帧频线程
        Future future = ThreadPeriodTaskLoad.BOSS_AREA_THREAD_POOL.scheduleAtFixedRate(bossScene, 0, 30, TimeUnit.MILLISECONDS);
        ThreadPeriodTaskLoad.futureMap.put(bossScene.getTeamId(), future);
        bossScene.setFutureMap(ThreadPeriodTaskLoad.futureMap);
    }


    /**
     * 检查是否有人活着
     *
     * @param team
     * @return
     */
    private boolean checkAllManAlive(Team team) {
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            if (entry.getValue().getStatus().equals(GrobalConfig.DEAD)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 第一次进入副本改变队伍中所有用户的状态和提示信息
     *
     * @param team
     * @param bossScene
     */
    private void changeChannelStatus(Team team, BossScene bossScene) {
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
//          移除之前场景线程的用户
            User user = entry.getValue();
            Scene sceneOld = SceneResourceLoad.sceneMap.get(user.getPos());
            sceneOld.getUserMap().remove(user.getUsername());
//          新场景添加用户
            bossScene.getUserMap().put(user.getUsername(), user);

//          更新渠道的状态
            Channel channel = ChannelUtil.userToChannelMap.get(entry.getValue());
            ChannelUtil.channelStatus.put(channel, ChannelStatus.BOSSSCENE);

            String resp = "[全队同意加入副本]" + "进入" + bossScene.getBossName() + "副本,出现boss有：";
            for (Map.Entry<String, Monster> entryMonster : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
                resp += entryMonster.getValue().getName() + " ";
            }
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channel, builder.build());
        }
    }

}
