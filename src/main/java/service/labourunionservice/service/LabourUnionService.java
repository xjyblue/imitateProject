package service.labourunionservice.service;

import core.config.GrobalConfig;
import service.achievementservice.service.AchievementService;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import core.component.good.parent.BaseGood;
import core.config.MessageConfig;
import service.userbagservice.service.UserbagService;
import service.userservice.service.UserService;
import service.weaponservice.service.Weaponservice;
import core.ChannelStatus;
import io.netty.channel.Channel;
import mapper.*;
import core.context.ProjectContext;
import core.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import core.packet.PacketType;
import pojo.*;
import utils.MessageUtil;

import java.math.BigInteger;
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
    private Weaponservice weaponservice;
    @Autowired
    private UserService userService;

    private Lock lock = new ReentrantLock();

    /**
     * 展示武器栏
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qw")
    public void showUserWeapon(Channel channel, String msg) {
        weaponservice.queryEquipmentBar(channel, msg);
    }

    /**
     * 修复武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "fix")
    public void fixWeapon(Channel channel, String msg) {
        weaponservice.fixEquipment(channel, msg);
    }

    /**
     * 卸下武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "wq")
    public void takeOffWeapon(Channel channel, String msg) {
        weaponservice.quitEquipment(channel, msg);
    }

    /**
     * 装备武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ww")
    public void takeInWeapon(Channel channel, String msg) {
        weaponservice.takeEquipment(channel, msg);
    }

    /**
     * 展示背包
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qb")
    public void showUserbag(Channel channel, String msg) {
        userbagService.refreshUserbagInfo(channel, msg);
    }

    /**
     * 使用背包物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ub-")
    public void useUserbag(Channel channel, String msg) {
        userbagService.useUserbag(channel, msg);
    }

    /**
     * 捐献金币到工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "jxjb")
    public void giveMoneyToUnion(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("-");
        if (temp.length != GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (!moneyCaculationService.checkUserHasEnoughMoney(user, temp[1])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHMONEYTOGIVE));
            return;
        }
        moneyCaculationService.removeMoneyToUser(user, temp[1]);
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        unioninfo.setUnionmoney(unioninfo.getUnionmoney() + Integer.parseInt(temp[1]));
        unioninfoMapper.updateByPrimaryKey(unioninfo);
        messageToAllInUnion(user.getUnionid(), user.getUsername() + "向工会捐献了" + temp[1] + "个金币");
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGIVEMONEYTOUNION, PacketType.UNIONINFO));
    }


    /**
     * 从工会获取物品到背包
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "hq")
    public void getUserbagFromUnion(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() > GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }

        try {
            lock.lock();
//      拿到工会格子
            Userbag userbag = userbagMapper.selectByPrimaryKey(temp[1]);
            if (userbag.getNum() < Integer.parseInt(temp[GrobalConfig.TWO])) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORUSERBAGNUM));
                return;
            }


            if (userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
//              关联处理
                UnionwarehouseExample unionwarehouseExample = new UnionwarehouseExample();
                UnionwarehouseExample.Criteria criteria = unionwarehouseExample.createCriteria();
                criteria.andUserbagidEqualTo(temp[1]);
                unionwarehouseMapper.deleteByExample(unionwarehouseExample);

                userbagCaculationService.addUserBagForUser(user, userbag);

                String resp = "用户：" + user.getUsername() + "向工会仓库拿取了" + BaseGood.getGoodNameByUserbag(userbag);
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
            userbagCaculationService.addUserBagForUser(user, userbagNew);

            String resp = "用户：" + user.getUsername() + "向工会仓库拿取了" + BaseGood.getGoodNameByUserbag(userbagNew);
            messageToAllInUnion(user.getUnionid(), resp);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGETUNIONGOOD, PacketType.UNIONINFO));
            return;

        } finally {
            lock.unlock();
        }


    }

    /**
     * 局限物品到工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "jxwp")
    public void giveUserbagToUnion(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
            return;
        }
        if (Integer.parseInt(temp[GrobalConfig.TWO]) > userbag.getNum()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORUSERBAGNUM));
            return;
        }

//      处理捐赠逻辑
//      先处理用户背包的这一块
        if (!userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
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
        if (userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
            userbag.setName(null);
            Unionwarehouse unionwarehouse = new Unionwarehouse();
            unionwarehouse.setUserbagid(userbag.getId());
            unionwarehouse.setUnionwarehouseid(unioninfo.getUnionwarehourseid());
            unionwarehouseMapper.insert(unionwarehouse);
            userbagMapper.updateByPrimaryKey(userbag);

            String resp = "用户：" + user.getUsername() + "向工会捐献了" + BaseGood.getGoodNameByUserbag(userbag);
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
        String resp = "用户：" + user.getUsername() + "向工会捐献了" + BaseGood.getGoodNameByUserbag(userbagNew);
        messageToAllInUnion(user.getUnionid(), resp);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.SUCCESSGIVEGOODTOUNION, PacketType.UNIONINFO));
        return;
    }

    /**
     * 展示工会仓库
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "zsck")
    public void showWarehouse(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        Unioninfo unioninfo = unioninfoMapper.selectByPrimaryKey(user.getUnionid());
        List<Userbag> list = userbagMapper.selectUserbagByWarehourseId(unioninfo.getUnionwarehourseid());
        String resp = "";
        for (Userbag userbag : list) {
            resp += BaseGood.getGoodNameByUserbag(userbag) + System.getProperty("line.separator");
        }
        resp += "工会仓库金币数量为：" + unioninfo.getUnionmoney() + System.getProperty("line.separator");
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + resp, PacketType.UNIONINFO));
        return;
    }

    @Order(orderMsg = "t=")
    public void removeMember(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
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
        User userSession = userService.getUserByNameFromSession(userTarget.getUsername());
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

    /**
     * 不同意玩家加入工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ls=n")
    public void disagreeApplyInfo(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
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

    /**
     * 修改工会玩家等级
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "sj")
    public void memberLevelChange(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ProjectContext.session2UserIds.get(channel);
        if (temp.length != GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (user.getUnionlevel() > GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() >= Integer.parseInt(temp[GrobalConfig.TWO])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.FOURZEROTHREE, PacketType.UNIONINFO));
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
    @Order(orderMsg = "zsry")
    public void queryUnionMemberInfo(Channel channel, String msg) {
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

    /**
     * 同意玩家加入工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ls=y")
    public void agreeApplyInfo(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
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
        User userSession = userService.getUserByNameFromSession(userTarget.getUsername());
        if (userSession != null) {
            userSession.setUnionid(applyunioninfo.getUnionid());
            userSession.setUnionlevel(4);
        }

        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + "您同意了" + userTarget.getUsername() + "加入本工会", PacketType.UNIONINFO));

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
    @Order(orderMsg = "lsu")
    public void queryApplyInfo(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() > GrobalConfig.TWO) {
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

    /**
     * 申请加入工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "sq")
    public void applyUnion(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
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

    /**
     * 退出工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "tc")
    public void outUnion(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
            return;
        }
        if (user.getUnionlevel() == 1) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOOUTUNION));
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
    @Order(orderMsg = "cu")
    public void createUnion(Channel channel, String msg) {
        String[] temp = msg.split("-");
        if (temp.length != GrobalConfig.TWO) {
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

    /**
     * 查看可加入的工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "lu")
    public void queryUnion(Channel channel, String msg) {
        UnioninfoExample unioninfoExample = new UnioninfoExample();
        List<Unioninfo> list = unioninfoMapper.selectByExample(unioninfoExample);
        String resp = "";
        for (Unioninfo unioninfo : list) {
            resp += System.getProperty("line.separator") + "工会id [ " + unioninfo.getUnionid() + " ]" + "工会名称 [ " + unioninfo.getUnionname() + " ]";
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + resp, PacketType.UNIONINFO));
        return;
    }

    /**
     * 进入工会管理界面
     *
     * @param channel
     * @param msg
     */
    public void enterUnionView(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, ChannelStatus.LABOURUNION);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERLABOURVIEW));
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getUnionid() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG + MessageConfig.YOUARENOUNON, PacketType.UNIONINFO));
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNIONMSG, PacketType.UNIONINFO));
        }
        return;
    }

    /**
     * 退出工会
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qt")
    public void outUnionView(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, ChannelStatus.COMMONSCENE);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUTLABOURVIEW));
        channel.writeAndFlush(MessageUtil.turnToPacket("", PacketType.UNIONINFO));
        return;
    }


    /**
     * 对在线的工会玩家进行一次广播
     *
     * @param unionId
     * @param msg
     */
    private void messageToAllInUnion(String unionId, String msg) {
        for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
            User userTemp = entry.getValue();
            if (userTemp.getUnionid() != null && userTemp.getUnionid().equals(unionId)) {
                Channel channelTemp = entry.getKey();
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
            }
        }
    }
}
