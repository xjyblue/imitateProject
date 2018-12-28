package event;

import achievement.AchievementExecutor;
import caculation.MoneyCaculation;
import caculation.UserbagCaculation;
import component.Equipment;
import component.MpMedicine;
import component.parent.Good;
import config.MessageConfig;
import context.ProjectUtil;
import io.netty.channel.Channel;
import context.ProjectContext;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.User;
import pojo.Userbag;
import trade.Trade;
import utils.MessageUtil;
import utils.UserUtil;
import utils.UserbagUtil;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/3 10:14
 */
@Component("transactionEvent")
public class TransactionEvent {

    private Lock lock = new ReentrantLock();
    private Lock agreelock = new ReentrantLock();

    @Autowired
    private CommonEvent commonEvent;
    @Autowired
    private UserbagCaculation userbagCaculation;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private MoneyCaculation moneyCaculation;

    @Order(orderMsg = "ntrade")
    public void cancelTrade(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getTraceId() == null || !ProjectContext.tradeMap.containsKey(user.getTraceId())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOCREATETRADE));
            return;
        }
        Trade trade = ProjectContext.tradeMap.get(user.getTraceId());

//      加锁处理
        lock.lock();
        try {
//          解决同意线程先抢到锁
            if (trade.isIfexe()) {
                return;
            } else {
//              解决取消线程先抢到锁
                ProjectContext.tradeMap.remove(user.getTraceId());
                user.setTraceId(null);
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.CANCELTRADE));
                return;
            }
        } finally {
            lock.unlock();
        }
    }

    @Order(orderMsg = "ytrade")
    public void agreeTrade(Channel channel, String msg) {
        String temp[] = msg.split("=");
        User user = ProjectContext.session2UserIds.get(channel);
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (!ProjectContext.tradeMap.containsKey(temp[1])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTRADERECORD));
            return;
        }
//      建立起交易，把两者的转态弄到交易状态中去，多个线程抢锁
        User userStart = ProjectContext.tradeMap.get(temp[1]).getUserStart();
        Trade trade = ProjectContext.tradeMap.get(temp[1]);
        lock.lock();
        try {
//          解决取消线程先抢到锁
            if (!ProjectContext.tradeMap.containsKey(trade.getTradeId())) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.CANCELTRADE));
                return;
            }
//          解决同意线程先抢到锁
//          解决针对同一用户的同意交易和被同意的抢锁问题
            if (userStart.isIfTrade() || user.isIfTrade()) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.TRADEING));
                return;
            }
            userStart.setIfTrade(true);
            user.setIfTrade(true);
            trade.setIfexe(true);
        } finally {
            lock.unlock();
        }
//      把用户和另外一个用户设置成交易状态
//      处理建立交易的逻辑
        user.setTraceId(userStart.getTraceId());
        ProjectContext.eventStatus.put(channel, EventStatus.TRADE);
        Channel channelStart = ProjectContext.userToChannelMap.get(userStart);
        ProjectContext.eventStatus.put(channelStart, EventStatus.TRADE);
        trade.setUserTo(user);
        trade.setEndTime(System.currentTimeMillis() + 500000);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSCREATETRADE));
        channelStart.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSCREATETRADE));

        channel.writeAndFlush(MessageUtil.turnToPacket("欢迎来到交易界面，现在您与" + userStart.getUsername() + "建立交易，jbjy=金币金额进行金币交易，jyg=交易格子号可以把交易物品填充到交易栏，jy=y确认交易物品，双方都确认后交易完成", PacketType.TRADEMSG));
        channelStart.writeAndFlush(MessageUtil.turnToPacket("欢迎来到交易界面，现在您与" + user.getUsername() + "建立交易，jbjy=金币金额进行金币交易，jyg=交易格子号可以把交易物品填充到交易栏，jy=y确认交易物品，双方都确认后交易完成", PacketType.TRADEMSG));
    }

    @Order(orderMsg = "iftrade")
    public void createTrade(Channel channel, String msg) {
        String temp[] = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        Channel channelTarget = ProjectContext.userToChannelMap.get(UserUtil.getUserByName(temp[1]));
        if (channelTarget == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTRACEUSER));
            return;
        }
        User userTarget = ProjectContext.session2UserIds.get(channelTarget);
        if (user.getTraceId() != null && ProjectContext.tradeMap.containsKey(user.getTraceId())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.MANISINGTRADING));
            return;
        }
        try {
            lock.lock();
//          解决同意交易和另外创建用户交易请求的锁问题
            if (userTarget.isIfTrade()) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.TRADETARGETHASMAN));
                return;
            }
        } finally {
            lock.unlock();
        }
        channel.writeAndFlush(MessageUtil.turnToPacket("你向" + temp[1] + "发起了交易请求等待交易准许"));
