package event;

import achievement.Achievement;
import achievement.AchievementExecutor;
import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.FriendapplyinfoMapper;
import mapper.FriendinfoMapper;
import mapper.UserMapper;
import context.ProjectContext;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.*;
import utils.MessageUtil;
import utils.UserUtil;

import java.util.List;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/13 17:04
 */
@Component("friendEvent")
public class FriendEvent {
    @Autowired
    private FriendapplyinfoMapper friendapplyinfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FriendinfoMapper friendinfoMapper;
    @Autowired
    private AchievementExecutor achievementExecutor;

    @Order(orderMsg = "p")
    public void enterFriendView(Channel channel,String msg){
        ProjectContext.eventStatus.put(channel, EventStatus.FRIEND);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERFRIENDVIEW));
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG, PacketType.FRIENDMSG));
        return;
    }

    @Order(orderMsg = "q")
    public void quitFriendView(Channel channel,String msg){
        ProjectContext.eventStatus.put(channel, EventStatus.STOPAREA);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUTFRIENDVIEW));
        channel.writeAndFlush(MessageUtil.turnToPacket("", PacketType.FRIENDMSG));
        return;
    }

    @Order(orderMsg = "ty=y")
    public void agreeApplyInfo(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != 2) {
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
        User userTarget = UserUtil.getUserByName(friendapplyinfo.getFromuser());
        if (userTarget != null) {
            Channel channelTarget = ProjectContext.userToChannelMap.get(userTarget);
            channelTarget.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "同意了你的好友申请，你们现在是好友啦"));
        }

//      触发好友成就
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            if (!achievementprocess.getIffinish() && achievementprocess.getType().equals(Achievement.FRIEND)) {
                achievementExecutor.executeAddFirstFriend(achievementprocess,user,userTarget,friendapplyinfo.getFromuser());
            }
        }
        return;
    }

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

    @Order(orderMsg = "sq-")
    public void applyFriendToOther(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(temp[1]);
        List<User> list = userMapper.selectByExample(userExample);
        if (list.size() == 0) {
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

    @Order(orderMsg = "ls")
    public void queryApplyUserInfo(Channel channel,String msg) {
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
