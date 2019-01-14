package service.auctionservice.service;

import core.channel.ChannelStatus;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.context.ProjectContext;
import core.annotation.Order;
import io.netty.channel.Channel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.auctionservice.entity.AuctionItem;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import service.emailservice.service.EmailService;
import service.userbagservice.service.UserbagService;
import service.userservice.service.UserService;
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
@Region
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
    @Order(orderMsg = "eau",status = {ChannelStatus.COMMONSCENE})
    public void enterAuctionView(Channel channel, String msg) {
        ProjectContext.channelStatus.put(channel, ChannelStatus.AUCTION);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTER_AUCTION_VIEW));
    }

    /**
     * 退出拍卖行
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qau",status = {ChannelStatus.AUCTION})
    public void outAuctionView(Channel channel, String msg) {
        ProjectContext.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUT_AUCTION_VIEW));
    }

    /**
     * 展示拍卖物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "queryau",status = {ChannelStatus.AUCTION})
    public void queryAuctionItems(Channel channel, String msg) {
        refreshAuctionItems(channel);
    }

    /**
     * 上架拍卖行
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "sj=",status = {ChannelStatus.AUCTION})
    public void upAuctionItem(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ProjectContext.channelToUserMap.get(channel);
        if (temp.length != GrobalConfig.FIVE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
            return;
        }
        if (!userbagService.checkUserbagNum(userbag, temp[GrobalConfig.TWO])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHCHANGEGOOD));
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
        ProjectContext.auctionItemMap.put(auctionItem.getId(), auctionItem);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESS_UP_AUCTIONITEM));
    }

    /**
     * 竞拍物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qp=",status = {ChannelStatus.AUCTION})
    public void getAuctionItem(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ProjectContext.channelToUserMap.get(channel);
//      拍卖物品校验
        if (!ProjectContext.auctionItemMap.containsKey(temp[GrobalConfig.ONE])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NO_THIS_AUCTIONITEM));
            return;
        }
        AuctionItem auctionItem = ProjectContext.auctionItemMap.get(temp[1]);
        try {
            auctionItem.getAuctionItemLock().lock();
//          拍卖结束,多线程
            if (auctionItem.isEnd()) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.AUCTION_IS_END));
                return;
            }
//          用户不能竞拍自己的东西
            if (user.getUsername().equals(auctionItem.getFromUsername())) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NO_AUCTION_FOR_SELF));
                return;
            }
//          一口价
            if (auctionItem.isImmediate()) {
//              指令校验
                if (temp.length != GrobalConfig.TWO) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                    return;
                }
//              拍卖金币校验
                if (!moneyCaculationService.checkUserHasEnoughMoney(user, auctionItem.getSaleMoney().toString())) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHMONEYTOGIVE));
                    return;
                }
//              扣除用户金币
                moneyCaculationService.removeMoneyToUser(user, auctionItem.getSaleMoney().toString());
//              新增物品
                userbagCaculationService.addUserBagForUser(user, auctionItem.getUserbag());
                auctionItem.setEnd(true);
//              删除拍卖单
                ProjectContext.auctionItemMap.remove(auctionItem.getId());
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESS_BUY_GOOD_IMMEDIATE));
            } else {
//              指令校验
                if (temp.length != GrobalConfig.THREE) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                    return;
                }
//              用户金币校验
                if (!moneyCaculationService.checkUserHasEnoughMoney(user, temp[GrobalConfig.TWO])) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHMONEYTOGIVE));
                    return;
                }
//              参加竞拍价格和物品价格校验
                if (Integer.parseInt(temp[GrobalConfig.TWO]) < auctionItem.getBuyMoney() + 1) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NO_ENOUGH_MONEY_FOR_AUCTION));
                    return;
                }
//              抢占前一个拍卖者,邮件退回前一个拍卖者金币
                if (auctionItem.getBuyUsername() != null) {
                    emailService.systemSendMail(auctionItem.getBuyUsername(), "竞拍失败，返回金币:" + auctionItem.getBuyMoney(), null, auctionItem.getBuyMoney());
                    userService.sendMessageByUserName(auctionItem.getBuyUsername(), "[您的竞拍单:" + auctionItem.getId() + "]被顶下了，可重新加价或者放弃竞拍");
                }
//              更新竞拍单子
                auctionItem.setBuyMoney(Integer.parseInt(temp[2]));
                auctionItem.setBuyUsername(user.getUsername());
//              扣除用户金币
                moneyCaculationService.removeMoneyToUser(user, temp[2]);
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESS_BUY_GOOD_UP_PRICE));
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
        if (ProjectContext.auctionItemMap.size() == 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOAUCTIONITEMS));
            return;
        }
        for (Map.Entry<String, AuctionItem> entry : ProjectContext.auctionItemMap.entrySet()) {
            AuctionItem auctionItem = entry.getValue();
            queryMsg += "[拍卖ID: " + auctionItem.getId() + "] [卖家名字:" + auctionItem.getFromUsername() + "] " +
                    "[竞拍的物品:" + userbagService.getGoodNameByUserbag(auctionItem.getUserbag()) + "]" + "[是否一口价:" + auctionItem.isImmediate() + "]";
            if (auctionItem.isImmediate()) {
                queryMsg += " [价格:" + auctionItem.getSaleMoney() + "]";
            } else {
                queryMsg += " [竞拍起价:" + auctionItem.getSaleMoney() + "]" + " [目前竞拍者: " + auctionItem.getBuyUsername() + "]"
                        + " [目前竞拍价格:" + auctionItem.getBuyMoney() + "]" + " [竞拍截止时间:" + auctionItem.getEndTime() + "]";
            }
            queryMsg += System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(queryMsg));
    }
}