//      建立单方交易单
        String uuid = UUID.randomUUID().toString();
        user.setTraceId(uuid);
        Trade trade = new Trade();
        trade.setEndTime(System.currentTimeMillis() + 500000);
        trade.setTradeId(uuid);
        trade.setToUserAgree(false);
        trade.setStartMoney(new BigInteger("0"));
        trade.setToMoney(new BigInteger("0"));
        trade.setStartUserAgree(false);
        trade.setIfexe(false);
        trade.setStartUserBag(new HashMap<String, Userbag>());
        trade.setToUserBag(new HashMap<String, Userbag>());
        trade.setUserStart(user);
        ProjectContext.tradeMap.put(trade.getTradeId(), trade);
        channelTarget.writeAndFlush(MessageUtil.turnToPacket("你收到了来自" + user.getUsername() + "的交易请求交易单为:" + uuid + ",同意交易请输入ytrade=交易单"));
    }

    @Order(orderMsg = "jy=q")
    public void quitTrade(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        Trade trade = ProjectContext.tradeMap.get(user.getTraceId());
        Channel channelStart = ProjectContext.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ProjectContext.userToChannelMap.get(trade.getUserTo());
        try {
            lock.lock();
            trade.setIfexe(false);
        } finally {
            agreelock.unlock();
        }
//      交易单物品还原
        for (Map.Entry<String, Userbag> entry : trade.getToUserBag().entrySet()) {
            userbagCaculation.addUserBagForUser(trade.getUserTo(), entry.getValue());
        }
        for (Map.Entry<String, Userbag> entry : trade.getStartUserBag().entrySet()) {
            userbagCaculation.addUserBagForUser(trade.getUserStart(), entry.getValue());
        }
//      交易失败金币还原
        moneyCaculation.addMoneyToUser(trade.getUserStart(), trade.getStartMoney().toString());
        moneyCaculation.addMoneyToUser(trade.getUserTo(), trade.getToMoney().toString());

        channelStart.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FAILTRADEEND));
        channelEnd.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FAILTRADEEND));
        channelStart.writeAndFlush(MessageUtil.turnToPacket("", PacketType.TRADEMSG));
        channelEnd.writeAndFlush(MessageUtil.turnToPacket("", PacketType.TRADEMSG));
//           内存交易单移除
        ProjectContext.tradeMap.remove(trade.getTradeId());
//          渠道状态还原
        ProjectContext.eventStatus.put(channelStart, EventStatus.STOPAREA);
        ProjectContext.eventStatus.put(channelEnd, EventStatus.STOPAREA);
//          人物tradeid移除
        trade.getUserStart().setTraceId(null);
        trade.getUserTo().setTraceId(null);
