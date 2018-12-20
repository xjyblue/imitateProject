package event;

import achievement.AchievementExecutor;
import caculation.MoneyCaculation;
import caculation.UserbagCaculation;
import component.parent.Good;
import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.*;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.*;
import utils.MessageUtil;
import utils.UserbagUtil;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/6 16:30
 */
@Component("labourUnionEvents")
public class LabourUnionEvent {
    @Autowired
    private UnioninfoMapper unioninfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ApplyunioninfoMapper applyunioninfoMapper;
    @Autowired
    private UserbagMapper userbagMapper;
    @Autowired
    private CommonEvent commonEvent;
    @Autowired
    private UnionwarehouseMapper unionwarehouseMapper;
    @Autowired
    private UserbagCaculation userbagCaculation;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private MoneyCaculation moneyCaculation;

    private Lock lock = new ReentrantLock();

    public void solve(Channel channel, String msg) {
        if (msg.startsWith("b") || msg.startsWith("w") || msg.startsWith("fix-")) {
            commonEvent.common(channel, msg);
            return;
        }
        if (msg.equals("q")) {
            outUnionView(channel, msg);
            return;
        }
        if (msg.equals("g")) {
            enterUnionView(channel, msg);
            return;
        }
        if (msg.equals("lu")) {
            queryUnion(channel, msg);
            return;
        }
        if (msg.startsWith("cu")) {
            createUnion(channel, msg);
            return;
        }
        if (msg.equals("tc")) {
            outUnion(channel, msg);
            return;
        }
        if (msg.startsWith("sq")) {
            applyUnion(channel, msg);
            return;
        }
        if (msg.startsWith("ls=y")) {
            agreeApplyInfo(channel, msg);
            return;
        }
        if (msg.startsWith("ls=n")) {
            disagreeApplyInfo(channel, msg);
            return;
        }
        if (msg.equals("ls")) {
            queryApplyInfo(channel, msg);
            return;
        }
        if (msg.equals("zsry")) {
            queryUnionMemberInfo(channel, msg);
            return;
        }
        if (msg.startsWith("sj")) {
            memberLevelChange(channel, msg);
            return;
        }
        if (msg.startsWith("t=")) {
            removeMember(channel, msg);
            return;
        }
        if (msg.equals("zsck")) {
            showWarehouse(channel, msg);
            return;
        }
        if (msg.startsWith("jxjb")) {
            giveMoneyToUnion(channel, msg);
            return;
        }
        if (msg.startsWith("jx")) {
            giveUserbagToUnion(channel, msg);
            return;
        }
        if (msg.startsWith("hq")) {
            getUserbagFromUnion(channel, msg);
        }
    }

    private void giveMoneyToUnion(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (!checkUserHasEnoughMoney(user, temp[1])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHMONEYTOGIVE));
            return;
        }
        moneyCaculation.removeMoneyToUser(user, temp[1]);
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        unioninfo.setUnionmoney(unioninfo.getUnionmoney() + Integer.parseInt(temp[1]));
        unioninfoMapper.updateByPrimaryKey(unioninfo);
        messageToAllInUnion(user.getUnionid(), user.getUsername() + "向工会捐献了" + temp[1] + "个金币");
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGIVEMONEYTOUNION, PacketType.UNIONINFO));
    }

    private boolean checkUserHasEnoughMoney(User user, String money) {
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger jxMoney = new BigInteger(money);
        if (userMoney.compareTo(jxMoney) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private void getUserbagFromUnion(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("=");
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() > 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }

        try {
            lock.lock();
//      拿到工会格子
            Userbag userbag = userbagMapper.selectByPrimaryKey(temp[1]);
            if (userbag.getNum() < Integer.parseInt(temp[2])) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORUSERBAGNUM));
                return;
            }


            if (userbag.getTypeof().equals(Good.EQUIPMENT)) {
//              关联处理
                UnionwarehouseExample unionwarehouseExample = new UnionwarehouseExample();
                UnionwarehouseExample.Criteria criteria = unionwarehouseExample.createCriteria();
                criteria.andUserbagidEqualTo(temp[1]);
                unionwarehouseMapper.deleteByExample(unionwarehouseExample);

                userbagCaculation.addUserBagForUser(user, userbag);

                String resp = "用户：" + user.getUsername() + "向工会仓库拿取了" + Good.getGoodNameByUserbag(userbag);
                messageToAllInUnion(user.getUnionid(), resp);
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGETUNIONGOOD, PacketType.UNIONINFO));
                return;
            }


