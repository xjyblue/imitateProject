package event;

import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.FriendapplyinfoMapper;
import mapper.FriendinfoMapper;
import mapper.UserMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.*;
import sun.plugin2.message.Message;
import utils.MessageUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/13 17:04
 */
@Component("friendEvent")
public class FriendEvent {
    @Autowired
    private FriendapplyinfoMapper friendapplyinfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FriendinfoMapper friendinfoMapper;

    public void solve(Channel channel, String msg) {
        if (msg.equals("p")) {
            NettyMemory.eventStatus.put(channel, EventStatus.FRIEND);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERFRIENDVIEW));
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FRIENDMSG, PacketType.FRIENDMSG));
            return;
        }
        if (msg.equals("q")) {
            NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUTFRIENDVIEW));
            channel.writeAndFlush(MessageUtil.turnToPacket("", PacketType.FRIENDMSG));
            return;
        }
        if (msg.equals("ls")) {
            queryApplyUserInfo(channel);
            return;
        }
        if (msg.startsWith("sq-")) {
            applyFriendToOther(channel, msg);
            return;
        }
        if (msg.equals("lu")) {
            queryFriendToSelf(channel, msg);
            return;
        }
        if (msg.startsWith("ty=")) {
            agreeApplyInfo(channel, msg);
            return;
        }
    }

    private void agreeApplyInfo(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
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
        User userTarget = getUserByName(friendapplyinfo.getFromuser());
        if (userTarget != null) {
            Channel channelTarget = NettyMemory.userToChannelMap.get(userTarget);
            channelTarget.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "同意了你的好友申请，你们现在是好友啦"));
        }
        return;
    }

    private void queryFriendToSelf(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
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
            resp += "[" + friendinfo.getFriendname() + "] ";
        }
    }

    private void applyFriendToOther(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
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

    private void queryApplyUserInfo(Channel channel) {
        User user = NettyMemory.session2UserIds.get(channel);
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

    private User getUserByName(String s) {
        for (Map.Entry<Channel, User> entry : NettyMemory.session2UserIds.entrySet()) {
            if (entry.getValue().getUsername().equals(s)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
