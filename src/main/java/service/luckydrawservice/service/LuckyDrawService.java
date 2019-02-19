package service.luckydrawservice.service;

import config.impl.excel.EquipmentResourceLoad;
import config.impl.excel.LuckDrawItemResourceLoad;
import config.impl.reflect.ReflectMethodLoad;
import core.annotation.order.Order;
import core.annotation.order.OrderRegion;
import core.channel.ChannelStatus;
import core.component.good.Equipment;
import core.component.good.parent.BaseGood;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import core.reflect.son.GoodRewardInvokeMethod;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import mapper.UserbagMapper;
import mapper.UserluckydrawitemrecordMapper;
import mapper.UserluckydrawrecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userluckydrawitemrecord;
import pojo.Userluckydrawrecord;
import service.caculationservice.service.MoneyCaculationService;
import service.luckydrawservice.entity.LuckDrawGoodItem;
import service.luckydrawservice.entity.LuckyDrawItem;
import service.userbagservice.service.UserbagService;
import utils.ChannelUtil;
import utils.MathUtil;
import utils.MessageUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName LuckyDrawService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/14 10:36
 * @Version 1.0
 **/
@Component
@Slf4j
@OrderRegion
public class LuckyDrawService {
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private UserluckydrawitemrecordMapper userluckydrawitemrecordMapper;
    @Autowired
    private UserluckydrawrecordMapper userluckydrawrecordMapper;
    @Autowired
    private MoneyCaculationService moneyCaculationService;

