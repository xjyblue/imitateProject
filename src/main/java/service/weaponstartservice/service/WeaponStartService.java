package service.weaponstartservice.service;

import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.packet.ServerPacket;
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
    @Order(orderMsg = "iu", status = {ChannelStatus.COMMONSCENE})
    public void upEquipmentStartlevel(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.GOODNOEXISTBAG);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      开始升星
        if (userbag.getStartlevel() == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOTOUPSTARTLEVEL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        if (!moneyCaculationService.removeMoneyToUser(user, "100000")) {
            return;
        }

        userbag.setStartlevel(userbag.getStartlevel() + 1);
        userbagMapper.updateByPrimaryKeySelective(userbag);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("升星成功，升星花费100000金币,当前装备星级" + userbag.getStartlevel());
        MessageUtil.sendMessage(channel, builder.build());
        userbagService.refreshUserbagInfo(channel, null);
    }

}
