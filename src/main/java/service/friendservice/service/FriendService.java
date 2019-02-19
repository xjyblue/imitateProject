package service.friendservice.service;

import core.annotation.order.OrderRegion;
import core.config.GrobalConfig;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import service.achievementservice.entity.AchievementConfig;
import service.achievementservice.service.AchievementService;
import core.config.MessageConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import mapper.FriendapplyinfoMapper;
import mapper.FriendinfoMapper;
import mapper.UserMapper;
import core.annotation.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.*;
import utils.ChannelUtil;
import utils.MessageUtil;
import service.userservice.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * @ClassName FriendService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@OrderRegion
public class FriendService {
    @Autowired
    private FriendapplyinfoMapper friendapplyinfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FriendinfoMapper friendinfoMapper;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserService userService;

    /**
     * 进入朋友界面
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.ENTER_FRIEND_VIEW_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void enterFriendView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.FRIEND);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.ENTER_FRIEND_VIEW);
        MessageUtil.sendMessage(channel, builder.build());

        ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
        builder1.setData(MessageConfig.FRIEND_MSG);
        MessageUtil.sendMessage(channel, builder1.build());
        return;
    }

    /**
     * 退出朋友界面
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.QUIT_FRIEND_VIEW_ORDER, status = {ChannelStatus.FRIEND})
    public void quitFriendView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.OUT_FRIEND_VIEW);
        MessageUtil.sendMessage(channel, builder.build());

        ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
        builder1.setData("");
        MessageUtil.sendMessage(channel, builder1.build());
        return;
    }

    /**
     * 同意交友
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.AGREE_GET_NEW_FRIEND_ORDER, status = {ChannelStatus.FRIEND})
    public void agreeApplyInfo(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Friendapplyinfo friendapplyinfo = friendapplyinfoMapper.selectByPrimaryKey(temp[1]);
        if (friendapplyinfo == null) {
            ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
            builder1.setData(MessageConfig.FRIEND_MSG + MessageConfig.NO_FRIEND_RECORD);
            MessageUtil.sendMessage(channel, builder1.build());
            return;
        }
        Friendinfo friendinfo = new Friendinfo();
        friendinfo.setUsername(friendapplyinfo.getFromuser());
        friendinfo.setFriendname(friendapplyinfo.getTouser());
        friendinfoMapper.insert(friendinfo);

        friendinfo = new Friendinfo();
        friendinfo.setUsername(friendapplyinfo.getTouser());
        friendinfo.setFriendname(friendapplyinfo.getFromuser());
        friendinfoMapper.insert(friendinfo);

        friendapplyinfo.setApplystatus(1);
        friendapplyinfoMapper.updateByPrimaryKeySelective(friendapplyinfo);
//      通知双方如果在线的话
        ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
        builder1.setData(MessageConfig.FRIEND_MSG + "你同意了" + friendapplyinfo.getFromuser() + "的好友申请");
        MessageUtil.sendMessage(channel, builder1.build());

        User userTarget = userService.getUserByNameFromSession(friendapplyinfo.getFromuser());
        if (userTarget != null) {
            Channel channelTarget = ChannelUtil.userToChannelMap.get(userTarget);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(user.getUsername() + "同意了你的好友申请，你们现在是好友啦");
            MessageUtil.sendMessage(channelTarget, builder.build());
        }

//      触发好友成就
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            if (achievementprocess.getIffinish().equals(AchievementConfig.DOING_TASK) && achievementprocess.getType().equals(AchievementConfig.FRIEND)) {
                achievementService.executeAddFirstFriend(achievementprocess, user, userTarget, friendapplyinfo.getFromuser());
            }
        }
        return;
    }

    /**
     * 展示好友
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.SHOW_ALL_FRIEND_ORDER, status = {ChannelStatus.FRIEND})
    public void queryFriendToSelf(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        FriendinfoExample friendinfoExample = new FriendinfoExample();
        FriendinfoExample.Criteria criteria = friendinfoExample.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        List<Friendinfo> list = friendinfoMapper.selectByExample(friendinfoExample);
        String resp = "你的好友有";
        if (list.size() == 0) {
            ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
            builder1.setData(MessageConfig.FRIEND_MSG + MessageConfig.NO_FRIEND);
            MessageUtil.sendMessage(channel, builder1.build());
            return;
        }
        for (Friendinfo friendinfo : list) {
            resp += "[" + friendinfo.getFriendname() + "] " + System.getProperty("line.separator");
        }
        ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
        builder1.setData(MessageConfig.FRIEND_MSG + resp);
        MessageUtil.sendMessage(channel, builder1.build());
    }

    /**
     * 申请好友
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.ASK_FRIEND_ORDER, status = {ChannelStatus.FRIEND})
    public void applyFriendToOther(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(temp[GrobalConfig.ONE]);
        List<User> list = userMapper.selectByExample(userExample);
        if (list.size() == GrobalConfig.ZERO) {
            ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
            builder1.setData(MessageConfig.FRIEND_MSG + MessageConfig.NO_FOUND_MAN);
            MessageUtil.sendMessage(channel, builder1.build());
            return;
        } else {
            ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
            builder1.setData(MessageConfig.FRIEND_MSG + "你已向" + temp[1] + "发出了好友申请");
            MessageUtil.sendMessage(channel, builder1.build());
        }
        Friendapplyinfo friendapplyinfo = new Friendapplyinfo();
        friendapplyinfo.setTouser(list.get(0).getUsername());
        friendapplyinfo.setApplystatus(0);
        friendapplyinfo.setFromuser(user.getUsername());
        friendapplyinfo.setId(UUID.randomUUID().toString());
        friendapplyinfoMapper.insertSelective(friendapplyinfo);
    }

    /**
     * 展示好友申请记录
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.SHOW_NEW_FRIEND_REQUEST_ORDER, status = {ChannelStatus.FRIEND})
    public void queryApplyUserInfo(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        FriendapplyinfoExample friendapplyinfoExample = new FriendapplyinfoExample();
        FriendapplyinfoExample.Criteria criteria = friendapplyinfoExample.createCriteria();
        criteria.andTouserEqualTo(user.getUsername());
        criteria.andApplystatusEqualTo(0);
        List<Friendapplyinfo> list = friendapplyinfoMapper.selectByExample(friendapplyinfoExample);
        String resp = "";
        if (list.size() == 0) {
            ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
            builder1.setData(MessageConfig.FRIEND_MSG + "您无好友申请记录");
            MessageUtil.sendMessage(channel, builder1.build());
            return;
        }
        for (Friendapplyinfo friendapplyinfo : list) {
            resp += "[" + friendapplyinfo.getFromuser() + "] 向您发起了好友申请" + "[申请编号：" + friendapplyinfo.getId() + "]" + System.getProperty("line.separator");
        }
        ServerPacket.FriendResp.Builder builder1 = ServerPacket.FriendResp.newBuilder();
        builder1.setData(MessageConfig.FRIEND_MSG + resp);
        MessageUtil.sendMessage(channel, builder1.build());
    }

    @Order(orderMsg = OrderConfig.REMOVE_FRIEND_ORDER, status = {ChannelStatus.FRIEND})
    public void removefriend(Channel channel, String msg) {
        String temp[] = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User userT = userMapper.selectByPrimaryKey(temp[1]);
        if (userT == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_FOUND_MAN);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      在线提示
        User user = userService.getUserByNameFromSession(temp[1]);
        User userS = ChannelUtil.channelToUserMap.get(channel);
        if (user != null) {
            Channel channelTarget = ChannelUtil.userToChannelMap.get(user);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(userS.getUsername() + "和你解除好友关系");
            MessageUtil.sendMessage(channelTarget, builder.build());
        }
//      数据库处理
        friendinfoMapper.deleteByUserName(userS.getUsername(), userT.getUsername());
        friendinfoMapper.deleteByUserName(userT.getUsername(), userS.getUsername());

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("你成功和" + userT.getUsername() + "解除好友关系");
        MessageUtil.sendMessage(channel, builder.build());
    }

}