    /**
     * 展示幸运抽奖项目
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.SHOW_ALL_LUCKY_DRAW, status = {ChannelStatus.COMMONSCENE})
    public void showLuckyDraw(Channel channel, String msg) {
        String resp = "";
        for (Map.Entry<Integer, LuckyDrawItem> entry : LuckDrawItemResourceLoad.luckDrawItemMap.entrySet()) {
            LuckyDrawItem luckyDrawItem = entry.getValue();
            resp += "[抽奖id：" + luckyDrawItem.getId() + "] [抽奖名称：" + luckyDrawItem.getName() + "] [花费的金币：" + luckyDrawItem.getNeedMoney() + "] 奖品如下：" + System.getProperty("line.separator");
            resp += MessageConfig.MESSAGE_MID + System.getProperty("line.separator");
            for (Map.Entry<Integer, LuckDrawGoodItem> entry1 : luckyDrawItem.getRandomLuckDrawGoodItemMap().entrySet()) {
                LuckDrawGoodItem luckDrawGoodItem = entry1.getValue();
                resp += userbagService.getGoodNameByWidAndType(luckDrawGoodItem.getType(), luckDrawGoodItem.getWid()) + " 数量：" + luckDrawGoodItem.getNum() + System.getProperty("line.separator");
            }
            for (Map.Entry<Integer, LuckDrawGoodItem> entry1 : luckyDrawItem.getConfirmLuckDrawGoodItemMap().entrySet()) {
                LuckDrawGoodItem luckDrawGoodItem = entry1.getValue();
                resp += userbagService.getGoodNameByWidAndType(luckDrawGoodItem.getType(), luckDrawGoodItem.getWid()) + " 数量：" + luckDrawGoodItem.getNum() + System.getProperty("line.separator");
            }
            resp += MessageConfig.MESSAGE_MID + System.getProperty("line.separator");
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(resp);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 进行幸运抽奖
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.START_LUCKY_DRAW_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void luckyDraw(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (temp.length != GrobalConfig.TWO) {
            return;
        }
        Integer luckyDrawId = Integer.parseInt(temp[1]);
        Userluckydrawrecord userluckydrawrecord = userluckydrawrecordMapper.selectByUserNameAndLuckyDrawRecordId(user.getUsername(), luckyDrawId);
        LuckyDrawItem luckyDrawItem = LuckDrawItemResourceLoad.luckDrawItemMap.get(luckyDrawId);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
//      钱的校验，通过则扣钱不通过返回
        if (!moneyCaculationService.removeMoneyToUser(user, luckyDrawItem.getNeedMoney() + "")) {
            return;
        }


//      初次抽奖逻辑
        if (userluckydrawrecord == null) {
//          新增记录
            userluckydrawrecord = new Userluckydrawrecord();
            userluckydrawrecord.setNowcount(1);
            userluckydrawrecord.setLuckydrawid(luckyDrawId);
            userluckydrawrecord.setUsername(user.getUsername());
            userluckydrawrecordMapper.insert(userluckydrawrecord);
//          生成随机
            for (Map.Entry<Integer, LuckDrawGoodItem> entry : luckyDrawItem.getConfirmLuckDrawGoodItemMap().entrySet()) {
                LuckDrawGoodItem luckDrawGoodItem = entry.getValue();
                int random = MathUtil.getRandom(luckDrawGoodItem.getStartCount(), luckDrawGoodItem.getEndCount());
                Userluckydrawitemrecord userluckydrawitemrecord = new Userluckydrawitemrecord();
//              新增随机条目
                userluckydrawitemrecord.setAlreadycount(random);
                userluckydrawitemrecord.setItemid(luckDrawGoodItem.getId());
                userluckydrawitemrecord.setLuckydrawid(userluckydrawrecord.getId());
                userluckydrawitemrecordMapper.insert(userluckydrawitemrecord);
            }

            LuckDrawGoodItem luckDrawGoodItem = getRandomLuckDrawGoodItem(luckyDrawItem);
            builder.setData("恭喜您抽中了===》"
                    + userbagService.getGoodNameByWidAndType(luckDrawGoodItem.getType(), luckDrawGoodItem.getWid())
                    + "《=== 数量为：" + luckDrawGoodItem.getNum());
            MessageUtil.sendMessage(channel, builder.build());
//          初次入包
            rewardGoodToUser(user, luckDrawGoodItem);
        } else {
//          再次抽奖逻辑
            boolean ifGet = false;
            userluckydrawrecord.setNowcount(userluckydrawrecord.getNowcount() + 1);
//          抽到内定奖品
            for (Userluckydrawitemrecord userluckydrawitemrecord : userluckydrawrecord.getUserluckydrawitemrecordList()) {
                if (userluckydrawitemrecord.getAlreadycount().equals(userluckydrawrecord.getNowcount())) {
                    LuckDrawGoodItem luckDrawGoodItem = luckyDrawItem.getConfirmLuckDrawGoodItemMap().get(userluckydrawitemrecord.getItemid());
                    builder.setData("恭喜您抽中了*****非常稀有的超低爆率的大奖*****====》"
                            + userbagService.getGoodNameByWidAndType(luckDrawGoodItem.getType(), luckDrawGoodItem.getWid())
                            + "《=== 数量为：" + luckDrawGoodItem.getNum());
                    MessageUtil.sendMessage(channel, builder.build());
//                  内定入包
                    rewardGoodToUser(user, luckDrawGoodItem);
                    ifGet = true;
                }
            }
//          抽不到内定奖品，出随机
            if (!ifGet) {
                LuckDrawGoodItem luckDrawGoodItem = getRandomLuckDrawGoodItem(luckyDrawItem);
                builder.setData("恭喜您抽中了===》" + userbagService.getGoodNameByWidAndType(luckDrawGoodItem.getType(), luckDrawGoodItem.getWid())
                        + "《=== 数量为：" + luckDrawGoodItem.getNum());
                MessageUtil.sendMessage(channel, builder.build());
//              随机入包
                rewardGoodToUser(user, luckDrawGoodItem);
            }
            userluckydrawrecordMapper.updateByPrimaryKeySelective(userluckydrawrecord);

//          重刷用户次数
            if (userluckydrawrecord.getNowcount().equals(luckyDrawItem.getRefreshCount())) {
                for (Userluckydrawitemrecord userluckydrawitemrecord : userluckydrawrecord.getUserluckydrawitemrecordList()) {
                    LuckDrawGoodItem luckDrawGoodItem = luckyDrawItem.getConfirmLuckDrawGoodItemMap().get(userluckydrawitemrecord.getItemid());
                    int random = MathUtil.getRandom(luckDrawGoodItem.getStartCount(), luckDrawGoodItem.getEndCount());
                    userluckydrawitemrecord.setAlreadycount(random);
                    userluckydrawitemrecordMapper.updateByPrimaryKeySelective(userluckydrawitemrecord);
                }
                userluckydrawrecord.setNowcount(1);
                userluckydrawrecordMapper.updateByPrimaryKeySelective(userluckydrawrecord);
            }
        }
    }

    /**
     * 中奖入包
     *
     * @param user
     * @param luckDrawGoodItem
     */
    private void rewardGoodToUser(User user, LuckDrawGoodItem luckDrawGoodItem) {
//      此处进行反射调用
        try {
            if (ReflectMethodLoad.goodRewardInvokeMethodMap.containsKey(luckDrawGoodItem.getType())) {
                GoodRewardInvokeMethod goodRewardInvokeMethod = ReflectMethodLoad.goodRewardInvokeMethodMap.get(luckDrawGoodItem.getType());
                goodRewardInvokeMethod.getMethod().invoke(goodRewardInvokeMethod.getObject(), user, luckDrawGoodItem);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 中随机奖品
     *
     * @param luckyDrawItem
     * @return
     */
    private LuckDrawGoodItem getRandomLuckDrawGoodItem(LuckyDrawItem luckyDrawItem) {
        Map<Integer, LuckDrawGoodItem> randomLuckDrawGoodItemMap = luckyDrawItem.getRandomLuckDrawGoodItemMap();
        Integer[] keys = randomLuckDrawGoodItemMap.keySet().toArray(new Integer[0]);
        Random random = new Random();
        Integer randomKey = keys[random.nextInt(keys.length)];
        return luckyDrawItem.getRandomLuckDrawGoodItemMap().get(randomKey);
    }
}