//          人物交易状态改为false
        trade.getUserTo().setIfTrade(false);
        trade.getUserStart().setIfTrade(false);
    }

    @Order(orderMsg = "jy=y")
    public void trading(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        Trade trade = ProjectContext.tradeMap.get(user.getTraceId());
        Channel channelStart = ProjectContext.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ProjectContext.userToChannelMap.get(trade.getUserTo());
        try {
            agreelock.lock();
            if (!trade.isIfexe()) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.FAILTRADEEND));
                return;
            }
            if (user == trade.getUserStart() && trade.getStartUserAgree()) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REPEATYESTRADE));
                return;
            }
            if (user == trade.getUserTo() && trade.getToUserAgree()) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REPEATYESTRADE));
                return;
            }
            if (user == trade.getUserStart() && !trade.getStartUserAgree()) {
                trade.setStartUserAgree(true);
            }
            if (user == trade.getUserTo() && !trade.getToUserAgree()) {
                trade.setToUserAgree(true);
            }
            if ((trade.getStartUserAgree() && !trade.getToUserAgree()) || (trade.getToUserAgree() && !trade.getStartUserAgree())) {
                if (user == trade.getUserStart()) {
                    channelStart.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.YOUCOMFIRMTRADE));
                    channelEnd.writeAndFlush(MessageUtil.turnToPacket(trade.getUserStart().getUsername() + "同意了此次交易，请您尽快做出抉择"));
                } else {
                    channelEnd.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.YOUCOMFIRMTRADE));
                    channelStart.writeAndFlush(MessageUtil.turnToPacket(trade.getUserTo().getUsername() + "同意了此次交易，请您尽快做出抉择"));
                }
                return;
            }
        } finally {
            agreelock.unlock();
        }

//          交易成功金币处理
        moneyCaculation.addMoneyToUser(trade.getUserStart(), trade.getToMoney().toString());
        moneyCaculation.addMoneyToUser(trade.getUserTo(), trade.getStartMoney().toString());

        for (Map.Entry<String, Userbag> entry : trade.getToUserBag().entrySet()) {
            userbagCaculation.addUserBagForUser(trade.getUserStart(), entry.getValue());
        }
        for (Map.Entry<String, Userbag> entry : trade.getStartUserBag().entrySet()) {
            userbagCaculation.addUserBagForUser(trade.getUserTo(), entry.getValue());
        }
        channelStart.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSTRADEEND));
        channelEnd.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSTRADEEND));
        channelStart.writeAndFlush(MessageUtil.turnToPacket("", PacketType.TRADEMSG));
        channelEnd.writeAndFlush(MessageUtil.turnToPacket("", PacketType.TRADEMSG));
//           内存交易单移除
        ProjectContext.tradeMap.remove(trade.getTradeId());
//          渠道状态还原
        ProjectContext.eventStatus.put(channelStart, EventStatus.STOPAREA);
        ProjectContext.eventStatus.put(channelEnd, EventStatus.STOPAREA);
//          人物tradeid移除
        trade.getUserStart().setTraceId(null);
        trade.getUserTo().setTraceId(null);
//          人物交易状态改为false
        trade.getUserTo().setIfTrade(false);
        trade.getUserStart().setIfTrade(false);
        UserbagUtil.refreshUserbagInfo(ProjectContext.userToChannelMap.get(trade.getUserTo()));
        UserbagUtil.refreshUserbagInfo(ProjectContext.userToChannelMap.get(trade.getUserStart()));

//          第一次成功交易触发任务
        achievementExecutor.executeFirstTrade(trade.getUserStart(), trade.getUserTo());
    }

    @Order(orderMsg = "xjbjy")
    public void reduceMoney(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        Trade trade = ProjectContext.tradeMap.get(user.getTraceId());
        Channel channelStart = ProjectContext.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ProjectContext.userToChannelMap.get(trade.getUserTo());
        String[] temp = msg.split("=");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger noSendMoney = new BigInteger(temp[1]);
        if (user == trade.getUserStart()) {
            if (trade.getStartMoney().compareTo(noSendMoney) < 0) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHMONEYTORESET));
                return;
            } else {
                moneyCaculation.addMoneyToUser(user, noSendMoney.toString());
                trade.setStartMoney(trade.getStartMoney().subtract(noSendMoney));
            }
        } else {
            if (trade.getToMoney().compareTo(noSendMoney) < 0) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHMONEYTORESET));
                return;
            } else {
                moneyCaculation.addMoneyToUser(user, noSendMoney.toString());
                trade.setToMoney(trade.getToMoney().subtract(noSendMoney));
            }
        }
