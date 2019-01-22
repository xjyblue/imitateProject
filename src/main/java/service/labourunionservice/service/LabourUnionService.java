package service.labourunionservice.service;

import core.annotation.Region;
import core.config.GrobalConfig;
import core.packet.PacketType;
import core.packet.ServerPacket;
import service.achievementservice.service.AchievementService;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import core.component.good.parent.BaseGood;
import core.config.MessageConfig;
import service.userbagservice.service.UserbagService;
import service.userservice.service.UserService;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import mapper.*;
import core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.*;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName LabourUnionService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class LabourUnionService {
    @Autowired
    private UnioninfoMapper unioninfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ApplyunioninfoMapper applyunioninfoMapper;
    @Autowired
    private UserbagMapper userbagMapper;
    @Autowired
    private UnionwarehouseMapper unionwarehouseMapper;
    @Autowired
    private UserbagCaculationService userbagCaculationService;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private MoneyCaculationService moneyCaculationService;
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private UserService userService;

    private Lock lock = new ReentrantLock();

    /**
     * 捐献金币到工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "jxjb", status = {ChannelStatus.LABOURUNION})
    public void giveMoneyToUnion(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (!moneyCaculationService.checkUserHasEnoughMoney(user, temp[1])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOENOUGHMONEYTOGIVE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        moneyCaculationService.removeMoneyToUser(user, temp[1]);
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        unioninfo.setUnionmoney(unioninfo.getUnionmoney() + Integer.parseInt(temp[1]));
        unioninfoMapper.updateByPrimaryKey(unioninfo);
        messageToAllInUnion(user.getUnionid(), user.getUsername() + "向工会捐献了" + temp[1] + "个金币");
        ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
        builder.setData(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGIVEMONEYTOUNION);
        MessageUtil.sendMessage(channel, builder.build());
    }


    /**
     * 从工会获取物品到背包
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "hq", status = {ChannelStatus.LABOURUNION})
    public void getUserbagFromUnion(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (user.getUnionid() == null) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }
        if (user.getUnionlevel() > GrobalConfig.THREE) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }

        try {
            lock.lock();
//      拿到工会格子
            Userbag userbag = userbagMapper.selectByPrimaryKey(temp[1]);
            if (userbag.getNum() < Integer.parseInt(temp[GrobalConfig.TWO])) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.ERRORUSERBAGNUM);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }


            if (userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
//              关联处理
                UnionwarehouseExample unionwarehouseExample = new UnionwarehouseExample();
                UnionwarehouseExample.Criteria criteria = unionwarehouseExample.createCriteria();
                criteria.andUserbagidEqualTo(temp[1]);
                unionwarehouseMapper.deleteByExample(unionwarehouseExample);

                userbagCaculationService.addUserBagForUser(user, userbag);

                String resp = "用户：" + user.getUsername() + "向工会仓库拿取了" + userbagService.getGoodNameByUserbag(userbag);
                messageToAllInUnion(user.getUnionid(), resp);
                ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
                builder.setData(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGETUNIONGOOD);
                MessageUtil.sendMessage(channel,builder.build());
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
            userbagCaculationService.addUserBagForUser(user, userbagNew);

            String resp = "用户：" + user.getUsername() + "向工会仓库拿取了" + userbagService.getGoodNameByUserbag(userbagNew);
            messageToAllInUnion(user.getUnionid(), resp);
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGETUNIONGOOD);
            MessageUtil.sendMessage(channel,builder.build());
            return;

        } finally {
            lock.unlock();
        }


    }

    /**
     * 捐献物品到工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "jxwp", status = {ChannelStatus.LABOURUNION})
    public void giveUserbagToUnion(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (user.getUnionid() == null) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOUSERBAGID);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (Integer.parseInt(temp[GrobalConfig.TWO]) > userbag.getNum()) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORUSERBAGNUM);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

//      处理捐赠逻辑
//      先处理用户背包的这一块
        if (!userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
            userbag.setNum(userbag.getNum() - Integer.parseInt(temp[2]));
            if (userbag.getNum() == 0) {
                user.getUserBag().remove(userbag);
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
        if (userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
            userbag.setName(null);
            Unionwarehouse unionwarehouse = new Unionwarehouse();
            unionwarehouse.setUserbagid(userbag.getId());
            unionwarehouse.setUnionwarehouseid(unioninfo.getUnionwarehourseid());
            unionwarehouseMapper.insert(unionwarehouse);
            userbagMapper.updateByPrimaryKey(userbag);

            String resp = "用户：" + user.getUsername() + "向工会捐献了" + userbagService.getGoodNameByUserbag(userbag);
            messageToAllInUnion(user.getUnionid(), resp);
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGIVEGOODTOUNION);
            MessageUtil.sendMessage(channel,builder.build());
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
        String resp = "用户：" + user.getUsername() + "向工会捐献了" + userbagService.getGoodNameByUserbag(userbagNew);
        messageToAllInUnion(user.getUnionid(), resp);
        ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
        builder.setData(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGIVEGOODTOUNION);
        MessageUtil.sendMessage(channel,builder.build());
        return;
    }

    /**
     * 展示工会仓库
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "zsck", status = {ChannelStatus.LABOURUNION})
    public void showWarehouse(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (user.getUnionid() == null) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        List<Userbag> list = userbagMapper.selectUserbagByWarehourseId(unioninfo.getUnionwarehourseid());
        String resp = "";
        for (Userbag userbag : list) {
            resp += userbagService.getGoodNameByUserbag(userbag) + System.getProperty("line.separator");
        }
        resp += "工会仓库金币数量为：" + unioninfo.getUnionmoney() + System.getProperty("line.separator");
        ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
        builder.setData(MessageConfig.UNIONMSG + resp);
        MessageUtil.sendMessage(channel,builder.build());
        return;
    }

    @Order(orderMsg = "tg", status = {ChannelStatus.LABOURUNION})
    public void removeMember(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User userTarget = userMapper.selectByPrimaryKey(temp[1]);
        if (userTarget == null) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.NOUSER);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }
        if (user.getUnionlevel() >= userTarget.getUnionlevel()) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }
        userTarget.setUnionlevel(null);
        userTarget.setUnionid(null);
        userMapper.updateByPrimaryKey(userTarget);

//      同步用户会话信息
        User userSession = userService.getUserByNameFromSession(userTarget.getUsername());
        if (userSession != null) {
            userSession.setUnionid(null);
            userSession.setUnionlevel(null);
            Channel channelTemp = ChannelUtil.userToChannelMap.get(userSession);

            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("你被" + user.getUsername() + "T出了工会");
            MessageUtil.sendMessage(channel, builder.build());
        }

        String resp = user.getUsername() + "已踢出了工会中的" + userTarget.getUsername() + "玩家";
        messageToAllInUnion(user.getUnionid(), resp);
        return;
    }

    /**
     * 不同意玩家加入工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "gn", status = {ChannelStatus.LABOURUNION})
    public void disagreeApplyInfo(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Applyunioninfo applyunioninfo = applyunioninfoMapper.selectByPrimaryKey(temp[1]);
        if (applyunioninfo == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOAPPLYINFO);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      不同意
        applyunioninfoMapper.deleteByPrimaryKey(applyunioninfo.getApplyid());
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.DISAGREEUSEAPPLY);
        MessageUtil.sendMessage(channel, builder.build());
        return;
    }

    /**
     * 修改工会玩家等级
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "sjg", status = {ChannelStatus.LABOURUNION})
    public void memberLevelChange(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (user.getUnionlevel() > GrobalConfig.TWO) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }
        if (user.getUnionlevel() >= Integer.parseInt(temp[GrobalConfig.TWO])) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }
        User userTarget = userMapper.selectByPrimaryKey(temp[1]);
        userTarget.setUnionlevel(Integer.parseInt(temp[2]));
        userMapper.updateByPrimaryKeySelective(userTarget);

//      同步用户会话信息
        User userSession = userService.getUserByNameFromSession(userTarget.getUsername());
        if (userSession != null) {
            userSession.setUnionlevel(Integer.parseInt(temp[2]));
        }
//      广播升级信息
        String msgToAll = "恭喜玩家" + userTarget.getUsername() + "被" + user.getUsername() + "升为[ " + temp[2] + " ]级";
        messageToAllInUnion(user.getUnionid(), msgToAll);
        return;
    }

    /**
     * 展示工会人员信息
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "zsry", status = {ChannelStatus.LABOURUNION})
    public void queryUnionMemberInfo(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (user.getUnionid() == null) {
            ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
            builder.setData(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON);
            MessageUtil.sendMessage(channel,builder.build());
            return;
        }
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        List<User> users = userMapper.selectByUnionId(unioninfo.getUnionid());
        String resp = "";
        for (User userTemp : users) {
            resp += "用户名 [ " + userTemp.getUsername() + " ] 用户工会等级 [ " + userTemp.getUnionlevel() + " ] " + System.getProperty("line.separator");
        }
        ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
        builder.setData(MessageConfig.UNIONMSG + resp);
        MessageUtil.sendMessage(channel,builder.build());
        return;
    }

    /**
     * 同意玩家加入工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "gy", status = {ChannelStatus.LABOURUNION})
    public void agreeApplyInfo(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Applyunioninfo applyunioninfo = applyunioninfoMapper.selectByPrimaryKey(temp[1]);
        if (applyunioninfo == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOAPPLYINFO);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        User userTarget = userMapper.selectByPrimaryKey(applyunioninfo.getApplyuser());

        userTarget.setUnionid(applyunioninfo.getUnionid());
        userTarget.setUnionlevel(4);
        userMapper.updateByPrimaryKeySelective(userTarget);

//      同步用户会话信息
        User userSession = userService.getUserByNameFromSession(userTarget.getUsername());
        if (userSession != null) {
            userSession.setUnionid(applyunioninfo.getUnionid());
            userSession.setUnionlevel(4);
        }
        ServerPacket.UnionResp.Builder builder = ServerPacket.UnionResp.newBuilder();
        builder.setData(MessageConfig.UNIONMSG + "您同意了" + userTarget.getUsername() + "加入本工会");
        MessageUtil.sendMessage(channel,builder.build());

//      处理第一次加入工会的事件
        achievementService.executeAddUnionFirst(userSession, userTarget.getUsername());

        String msgToAll = "欢迎" + userTarget.getUsername() + "加入了本公会";
        messageToAllInUnion(applyunioninfo.getUnionid(), msgToAll);
//      移除申请记录
        applyunioninfoMapper.deleteByPrimaryKey(applyunioninfo.getApplyid());
        return;
    }

    /**
     * 查看所有申请加入工会的玩家
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "lsg", status = {ChannelStatus.LABOURUNION})
    public void queryApplyInfo(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        ServerPacket.UnionResp.Builder builder1 = ServerPacket.UnionResp.newBuilder();
        if (user.getUnionid() == null) {
            builder1.setData(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON);
            MessageUtil.sendMessage(channel, builder1.build());
            return;
        }
        if (user.getUnionlevel() > GrobalConfig.TWO) {
            builder1.setData(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE);
            MessageUtil.sendMessage(channel, builder1.build());
            return;
        }
        List<Applyunioninfo> list = applyunioninfoMapper.selectByApplyinfoByUnionId(user.getUnionid());
        String resp = "";
        for (Applyunioninfo applyunioninfo : list) {
            resp += "申请编号 [ " + applyunioninfo.getApplyid() + "] 申请用户 [ " + applyunioninfo.getApplyuser() + "] " + System.getProperty("line.separator");
        }
        builder1.setData(MessageConfig.UNIONMSG + resp);
        MessageUtil.sendMessage(channel, builder1.build());
        return;
    }

    /**
     * 申请加入工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "sqg", status = {ChannelStatus.LABOURUNION})
    public void applyUnion(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (user.getUnionid() != null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOAPPLYUNION);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      申请加入不存在的工会
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(temp[1]);
        if (unioninfo == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOEXISTUNIONID);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        int count = applyunioninfoMapper.selectByUserIdAndUnionId(user.getUsername(), temp[1]);
        if (count > 0) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOREPEATUNIONAPPLY);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

//      创建入会申请记录
        Applyunioninfo applyunioninfo = new Applyunioninfo();
        applyunioninfo.setApplyid(UUID.randomUUID().toString());
        applyunioninfo.setApplyuser(user.getUsername());
        applyunioninfo.setUnionid(temp[1]);
        applyunioninfoMapper.insert(applyunioninfo);

        ServerPacket.UnionResp.Builder builder1 = ServerPacket.UnionResp.newBuilder();
        builder1.setData(MessageConfig.UNIONMSG + MessageConfig.SUCCESSUNIONAPPLY);
        MessageUtil.sendMessage(channel, builder1.build());
        return;
    }

    /**
     * 退出工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "backg", status = {ChannelStatus.LABOURUNION})
    public void outUnion(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (user.getUnionid() == null) {
            ServerPacket.UnionResp.Builder builder1 = ServerPacket.UnionResp.newBuilder();
            builder1.setData(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON);
            MessageUtil.sendMessage(channel, builder1.build());
            return;
        }
        if (user.getUnionlevel() == 1) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOOUTUNION);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        user.setUnionid(null);
        user.setUnionlevel(null);
        userMapper.updateByPrimaryKey(user);
        return;
    }

    /**
     * 创建工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "cgu", status = {ChannelStatus.LABOURUNION})
    public void createUnion(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (user.getUnionid() != null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOCREATEUNION);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Unioninfo unioninfo = new Unioninfo();
        String unioninfoId = UUID.randomUUID().toString();
        unioninfo.setUnionid(unioninfoId);
        unioninfo.setUnionname(temp[1]);
        unioninfo.setUnionmoney(0);
        unioninfo.setUnionwarehourseid(UUID.randomUUID().toString());
        User userTemp = ChannelUtil.channelToUserMap.get(channel);
        userTemp.setUnionid(unioninfoId);


        userTemp.setUnionlevel(1);
        unioninfoMapper.insert(unioninfo);
        userMapper.updateByPrimaryKeySelective(userTemp);
        ServerPacket.UnionResp.Builder builder1 = ServerPacket.UnionResp.newBuilder();
        builder1.setData(MessageConfig.UNIONMSG + "你创建了" + temp[1] + "工会");
        MessageUtil.sendMessage(channel, builder1.build());
        return;
    }

    /**
     * 查看可加入的工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "lgu", status = {ChannelStatus.LABOURUNION})
    public void queryUnion(Channel channel, String msg) {
        UnioninfoExample unioninfoExample = new UnioninfoExample();
        List<Unioninfo> list = unioninfoMapper.selectByExample(unioninfoExample);
        String resp = "";
        for (Unioninfo unioninfo : list) {
            resp += System.getProperty("line.separator") + "工会id [ " + unioninfo.getUnionid() + " ]" + "工会名称 [ " + unioninfo.getUnionname() + " ]";
        }
        ServerPacket.UnionResp.Builder builder1 = ServerPacket.UnionResp.newBuilder();
        builder1.setData(MessageConfig.UNIONMSG + resp);
        MessageUtil.sendMessage(channel, builder1.build());
        return;
    }

    /**
     * 进入工会管理界面
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "eg", status = {ChannelStatus.COMMONSCENE})
    public void enterUnionView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.LABOURUNION);

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.ENTERLABOURVIEW);
        MessageUtil.sendMessage(channel, builder.build());

        User user = ChannelUtil.channelToUserMap.get(channel);
        ServerPacket.UnionResp.Builder builder1 = ServerPacket.UnionResp.newBuilder();
        if (user.getUnionid() == null) {
            builder1.setData(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON);
            MessageUtil.sendMessage(channel, builder1.build());
        } else {
            builder1.setData(MessageConfig.UNIONMSG);
            MessageUtil.sendMessage(channel, builder1.build());
        }
        return;
    }

    /**
     * 退出工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qtg", status = {ChannelStatus.LABOURUNION})
    public void outUnionView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(msg);
        MessageUtil.sendMessage(channel, builder.build());

        ServerPacket.UnionResp.Builder builder1 = ServerPacket.UnionResp.newBuilder();
        builder1.setData("");
        MessageUtil.sendMessage(channel, builder1.build());
        return;
    }


    /**
     * 对在线的工会玩家进行一次广播
     *
     * @param unionId
     * @param msg
     */
    private void messageToAllInUnion(String unionId, String msg) {
        for (Map.Entry<Channel, User> entry : ChannelUtil.channelToUserMap.entrySet()) {
            User userTemp = entry.getValue();
            if (userTemp.getUnionid() != null && userTemp.getUnionid().equals(unionId)) {
                Channel channelTemp = entry.getKey();
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(msg);
                MessageUtil.sendMessage(channelTemp, builder.build());
            }
        }
    }
}
