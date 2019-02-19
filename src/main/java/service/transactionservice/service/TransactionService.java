package service.transactionservice.service;

import core.annotation.order.OrderRegion;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import service.achievementservice.service.AchievementService;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import core.component.good.parent.BaseGood;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import service.transactionservice.entity.TradeCache;
import service.userbagservice.service.UserbagService;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import core.annotation.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.transactionservice.entity.Trade;
import utils.ChannelUtil;
import utils.MessageUtil;
import service.userservice.service.UserService;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName TransactionService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@OrderRegion
public class TransactionService {

    private Lock lock = new ReentrantLock();
    private Lock agreelock = new ReentrantLock();

    @Autowired
    private UserbagCaculationService userbagCaculationService;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private MoneyCaculationService moneyCaculationService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserbagService userbagService;

    /**
     * 不同意交易
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.DISAGREE_TRADE_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void cancelTrade(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (user.getTraceId() == null || !TradeCache.tradeMap.containsKey(user.getTraceId())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_CREATE_TRADE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Trade trade = TradeCache.tradeMap.get(user.getTraceId());

//      加锁处理
        lock.lock();
        try {
//          解决同意线程先抢到锁
            if (trade.isIfexe()) {
                return;
            } else {
//              解决取消线程先抢到锁
                TradeCache.tradeMap.remove(user.getTraceId());
                user.setTraceId(null);
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.CANCEL_TRADE);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 同意交易
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.AGREE_TRADE_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void agreeTrade(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (!TradeCache.tradeMap.containsKey(temp[1])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_TRADE_RECORD);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      建立起交易，把两者的转态弄到交易状态中去，多个线程抢锁
        User userStart = TradeCache.tradeMap.get(temp[1]).getUserStart();
        Trade trade = TradeCache.tradeMap.get(temp[1]);
        lock.lock();
        try {
//          解决取消线程先抢到锁
            if (!TradeCache.tradeMap.containsKey(trade.getTradeId())) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.CANCEL_TRADE);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
//          解决同意线程先抢到锁
//          解决针对同一用户的同意交易和被同意的抢锁问题
            if (userStart.isIfTrade() || user.isIfTrade()) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.TRADEING);
                MessageUtil.sendMessage(channel, builder.build());
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
        ChannelUtil.channelStatus.put(channel, ChannelStatus.TRADE);
        Channel channelStart = ChannelUtil.userToChannelMap.get(userStart);
        ChannelUtil.channelStatus.put(channelStart, ChannelStatus.TRADE);
        trade.setUserTo(user);
        trade.setEndTime(System.currentTimeMillis() + 500000);

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.SUCCESS_CREATE_TRADE);
        MessageUtil.sendMessage(channel, builder.build());
        MessageUtil.sendMessage(channelStart, builder.build());

        ServerPacket.TradeResp.Builder builder1 = ServerPacket.TradeResp.newBuilder();
        builder1.setData("欢迎来到交易界面，现在您与" + userStart.getUsername() + "建立交易，jbjy=金币金额进行金币交易，jyg=交易格子号可以把交易物品填充到交易栏，jy=y确认交易物品，双方都确认后交易完成");
        MessageUtil.sendMessage(channel, builder1.build());
        builder1.setData("欢迎来到交易界面，现在您与" + user.getUsername() + "建立交易，jbjy=金币金额进行金币交易，jyg=交易格子号可以把交易物品填充到交易栏，jy=y确认交易物品，双方都确认后交易完成");
        MessageUtil.sendMessage(channelStart, builder1.build());
    }

    /**
     * 请求交易
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.TRADE_REQUEST_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void createTrade(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (userService.getUserByNameFromSession(temp[1]) == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_TRACE_USER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Channel channelTarget = ChannelUtil.userToChannelMap.get(userService.getUserByNameFromSession(temp[1]));
        if (channelTarget == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_TRACE_USER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User userTarget = ChannelUtil.channelToUserMap.get(channelTarget);
        if (user.getTraceId() != null && TradeCache.tradeMap.containsKey(user.getTraceId())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.YOU_TRADE_IS_ING);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        try {
            lock.lock();
//          解决同意交易和另外创建用户交易请求的锁问题
            if (userTarget.isIfTrade()) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.OTHER_TRADE_IS_ING);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
        } finally {
            lock.unlock();
        }

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("你向" + temp[1] + "发起了交易请求等待交易准许");
        MessageUtil.sendMessage(channel, builder.build());
//      建立单方交易单
        String uuid = UUID.randomUUID().toString();
        user.setTraceId(uuid);
        Trade trade = new Trade();
        trade.setEndTime(System.currentTimeMillis() + 500000);
        trade.setTradeId(uuid);
        trade.setToUserAgree(false);
        trade.setStartMoney(new BigInteger(GrobalConfig.MINVALUE));
        trade.setToMoney(new BigInteger(GrobalConfig.MINVALUE));
        trade.setStartUserAgree(false);
        trade.setIfexe(false);
        trade.setStartUserBag(new HashMap<String, Userbag>(64));
        trade.setToUserBag(new HashMap<String, Userbag>(64));
        trade.setUserStart(user);
        TradeCache.tradeMap.put(trade.getTradeId(), trade);


        builder.setData("你收到了来自" + user.getUsername() + "的交易请求交易单为:" + uuid + ",同意交易请输入ytrade=交易单");
        MessageUtil.sendMessage(channelTarget, builder.build());
    }

    /**
     * 取消交易
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.TRADING_CANCEL_TRADE_ORDER, status = {ChannelStatus.TRADE})
    public void quitTrade(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        Trade trade = TradeCache.tradeMap.get(user.getTraceId());
        Channel channelStart = ChannelUtil.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ChannelUtil.userToChannelMap.get(trade.getUserTo());
        try {
            agreelock.lock();
            trade.setIfexe(false);
        } finally {
            agreelock.unlock();
        }
//      交易单物品还原
        for (Map.Entry<String, Userbag> entry : trade.getToUserBag().entrySet()) {
            userbagCaculationService.addUserBagForUser(trade.getUserTo(), entry.getValue());
        }
        for (Map.Entry<String, Userbag> entry : trade.getStartUserBag().entrySet()) {
            userbagCaculationService.addUserBagForUser(trade.getUserStart(), entry.getValue());
        }
//      交易失败金币还原
        moneyCaculationService.addMoneyToUser(trade.getUserStart(), trade.getStartMoney().toString());
        moneyCaculationService.addMoneyToUser(trade.getUserTo(), trade.getToMoney().toString());

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.FAIL_TRADE_END);
        MessageUtil.sendMessage(channelStart, builder.build());
        MessageUtil.sendMessage(channelEnd, builder.build());

        ServerPacket.TradeResp.Builder builder1 = ServerPacket.TradeResp.newBuilder();
        builder1.setData("");
        MessageUtil.sendMessage(channelStart, builder1.build());
        MessageUtil.sendMessage(channelEnd, builder1.build());

//           内存交易单移除
        TradeCache.tradeMap.remove(trade.getTradeId());
//          渠道状态还原
        ChannelUtil.channelStatus.put(channelStart, ChannelStatus.COMMONSCENE);
        ChannelUtil.channelStatus.put(channelEnd, ChannelStatus.COMMONSCENE);
//          人物tradeid移除
        trade.getUserStart().setTraceId(null);
        trade.getUserTo().setTraceId(null);
//          人物交易状态改为false
        trade.getUserTo().setIfTrade(false);
        trade.getUserStart().setIfTrade(false);
    }

    /**
     * 同意交易的物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.TRADING_AGREE_TRADE_ORDER, status = {ChannelStatus.TRADE})
    public void trading(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        Trade trade = TradeCache.tradeMap.get(user.getTraceId());
        Channel channelStart = ChannelUtil.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ChannelUtil.userToChannelMap.get(trade.getUserTo());
        try {
            agreelock.lock();
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            if (!trade.isIfexe()) {
                builder.setData(MessageConfig.FAIL_TRADE_END);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
            if (user == trade.getUserStart() && trade.getStartUserAgree()) {
                builder.setData(MessageConfig.REPEAT_YES_TRADE);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
            if (user == trade.getUserTo() && trade.getToUserAgree()) {
                builder.setData(MessageConfig.REPEAT_YES_TRADE);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
            if (user == trade.getUserStart() && !trade.getStartUserAgree()) {
                trade.setStartUserAgree(true);
            }
            if (user == trade.getUserTo() && !trade.getToUserAgree()) {
                trade.setToUserAgree(true);
            }
            if (selectTradeUser(trade)) {
                if (user == trade.getUserStart()) {
                    builder.setData(MessageConfig.YOU_COMFIRM_TRADE);
                    MessageUtil.sendMessage(channelStart, builder.build());
                    builder.setData(trade.getUserStart().getUsername() + "同意了此次交易，请您尽快做出抉择");
                    MessageUtil.sendMessage(channelEnd, builder.build());
                } else {
                    builder.setData(MessageConfig.YOU_COMFIRM_TRADE);
                    MessageUtil.sendMessage(channelEnd, builder.build());
                    builder.setData(trade.getUserTo().getUsername() + "同意了此次交易，请您尽快做出抉择");
                    MessageUtil.sendMessage(channelStart, builder.build());
                }
                return;
            }
        } finally {
            agreelock.unlock();
        }

//          交易成功金币处理
        moneyCaculationService.addMoneyToUser(trade.getUserStart(), trade.getToMoney().toString());
        moneyCaculationService.addMoneyToUser(trade.getUserTo(), trade.getStartMoney().toString());

        for (Map.Entry<String, Userbag> entry : trade.getToUserBag().entrySet()) {
            userbagCaculationService.addUserBagForUser(trade.getUserStart(), entry.getValue());
        }
        for (Map.Entry<String, Userbag> entry : trade.getStartUserBag().entrySet()) {
            userbagCaculationService.addUserBagForUser(trade.getUserTo(), entry.getValue());
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.SUCCESS_TRADE_END);
        MessageUtil.sendMessage(channelStart, builder.build());

        builder.setData(MessageConfig.SUCCESS_TRADE_END);
        MessageUtil.sendMessage(channelEnd, builder.build());

        ServerPacket.TradeResp.Builder builder1 = ServerPacket.TradeResp.newBuilder();
        builder1.setData("");
        MessageUtil.sendMessage(channelStart, builder1.build());
        MessageUtil.sendMessage(channelEnd, builder1.build());
//           内存交易单移除
        TradeCache.tradeMap.remove(trade.getTradeId());
//          渠道状态还原
        ChannelUtil.channelStatus.put(channelStart, ChannelStatus.COMMONSCENE);
        ChannelUtil.channelStatus.put(channelEnd, ChannelStatus.COMMONSCENE);
//          人物tradeid移除
        trade.getUserStart().setTraceId(null);
        trade.getUserTo().setTraceId(null);
//          人物交易状态改为false
        trade.getUserTo().setIfTrade(false);
        trade.getUserStart().setIfTrade(false);

//          第一次成功交易触发任务
        achievementService.executeFirstTrade(trade.getUserStart(), trade.getUserTo());
    }

    /**
     * 判断哪个一交易方
     *
     * @param trade
     * @return
     */
    private boolean selectTradeUser(Trade trade) {
        return (trade.getStartUserAgree() && !trade.getToUserAgree()) || (trade.getToUserAgree() && !trade.getStartUserAgree());
    }