//          工会格子处理，与用户格子处理有区别
            userbag.setNum(userbag.getNum() - Integer.parseInt(temp[2]));
            if (userbag.getNum() == 0) {
                UnionwarehouseExample unionwarehouseExample = new UnionwarehouseExample();
                UnionwarehouseExample.Criteria criteria = unionwarehouseExample.createCriteria();
                criteria.andUserbagidEqualTo(userbag.getId());
//                  为0移除关联和背包格子
                unionwarehouseMapper.deleteByExample(unionwarehouseExample);
                userbagMapper.deleteByPrimaryKey(userbag.getId());
            } else {
                userbagMapper.updateByPrimaryKey(userbag);
            }

//          新增格子，具体处理细节到里面去实现
            Userbag userbagNew = new Userbag();
            userbagNew.setName(user.getUsername());
            userbagNew.setTypeof(userbag.getTypeof());
            userbagNew.setNum(Integer.parseInt(temp[2]));
            userbagNew.setWid(userbag.getWid());
            userbagNew.setDurability(userbag.getDurability());
            userbagNew.setId(UUID.randomUUID().toString());
            userbagCaculation.addUserBagForUser(user, userbagNew);

            String resp = "用户：" + user.getUsername() + "向工会仓库拿取了" + Good.getGoodNameByUserbag(userbagNew);
            messageToAllInUnion(user.getUnionid(), resp);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGETUNIONGOOD, PacketType.UNIONINFO));
            return;

        } finally {
            lock.unlock();
        }


    }

    private void giveUserbagToUnion(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("=");
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        Userbag userbag = UserbagUtil.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
            return;
        }
        if (Integer.parseInt(temp[2]) > userbag.getNum()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORUSERBAGNUM));
            return;
        }

//      处理捐赠逻辑
//      先处理用户背包的这一块
        if (!userbag.getTypeof().equals(Good.EQUIPMENT)) {
            userbag.setNum(userbag.getNum() - Integer.parseInt(temp[2]));
            if (userbag.getNum() == 0) {
                user.getUserBag().remove(userbag);
                userbag.setName(null);
                userbagMapper.deleteByPrimaryKey(userbag.getId());
            }
        } else {
            user.getUserBag().remove(userbag);
        }
//      同步数据库
        userbagMapper.updateByPrimaryKey(userbag);

//      处理工会仓库这一块
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        List<Userbag> list = userbagMapper.selectUserbagByWarehourseId(unioninfo.getUnionwarehourseid());
        if (userbag.getTypeof().equals(Good.EQUIPMENT)) {
            userbag.setName(null);
            Unionwarehouse unionwarehouse = new Unionwarehouse();
            unionwarehouse.setUserbagid(userbag.getId());
            unionwarehouse.setUnionwarehouseid(unioninfo.getUnionwarehourseid());
            unionwarehouseMapper.insert(unionwarehouse);
            userbagMapper.updateByPrimaryKey(userbag);

            String resp = "用户：" + user.getUsername() + "向工会捐献了" + Good.getGoodNameByUserbag(userbag);
            messageToAllInUnion(user.getUnionid(), resp);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGIVEGOODTOUNION, PacketType.UNIONINFO));
            return;
        }

        Userbag userbagNew = new Userbag();
        userbagNew.setId(UUID.randomUUID().toString());
        userbagNew.setWid(userbag.getWid());
        userbagNew.setNum(Integer.parseInt(temp[2]));
        userbagNew.setTypeof(userbag.getTypeof());
        boolean flag = false;
        for (Userbag userbagTemp : list) {
            if (userbagTemp.getWid().equals(userbag.getWid())) {
                userbagTemp.setNum(userbagTemp.getNum() + Integer.parseInt(temp[2]));
                userbagMapper.updateByPrimaryKeySelective(userbagTemp);
                userbagNew.setId(userbagTemp.getId());
                flag = true;
                break;
            }
        }
        if (!flag) {
            userbagNew.setId(UUID.randomUUID().toString());
            Unionwarehouse unionwarehouse = new Unionwarehouse();
            unionwarehouse.setUserbagid(userbagNew.getId());
            unionwarehouse.setUnionwarehouseid(unioninfo.getUnionwarehourseid());
            unionwarehouseMapper.insert(unionwarehouse);
            userbagMapper.insertSelective(userbagNew);
        }


