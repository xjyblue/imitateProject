package event;

import com.sun.org.apache.regexp.internal.RE;
import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.ApplyunioninfoMapper;
import mapper.UnioninfoMapper;
import mapper.UserMapper;
import memory.NettyMemory;
import netscape.security.UserTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketProto;
import packet.PacketType;
import pojo.*;
import utils.MessageUtil;

import java.util.List;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/6 16:30
 */
@Component("labourUnionEvents")
public class LabourUnionEvent {
    @Autowired
    private UnioninfoMapper unioninfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ApplyunioninfoMapper applyunioninfoMapper;

    public void slove(Channel channel, String msg) {
        if (msg.equals("q")) {
            outUnionView(channel, msg);
        }
        if (msg.equals("g")) {
            enterUnionView(channel, msg);
        }
        if (msg.equals("lu")) {
            queryUnion(channel, msg);
        }
        if (msg.startsWith("cu")) {
            createUnion(channel, msg);
        }
        if (msg.equals("tc")) {
            outUnion(channel, msg);
        }
        if (msg.startsWith("sq")) {
            applyUnion(channel, msg);
        }
        if (msg.startsWith("ls=y")) {
            agreeApplyInfo(channel, msg);
        }
        if (msg.equals("ls")) {
            queryApplyInfo(channel, msg);
        }
        if (msg.equals("zsry")) {
            queryUnionMemberInfo(channel, msg);
        }
        if(msg.startsWith("sj")){
            memberLevelChange(channel,msg);
        }
    }

    private void memberLevelChange(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = NettyMemory.session2UserIds.get(channel);
        if(temp.length!=3){
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if(user.getUnionlevel()>2){
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG+MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }
        if(user.getUnionlevel()>=Integer.parseInt(temp[2])){
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG+MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }
        User userTarget = userMapper.selectByPrimaryKey(temp[1]);
        userTarget.setUnionlevel(Integer.parseInt(temp[2]));
        userMapper.updateByPrimaryKeySelective(userTarget);
//      广播升级信息
        String msgToAll = "恭喜玩家" + userTarget.getUsername() + "被" + user.getUsername() + "升为[ " + temp[2] + " ]级";
        messageToAllInUnion(user.getUnionid(),msgToAll);
        return;
    }

    private void queryUnionMemberInfo(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        List<User> users = userMapper.selectByUnionId(unioninfo.getUnionid());
        String resp = "";
        for (User userTemp : users) {
            resp += "用户名 [ " + userTemp.getUsername() + " ] 用户工会等级 [ " +userTemp.getUnionlevel() + " ] " + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + resp,PacketType.UNIONINFO));
        return;
    }

    private void agreeApplyInfo(Channel channel, String msg) {
        String temp[] = msg.split("=");
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        Applyunioninfo applyunioninfo = applyunioninfoMapper.selectByPrimaryKey(temp[2]);
        if (applyunioninfo == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOAPPLYINFO));
            return;
        }

        User userTarget = userMapper.selectByPrimaryKey(applyunioninfo.getApplyuser());

        userTarget.setUnionid(applyunioninfo.getUnionid());
        userTarget.setUnionlevel(4);
        userMapper.updateByPrimaryKeySelective(userTarget);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + "您同意了" + userTarget.getUsername() + "加入本工会", PacketType.UNIONINFO));

        String msgToAll = "欢迎" + userTarget.getUsername() + "加入了本公会";
        messageToAllInUnion(applyunioninfo.getUnionid(),msgToAll);
        
//      移除申请记录
        applyunioninfoMapper.deleteByPrimaryKey(applyunioninfo.getApplyid());
        return;
    }

//  对在线的工会玩家广播一次内容
    private void messageToAllInUnion(String unionId,String msg) {
        for (Channel channelTemp : NettyMemory.group) {
            User userTemp = NettyMemory.session2UserIds.get(channelTemp);
            if (userTemp.getUnionid().equals(unionId)) {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
            }
        }
    }

    private void queryApplyInfo(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() > 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG+MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }
        List<Applyunioninfo> list = applyunioninfoMapper.selectByApplyinfoByUnionId(user.getUnionid());
        String resp = "";
        for (Applyunioninfo applyunioninfo : list) {
            resp += "申请编号 [ " + applyunioninfo.getApplyid() + "] 申请用户 [ " + applyunioninfo.getApplyuser() + "] " + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + resp, PacketType.UNIONINFO));
        return;
    }

    private void applyUnion(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
        String temp[] = msg.split("=");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (user.getUnionid() != null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOAPPLYUNION));
            return;
        }
//      申请加入不存在的工会
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(temp[1]);
        if (unioninfo == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEXISTUNIONID));
            return;
        }

        int count = applyunioninfoMapper.selectByUserIdAndUnionId(user.getUsername(), temp[1]);
        if (count > 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOREPEATUNIONAPPLY));
            return;
        }

//      创建入会申请记录
        Applyunioninfo applyunioninfo = new Applyunioninfo();
        applyunioninfo.setApplyid(UUID.randomUUID().toString());
        applyunioninfo.setApplyuser(user.getUsername());
        applyunioninfo.setUnionid(temp[1]);
        applyunioninfoMapper.insert(applyunioninfo);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSUNIONAPPLY, PacketType.UNIONINFO));
        return;
    }

    private void outUnion(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
        if (user.getUnionlevel() == 1) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOOUTUNION));
            return;
        }
        user.setUnionid(null);
        user.setUnionlevel(null);
        userMapper.updateByPrimaryKey(user);
        return;
    }

    private void createUnion(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = NettyMemory.session2UserIds.get(channel);
        if (user.getUnionid() != null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOCREATEUNION));
            return;
        }
        Unioninfo unioninfo = new Unioninfo();
        String unioninfoId = UUID.randomUUID().toString();
        unioninfo.setUnionid(unioninfoId);
        unioninfo.setUnionname(temp[1]);
        unioninfo.setUnionwarehourseid(UUID.randomUUID().toString());
        User userTemp = NettyMemory.session2UserIds.get(channel);
        userTemp.setUnionid(unioninfoId);


        userTemp.setUnionlevel(1);
        unioninfoMapper.insert(unioninfo);
        userMapper.updateByPrimaryKeySelective(userTemp);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + "你创建了" + temp[1] + "工会", PacketType.UNIONINFO));
        return;
    }

    private void queryUnion(Channel channel, String msg) {
        UnioninfoExample unioninfoExample = new UnioninfoExample();
        List<Unioninfo> list = unioninfoMapper.selectByExample(unioninfoExample);
        String resp = "";
        for (Unioninfo unioninfo : list) {
            resp += System.getProperty("line.separator") + "工会id [ " + unioninfo.getUnionid() + " ]" + "工会名称 [ " + unioninfo.getUnionname() + " ]";
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + resp, PacketType.UNIONINFO));
        return;
    }

    private void enterUnionView(Channel channel, String msg) {
        NettyMemory.eventStatus.put(channel, EventStatus.LABOURUNION);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERLABOURVIEW));
        User user = NettyMemory.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG, PacketType.UNIONINFO));
        }
        return;
    }

    private void outUnionView(Channel channel, String msg) {
        NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUTLABOURVIEW));
        channel.writeAndFlush(MessageUtil.turnToPacket("",PacketType.UNIONINFO));
        return;
    }
}
