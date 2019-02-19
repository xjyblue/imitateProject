package service.auctionservice.service;

import core.annotation.order.OrderRegion;
import core.channel.ChannelStatus;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.annotation.order.Order;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.auctionservice.entity.AuctionCache;
import service.auctionservice.entity.AuctionItem;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import service.emailservice.service.EmailService;
import service.userbagservice.service.UserbagService;
import service.userservice.service.UserService;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.Map;
import java.util.UUID;


/**
 * @ClassName AuctionService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/8 20:57
 * @Version 1.0
 **/
@Component
@OrderRegion
public class AuctionService {
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private UserService userService;
    @Autowired
    private MoneyCaculationService moneyCaculationService;
    @Autowired
    private UserbagCaculationService userbagCaculationService;
    @Autowired
    private EmailService emailService;

    /**
     * 进入拍卖行
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.ENTER_AUTION_VIEW_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void enterAuctionView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.AUCTION);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.ENTER_AUCTION_VIEW);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 退出拍卖行
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.QUIT_AUCTION_ORDER, status = {ChannelStatus.AUCTION})
    public void outAuctionView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.OUT_AUCTION_VIEW);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 展示拍卖物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.SHOW_AUTION_ITEM_ORDER, status = {ChannelStatus.AUCTION})
    public void queryAuctionItems(Channel channel, String msg) {
        refreshAuctionItems(channel);
    }

    /**
     * 上架拍卖行
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.UP_AUCTION_ITEM_ORDER, status = {ChannelStatus.AUCTION})
    public void upAuctionItem(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (temp.length != GrobalConfig.FIVE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_USERBAG_ID);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (!userbagService.checkUserbagNum(userbag, temp[GrobalConfig.TWO])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_ENOUGH_CHANGE_GOOD);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        //新的交易物品
        Userbag userbagNew = new Userbag();
        BeanUtils.copyProperties(userbag, userbagNew);
        userbagNew.setNum(Integer.parseInt(temp[2]));
        //移除用户背包的东西
        userbagCaculationService.removeUserbagFromUser(user, userbag, Integer.parseInt(temp[2]));
        //新增拍卖行的记录。。。
        AuctionItem auctionItem = new AuctionItem();
        auctionItem.setId(UUID.randomUUID().toString());
        auctionItem.setSaleMoney(Integer.parseInt(temp[3]));
        auctionItem.setEndTime(GrobalConfig.AUCTION_END_TIME + System.currentTimeMillis());
        auctionItem.setUserbag(userbagNew);
        auctionItem.setFromUsername(user.getUsername());
        auctionItem.setBuyMoney(Integer.parseInt(temp[3]));
        auctionItem.setImmediate("1".equals(temp[4]));
        auctionItem.setEnd(false);
        AuctionCache.auctionItemMap.put(auctionItem.getId(), auctionItem);

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.SUCCESS_UP_AUCTION_ITEM);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 竞拍物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.GET_AUCTION_ITEM_ORDER, status = {ChannelStatus.AUCTION})
    public void getAuctionItem(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ChannelUtil.channelToUserMap.get(channel);
//      拍卖物品校验
        if (!AuctionCache.auctionItemMap.containsKey(temp[GrobalConfig.ONE])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_THIS_AUCTION_ITEM);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        AuctionItem auctionItem = AuctionCache.auctionItemMap.get(temp[1]);
        try {
            auctionItem.getAuctionItemLock().lock();
//          拍卖结束,多线程
            if (auctionItem.isEnd()) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.AUCTION_IS_END);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
//          用户不能竞拍自己的东西
            if (user.getUsername().equals(auctionItem.getFromUsername())) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NO_AUCTION_FOR_SELF);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
//          一口价
            if (auctionItem.isImmediate()) {
//              指令校验
                if (temp.length != GrobalConfig.TWO) {
                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData(MessageConfig.ERROR_ORDER);
                    MessageUtil.sendMessage(channel, builder.build());
                    return;
                }
//              拍卖金币校验
                if (!moneyCaculationService.checkUserHasEnoughMoney(user, auctionItem.getSaleMoney().toString())) {
                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData(MessageConfig.NO_ENOUGH_MONEY_TO_GIVE);
                    MessageUtil.sendMessage(channel, builder.build());
                    return;
                }
                User userS = userService.getUserByNameFromSession(auctionItem.getFromUsername());
                if (userS == null) {
//                  处理离线用户的交易逻辑
                }
//              扣除用户金币
                moneyCaculationService.removeMoneyToUser(user, auctionItem.getSaleMoney().toString());
//              新增物品
                userbagCaculationService.addUserBagForUser(user, auctionItem.getUserbag());
//              新增对方用户金币
                moneyCaculationService.addMoneyToUser(userS, auctionItem.getSaleMoney().toString());
                auctionItem.setEnd(true);
//              删除拍卖单
                AuctionCache.auctionItemMap.remove(auctionItem.getId());

                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.SUCCESS_BUY_GOOD_IMMEDIATE);
                MessageUtil.sendMessage(channel, builder.build());
            } else {
//              指令校验
                if (temp.length != GrobalConfig.THREE) {
                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData(MessageConfig.ERROR_ORDER);
                    MessageUtil.sendMessage(channel, builder.build());
                    return;
                }
//              用户金币校验
                if (!moneyCaculationService.checkUserHasEnoughMoney(user, temp[GrobalConfig.TWO])) {
                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData(MessageConfig.NO_ENOUGH_MONEY_TO_GIVE);
                    MessageUtil.sendMessage(channel, builder.build());
                    return;
                }
//              参加竞拍价格和物品价格校验
                if (Integer.parseInt(temp[GrobalConfig.TWO]) < auctionItem.getBuyMoney() + 1) {
                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData(MessageConfig.NO_ENOUGH_MONEY_FOR_AUCTION);
                    MessageUtil.sendMessage(channel, builder.build());
                    return;
                }
//              抢占前一个拍卖者,邮件退回前一个拍卖者金币
                if (auctionItem.getBuyUsername() != null) {
                    emailService.systemSendMail(auctionItem.getBuyUsername(), "竞拍失败，返回金币:" + auctionItem.getBuyMoney(), null, auctionItem.getBuyMoney());
                    userService.sendMessageByUserName(auctionItem.getBuyUsername(), "[您的竞拍单:" + auctionItem.getId() + "]被顶下了，可重新加价或者放弃竞拍");
                }
//              扣除用户金币
                moneyCaculationService.removeMoneyToUser(user, temp[2]);
//              归还上一个竞拍者的金币
                if (auctionItem.getBuyUsername() != null) {
                    User userOld = userService.getUserByNameFromSession(auctionItem.getBuyUsername());
                    moneyCaculationService.addMoneyToUser(userOld, auctionItem.getBuyMoney().toString());
                }
//              更新竞拍单子
                auctionItem.setBuyMoney(Integer.parseInt(temp[2]));
                auctionItem.setBuyUsername(user.getUsername());

                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.SUCCESS_BUY_GOOD_UP_PRICE);
                MessageUtil.sendMessage(channel, builder.build());
            }
        } finally {
            auctionItem.getAuctionItemLock().unlock();
        }
    }

    /**
     * 刷新拍卖行
     *
     * @param channel
     */
    private void refreshAuctionItems(Channel channel) {
        String queryMsg = "";
        if (AuctionCache.auctionItemMap.size() == 0) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_AUCTION_ITEMS);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        for (Map.Entry<String, AuctionItem> entry : AuctionCache.auctionItemMap.entrySet()) {
            AuctionItem auctionItem = entry.getValue();
            queryMsg += "[拍卖ID: " + auctionItem.getId() + "] [卖家名字:" + auctionItem.getFromUsername() + "] " +
                    "[竞拍的物品:" + userbagService.getGoodNameByUserbag(auctionItem.getUserbag()) + "]" + "[是否一口价:" + auctionItem.isImmediate() + "]";
            if (auctionItem.isImmediate()) {
                queryMsg += " [价格:" + auctionItem.getSaleMoney() + "]";
            } else {
                queryMsg += " [竞拍起价:" + auctionItem.getSaleMoney() + "]" + " [目前竞拍者: " + auctionItem.getBuyUsername() + "]"
                        + " [目前竞拍价格:" + auctionItem.getBuyMoney() + "]" + " [竞拍截止时间:" + (auctionItem.getEndTime() - System.currentTimeMillis()) + "ms]";
            }
            queryMsg += System.getProperty("line.separator");
        }

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(queryMsg);
        MessageUtil.sendMessage(channel, builder.build());
    }
}