//      广播
        String resp = "用户：" + user.getUsername() + "向工会捐献了" + Good.getGoodNameByUserbag(userbagNew);
        messageToAllInUnion(user.getUnionid(), resp);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGIVEGOODTOUNION, PacketType.UNIONINFO));
        return;
    }

    private void showWarehouse(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        List<Userbag> list = userbagMapper.selectUserbagByWarehourseId(unioninfo.getUnionwarehourseid());
        String resp = "";
        for (Userbag userbag : list) {
            resp += Good.getGoodNameByUserbag(userbag) + System.getProperty("line.separator");
        }
        resp += "工会仓库金币数量为：" + unioninfo.getUnionmoney() + System.getProperty("line.separator");
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + resp, PacketType.UNIONINFO));
        return;
    }

    private void removeMember(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("=");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User userTarget = userMapper.selectByPrimaryKey(temp[1]);
        if (userTarget == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.NOUSER, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() >= userTarget.getUnionlevel()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }
        userTarget.setUnionlevel(null);
        userTarget.setUnionid(null);
        userMapper.updateByPrimaryKey(userTarget);

//      同步用户会话信息
        User userSession = getUserFromSessionById(userTarget.getUsername());
        if (userSession != null) {
            userSession.setUnionid(null);
            userSession.setUnionlevel(null);
            Channel channelTemp = ProjectContext.userToChannelMap.get(userSession);
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("你被" + user.getUsername() + "T出了工会"));
        }

        String resp = user.getUsername() + "已踢出了工会中的" + userTarget.getUsername() + "玩家";
        messageToAllInUnion(user.getUnionid(), resp);
        return;
    }

    private void disagreeApplyInfo(Channel channel, String msg) {
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
//      不同意
        applyunioninfoMapper.deleteByPrimaryKey(applyunioninfo.getApplyid());
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.DISAGREEUSEAPPLY));
        return;
    }

    private void memberLevelChange(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ProjectContext.session2UserIds.get(channel);
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (user.getUnionlevel() > 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() >= Integer.parseInt(temp[2])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }
        User userTarget = userMapper.selectByPrimaryKey(temp[1]);
        userTarget.setUnionlevel(Integer.parseInt(temp[2]));
        userMapper.updateByPrimaryKeySelective(userTarget);

//      同步用户会话信息
        User userSession = getUserFromSessionById(userTarget.getUsername());
        if (userSession != null) {
            userSession.setUnionlevel(Integer.parseInt(temp[2]));
        }
//      广播升级信息
        String msgToAll = "恭喜玩家" + userTarget.getUsername() + "被" + user.getUsername() + "升为[ " + temp[2] + " ]级";
        messageToAllInUnion(user.getUnionid(), msgToAll);
        return;
    }

    private void queryUnionMemberInfo(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        List<User> users = userMapper.selectByUnionId(unioninfo.getUnionid());
        String resp = "";
        for (User userTemp : users) {
            resp += "用户名 [ " + userTemp.getUsername() + " ] 用户工会等级 [ " + userTemp.getUnionlevel() + " ] " + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + resp, PacketType.UNIONINFO));
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

//      同步用户会话信息
        User userSession = getUserFromSessionById(userTarget.getUsername());
        if (userSession != null) {
            userSession.setUnionid(applyunioninfo.getUnionid());
            userSession.setUnionlevel(4);
        }

        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + "您同意了" + userTarget.getUsername() + "加入本工会", PacketType.UNIONINFO));

//      处理第一次加入工会的事件
        achievementExecutor.executeAddUnionFirst(userSession, userTarget.getUsername());

        String msgToAll = "欢迎" + userTarget.getUsername() + "加入了本公会";
        messageToAllInUnion(applyunioninfo.getUnionid(), msgToAll);
//      移除申请记录
        applyunioninfoMapper.deleteByPrimaryKey(applyunioninfo.getApplyid());
        return;
    }

    private void queryApplyInfo(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() > 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
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
        User user = ProjectContext.session2UserIds.get(channel);
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
        User user = ProjectContext.session2UserIds.get(channel);
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
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() != null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOCREATEUNION));
            return;
        }
        Unioninfo unioninfo = new Unioninfo();
        String unioninfoId = UUID.randomUUID().toString();
        unioninfo.setUnionid(unioninfoId);
        unioninfo.setUnionname(temp[1]);
        unioninfo.setUnionmoney(0);
        unioninfo.setUnionwarehourseid(UUID.randomUUID().toString());
        User userTemp = ProjectContext.session2UserIds.get(channel);
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
        ProjectContext.eventStatus.put(channel, EventStatus.LABOURUNION);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERLABOURVIEW));
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG, PacketType.UNIONINFO));
        }
        return;
    }

    private void outUnionView(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, EventStatus.STOPAREA);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUTLABOURVIEW));
        channel.writeAndFlush(MessageUtil.turnToPacket("", PacketType.UNIONINFO));
        return;
    }

    private User getUserFromSessionById(String username) {
        for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
            if (entry.getValue().getUsername().equals(username)) {
                return entry.getValue();
            }
        }
        return null;
    }

    //  对在线的工会玩家广播一次内容
    private void messageToAllInUnion(String unionId, String msg) {
        for (Channel channelTemp : ProjectContext.group) {
            User userTemp = ProjectContext.session2UserIds.get(channelTemp);
            if (userTemp.getUnionid() != null && userTemp.getUnionid().equals(unionId)) {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
            }
        }
    }
}