//          输出双方物品信息
        String resp = outTradeMessage(trade);
        channel.writeAndFlush(MessageUtil.turnToPacket("你的剩余金额为：" + user.getMoney()));
        channelStart.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
        channelEnd.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
        return;
    }

    @Order(orderMsg = "jbjy")
    public void addMoney(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        Trade trade = ProjectContext.tradeMap.get(user.getTraceId());
        Channel channelStart = ProjectContext.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ProjectContext.userToChannelMap.get(trade.getUserTo());
        String[] temp = msg.split("=");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger sendMoney = new BigInteger(temp[1]);
        if (userMoney.compareTo(sendMoney) <= 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.TRADENOENOUGHMONEY));
            return;
        }
        moneyCaculation.removeMoneyToUser(user,sendMoney.toString());
        channel.writeAndFlush(MessageUtil.turnToPacket("你的剩余金额为：" + user.getMoney()));
        if (user == trade.getUserStart()) {
            trade.setStartMoney(trade.getStartMoney().add(sendMoney));
        } else {
            trade.setToMoney(trade.getToMoney().add(sendMoney));
        }
        String resp = outTradeMessage(trade);
        channelStart.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
        channelEnd.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
    }


    @Order(orderMsg = "aoi,qb,ub-,qw,fix,wq,ww")
    public void commonMethod(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(commonEvent, channel, msg);
    }

    @Order(orderMsg = "jyg=")
    public void addGood(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        Trade trade = ProjectContext.tradeMap.get(user.getTraceId());
        Channel channelStart = ProjectContext.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ProjectContext.userToChannelMap.get(trade.getUserTo());
        String[] temp = msg.split("=");
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
//          移除背包给子物品到交易单
        Userbag userbag = getUserBagById(user, temp[1]);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
            return;
        }
//          移除背包格子,放到交易单中
        if (trade.getUserStart() == user) {
            if (userbag.getNum() < Integer.parseInt(temp[2])) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHGOODFORTRADE));
                return;
            }
            moveToUserTrade(trade.getUserStart(), trade.getStartUserBag(), userbag, temp[2]);
        } else {
            moveToUserTrade(trade.getUserTo(), trade.getToUserBag(), userbag, temp[2]);
        }
