package service.friendservice.service;

import core.config.GrobalConfig;
import service.achievementservice.entity.Achievement;
import service.achievementservice.service.AchievementService;
import core.config.MessageConfig;
import core.ChannelStatus;
import io.netty.channel.Channel;
import mapper.FriendapplyinfoMapper;
import mapper.FriendinfoMapper;
import mapper.UserMapper;
import core.context.ProjectContext;
import core.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import core.packet.PacketType;
import pojo.*;
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
     * @param channel
     * @param msg
     */
    public void enterFriendView(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, ChannelStatus.FRIEND);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERFRIENDVIEW));
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG, PacketType.FRIENDMSG));
        return;
    }

    /**
     * 退出朋友界面
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qt")
    public void quitFriendView(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, ChannelStatus.COMMONSCENE);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUTFRIENDVIEW));
        channel.writeAndFlush(MessageUtil.turnToPacket("", PacketType.FRIENDMSG));
        return;
    }

    /**
     * 同意交友
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ty=y")
    public void agreeApplyInfo(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        Friendapplyinfo friendapplyinfo = friendapplyinfoMapper.selectByPrimaryKey(temp[1]);
        if (friendapplyinfo == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG + MessageConfig.NOFRIENDRECORD, PacketType.FRIENDMSG));
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
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG + "你同意了" + friendapplyinfo.getFromuser() + "的好友申请", PacketType.FRIENDMSG));
        User userTarget = userService.getUserByNameFromSession(friendapplyinfo.getFromuser());
        if (userTarget != null) {
            Channel channelTarget = ProjectContext.userToChannelMap.get(userTarget);
            channelTarget.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "同意了你的好友申请，你们现在是好友啦"));
        }

//      触发好友成就
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            if (!achievementprocess.getIffinish() && achievementprocess.getType().equals(Achievement.FRIEND)) {
                achievementService.executeAddFirstFriend(achievementprocess, user, userTarget, friendapplyinfo.getFromuser());
            }
        }
        return;
    }

    /**
     * 展示好友
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "lu")
    public void queryFriendToSelf(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        FriendinfoExample friendinfoExample = new FriendinfoExample();
        FriendinfoExample.Criteria criteria = friendinfoExample.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        List<Friendinfo> list = friendinfoMapper.selectByExample(friendinfoExample);
        String resp = "你的好友有";
        if (list.size() == 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG + MessageConfig.NOFRIEND, PacketType.FRIENDMSG));
            return;
        }
        for (Friendinfo friendinfo : list) {
            resp += "[" + friendinfo.getFriendname() + "] " + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG + resp, PacketType.FRIENDMSG));
    }

    /**
     * 申请好友
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "sq-")
    public void applyFriendToOther(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("-");
        if (temp.length != GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(temp[GrobalConfig.ONE]);
        List<User> list = userMapper.selectByExample(userExample);
        if (list.size() == GrobalConfig.ZERO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG + MessageConfig.NOFOUNDMAN, PacketType.FRIENDMSG));
            return;
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG + "你已向" + temp[1] + "发出了好友申请", PacketType.FRIENDMSG));
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
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ls")
    public void queryApplyUserInfo(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        FriendapplyinfoExample friendapplyinfoExample = new FriendapplyinfoExample();
        FriendapplyinfoExample.Criteria criteria = friendapplyinfoExample.createCriteria();
        criteria.andTouserEqualTo(user.getUsername());
        criteria.andApplystatusEqualTo(0);
        List<Friendapplyinfo> list = friendapplyinfoMapper.selectByExample(friendapplyinfoExample);
        String resp = "";
        if (list.size() == 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG + "您无好友申请记录", PacketType.FRIENDMSG));
            return;
        }
        for (Friendapplyinfo friendapplyinfo : list) {
            resp += "[" + friendapplyinfo.getFromuser() + "] 向您发起了好友申请" + "[申请编号：" + friendapplyinfo.getId() + "]" + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG + resp, PacketType.FRIENDMSG));
    }

}