    /**
     * 金币交易减少
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.TRADING_REDUCE_MONEY_ORDER, status = {ChannelStatus.TRADE})
    public void reduceMoney(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        Trade trade = TradeCache.tradeMap.get(user.getTraceId());
        Channel channelStart = ChannelUtil.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ChannelUtil.userToChannelMap.get(trade.getUserTo());
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        BigInteger noSendMoney = new BigInteger(temp[1]);
        if (user == trade.getUserStart()) {
            if (trade.getStartMoney().compareTo(noSendMoney) < 0) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NO_ENOUGH_MONEY_TO_RESET);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            } else {
                moneyCaculationService.addMoneyToUser(user, noSendMoney.toString());
                trade.setStartMoney(trade.getStartMoney().subtract(noSendMoney));
            }
        } else {
            if (trade.getToMoney().compareTo(noSendMoney) < 0) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NO_ENOUGH_MONEY_TO_RESET);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            } else {
                moneyCaculationService.addMoneyToUser(user, noSendMoney.toString());
                trade.setToMoney(trade.getToMoney().subtract(noSendMoney));
            }
        }
//          输出双方物品信息
        String resp = outTradeMessage(trade);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("你的剩余金额为：" + user.getMoney());
        MessageUtil.sendMessage(channel, builder.build());


        ServerPacket.TradeResp.Builder builder1 = ServerPacket.TradeResp.newBuilder();
        builder1.setData(resp);
        MessageUtil.sendMessage(channelStart, builder1.build());
        MessageUtil.sendMessage(channelEnd, builder1.build());
        return;
    }

    /**
     * 金币交易增加
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.TRADING_ADD_MONEY_ORDER, status = {ChannelStatus.TRADE})
    public void addMoney(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        Trade trade = TradeCache.tradeMap.get(user.getTraceId());
        Channel channelStart = ChannelUtil.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ChannelUtil.userToChannelMap.get(trade.getUserTo());
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        BigInteger userMoney = new BigInteger(user.getMoney());
        BigInteger sendMoney = new BigInteger(temp[1]);
        if (userMoney.compareTo(sendMoney) <= 0) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.TRADE_NO_ENOUGH_MONEY);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        moneyCaculationService.removeMoneyToUser(user, sendMoney.toString());
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("你的剩余金额为：" + user.getMoney());
        MessageUtil.sendMessage(channel, builder.build());
        if (user == trade.getUserStart()) {
            trade.setStartMoney(trade.getStartMoney().add(sendMoney));
        } else {
            trade.setToMoney(trade.getToMoney().add(sendMoney));
        }
        String resp = outTradeMessage(trade);

        ServerPacket.TradeResp.Builder builder1 = ServerPacket.TradeResp.newBuilder();
        builder1.setData(resp);
        MessageUtil.sendMessage(channelStart, builder1.build());
        MessageUtil.sendMessage(channelEnd, builder1.build());
    }

    /**
     * 添加交易物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.TRADING_ADD_GOOD_ORDER, status = {ChannelStatus.TRADE})
    public void addGood(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        Trade trade = TradeCache.tradeMap.get(user.getTraceId());
        Channel channelStart = ChannelUtil.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ChannelUtil.userToChannelMap.get(trade.getUserTo());
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//          移除背包给子物品到交易单
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_USERBAG_ID);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//          移除背包格子,放到交易单中
        if (trade.getUserStart() == user) {
            if (userbag.getNum() < Integer.parseInt(temp[GrobalConfig.TWO])) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NO_ENOUGH_GOOD_FOR_TRADE);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
            moveToUserTrade(trade.getUserStart(), trade.getStartUserBag(), userbag, temp[2]);
        } else {
            moveToUserTrade(trade.getUserTo(), trade.getToUserBag(), userbag, temp[2]);
        }
//          输出双方物品信息
        String resp = outTradeMessage(trade);

        ServerPacket.TradeResp.Builder builder1 = ServerPacket.TradeResp.newBuilder();
        builder1.setData(resp);
        MessageUtil.sendMessage(channelStart, builder1.build());
        MessageUtil.sendMessage(channelEnd, builder1.build());
    }

    /**
     * 取消交易物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.TRADING_REMOVE_GOOD_ORDER, status = {ChannelStatus.TRADE})
    public void reduceGood(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        Trade trade = TradeCache.tradeMap.get(user.getTraceId());
        Channel channelStart = ChannelUtil.userToChannelMap.get(trade.getUserStart());
        Channel channelEnd = ChannelUtil.userToChannelMap.get(trade.getUserTo());
        String[] temp = msg.split("=");
//      移除交易单号武平到背包格子
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (user == trade.getUserStart() && trade.getStartUserBag().containsKey(temp[1])) {
            Userbag userbag = trade.getStartUserBag().get(temp[1]);
            moveToUserBag(userbag, trade.getUserStart(), trade.getStartUserBag(), temp[2]);

            String resp = outTradeMessage(trade);

            ServerPacket.TradeResp.Builder builder = ServerPacket.TradeResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channelStart, builder.build());

            builder.setData(resp);
            MessageUtil.sendMessage(channelEnd, builder.build());
            return;
        }
        if (user == trade.getUserTo() && trade.getToUserBag().containsKey(temp[1])) {
            Userbag userbag = trade.getToUserBag().get(temp[1]);
            moveToUserBag(userbag, trade.getUserTo(), trade.getToUserBag(), temp[2]);

            String resp = outTradeMessage(trade);

            ServerPacket.TradeResp.Builder builder = ServerPacket.TradeResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channelStart, builder.build());

            builder.setData(resp);
            MessageUtil.sendMessage(channelEnd, builder.build());
            return;
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.ERROR_ORDER);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 移动到交易单
     *
     * @param userbag
     * @param user
     * @param userBag
     * @param num
     */
    private void moveToUserBag(Userbag userbag, User user, Map<String, Userbag> userBag, String num) {
        if (userbag.getTypeof().equals(BaseGood.EQUIPMENT) && Integer.parseInt(num) == 1) {
            userBag.remove(userbag.getId());
            user.getUserBag().add(userbag);
            return;
        }
        if (userbag.getTypeof().equals(BaseGood.MPMEDICINE) || userbag.getTypeof().equals(BaseGood.HPMEDICINE)
                || userbag.getTypeof().equals(BaseGood.CHANGEGOOD)) {
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

    /**
     * 移动到用户背包
     *
     * @param user
     * @param startUserBag
     * @param userbag
     * @param num
     */
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
        userbagCaculationService.removeUserbagFromUser(user, userbag, Integer.parseInt(num));
    }


    /**
     * 输出信息
     *
     * @param trade
     * @return
     */
    private String outTradeMessage(Trade trade) {
        String resp = MessageConfig.TRADE_MSG;
        resp += System.getProperty("line.separator") +
                MessageConfig.MESSAGE_START + System.getProperty("line.separator");
        resp += trade.getUserStart().getUsername() + "放到交易单上的物品" + System.getProperty("line.separator");
        resp += trade.getUserStart().getUsername() + "的交易金币：" + trade.getStartMoney().toString() + System.getProperty("line.separator");
        for (Map.Entry<String, Userbag> entry : trade.getStartUserBag().entrySet()) {
            resp += trade.getUserStart().getUsername() + "的背包格子为: " + userbagService.getGoodNameByUserbag(entry.getValue()) + System.getProperty("line.separator");
        }
        resp += MessageConfig.MESSAGE_MID + System.getProperty("line.separator");
        resp += trade.getUserTo().getUsername() + "放到交易单上的物品" + System.getProperty("line.separator");
        resp += trade.getUserTo().getUsername() + "的交易金币" + trade.getToMoney().toString() + System.getProperty("line.separator");
        for (Map.Entry<String, Userbag> entry : trade.getToUserBag().entrySet()) {
            resp += trade.getUserTo().getUsername() + "的背包格子为: " + "物品为:" + userbagService.getGoodNameByUserbag(entry.getValue()) + System.getProperty("line.separator");
        }
        resp += MessageConfig.MESSAGE_END + System.getProperty("line.separator");

        userbagService.refreshUserbagInfo(ChannelUtil.userToChannelMap.get(trade.getUserTo()), null);
        userbagService.refreshUserbagInfo(ChannelUtil.userToChannelMap.get(trade.getUserStart()), null);
        return resp;
    }

}