//          输出双方物品信息
        String resp = outTradeMessage(trade);
        channelStart.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
        channelEnd.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
    }

    @Order(orderMsg = "xjyg=")
    public void reduceGood(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        Trade trade = ProjectContext.tradeMap.get(user.getTraceId());
        Channel channelStart = ProjectContext.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ProjectContext.userToChannelMap.get(trade.getUserTo());
        String[] temp = msg.split("=");
//      移除交易单号武平到背包格子
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (user == trade.getUserStart() && trade.getStartUserBag().containsKey(temp[1])) {
            Userbag userbag = trade.getStartUserBag().get(temp[1]);
            moveToUserBag(userbag, trade.getUserStart(), trade.getStartUserBag(), temp[2]);

            String resp = outTradeMessage(trade);
            channelStart.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
            channelEnd.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
            return;
        }
        if (user == trade.getUserTo() && trade.getToUserBag().containsKey(temp[1])) {
            Userbag userbag = trade.getToUserBag().get(temp[1]);
            moveToUserBag(userbag, trade.getUserTo(), trade.getToUserBag(), temp[2]);

            String resp = outTradeMessage(trade);
            channelStart.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
            channelEnd.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.TRADEMSG));
            return;
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
    }

    private void moveToUserBag(Userbag userbag, User user, Map<String, Userbag> userBag, String num) {
        if (userbag.getTypeof().equals(Good.EQUIPMENT) && Integer.parseInt(num) == 1) {
            userBag.remove(userbag.getId());
            user.getUserBag().add(userbag);
            return;
        }
        if (userbag.getTypeof().equals(Good.MPMEDICINE)) {
            if (userbag.getNum() == Integer.parseInt(num)) {
                userBag.remove(userbag.getId());
            } else {
                userbag.setNum(userbag.getNum() - Integer.parseInt(num));
            }
            for (Userbag userbagTemp : user.getUserBag()) {
                if (userbagTemp.getWid().equals(userbag.getWid())) {
                    userbagTemp.setNum(userbagTemp.getNum() + Integer.parseInt(num));
                    return;
                }
            }
            Userbag userbagNew = new Userbag();
            userbagNew.setId(UUID.randomUUID().toString());
            userbagNew.setNum(Integer.parseInt(num));
            userbagNew.setWid(userbag.getWid());
            userbagNew.setTypeof(userbag.getTypeof());
            userbagNew.setName(userbag.getName());
            user.getUserBag().add(userbagNew);
            return;
        }
    }

    private void moveToUserTrade(User user, Map<String, Userbag> startUserBag, Userbag userbag, String num) {
        if (Integer.parseInt(num) == userbag.getNum()) {
            startUserBag.put(userbag.getId(), userbag);
        } else {
            Userbag userbagNew = new Userbag();
            userbagNew.setTypeof(userbag.getTypeof());
            userbagNew.setWid(userbag.getWid());
            userbagNew.setNum(Integer.parseInt(num));
            userbagNew.setName(userbag.getName());
            userbagNew.setId(UUID.randomUUID().toString());
            startUserBag.put(userbagNew.getId(), userbagNew);
        }
        userbagCaculation.removeUserbagFromUser(user, userbag, Integer.parseInt(num));
    }


    private String outTradeMessage(Trade trade) {
        String resp = MessageConfig.TRADEMSG;
        resp += System.getProperty("line.separator") +
                MessageConfig.MESSAGESTART + System.getProperty("line.separator");
        resp += trade.getUserStart().getUsername() + "放到交易单上的物品" + System.getProperty("line.separator");
        resp += trade.getUserStart().getUsername() + "的交易金币：" + trade.getStartMoney().toString() + System.getProperty("line.separator");
        for (Map.Entry<String, Userbag> entry : trade.getStartUserBag().entrySet()) {
            resp += trade.getUserStart().getUsername() + "的背包格子为" + entry.getValue().getId() + "物品为:" + getUserbagInfoByUserbag(entry.getValue()) + "物品数量：" + entry.getValue().getNum() + System.getProperty("line.separator");
        }
        resp += MessageConfig.MESSAGEMID + System.getProperty("line.separator");
        resp += trade.getUserTo().getUsername() + "放到交易单上的物品" + System.getProperty("line.separator");
        resp += trade.getUserTo().getUsername() + "的交易金币" + trade.getToMoney().toString() + System.getProperty("line.separator");
        for (Map.Entry<String, Userbag> entry : trade.getToUserBag().entrySet()) {
            resp += trade.getUserTo().getUsername() + "的背包格子为" + entry.getValue().getId() + "物品为:" + getUserbagInfoByUserbag(entry.getValue()) + "物品数量：" + entry.getValue().getNum() + System.getProperty("line.separator");
        }
        resp += MessageConfig.MESSAGEEND + System.getProperty("line.separator");
        return resp;
    }

    private String getUserbagInfoByUserbag(Userbag userbag) {
        if (userbag.getTypeof().equals(Good.MPMEDICINE)) {
            MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(userbag.getWid());
            return mpMedicine.getName();
        }
        if (userbag.getTypeof().equals(Good.EQUIPMENT)) {
            Equipment equipment = ProjectContext.equipmentMap.get(userbag.getWid());
            return equipment.getName();
        }
        return null;
    }

    private Userbag getUserBagById(User user, String ubId) {
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getId().equals(ubId)) {
                return userbag;
            }
        }
        return null;
    }
}
