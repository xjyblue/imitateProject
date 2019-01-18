package service.weaponstartservice.service;

import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import io.netty.channel.Channel;
import mapper.UserbagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.caculationservice.service.MoneyCaculationService;
import service.userbagservice.service.UserbagService;
import utils.ChannelUtil;
import utils.MessageUtil;

/**
 * @ClassName WeaponStartService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class WeaponStartService {

    @Autowired
    private MoneyCaculationService moneyCaculationService;
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private UserbagMapper userbagMapper;

    /**
     * 装备升星
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "iu",status = {ChannelStatus.COMMONSCENE})
    public void upEquipmentStartlevel(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXISTBAG));
            return;
        }
//      开始升星
        if (userbag.getStartlevel() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTOUPSTARTLEVEL));
            return;
        }

        moneyCaculationService.removeMoneyToUser(user, "100000");

        userbag.setStartlevel(userbag.getStartlevel() + 1);
        userbagMapper.updateByPrimaryKeySelective(userbag);
        channel.writeAndFlush(MessageUtil.turnToPacket("升星成功，升星花费100000金币,当前装备星级" + userbag.getStartlevel()));
        userbagService.refreshUserbagInfo(channel, null);
    }

}
