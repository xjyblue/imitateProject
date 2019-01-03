package service.weaponstartservice.service;

import core.config.MessageConfig;
import core.context.ProjectContext;
import io.netty.channel.Channel;
import mapper.UserbagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.caculationservice.service.MoneyCaculationService;
import service.userbagservice.service.UserbagService;
import utils.MessageUtil;

/**
 * Description ：nettySpringServer  装备星级系统
 * Created by xiaojianyu on 2019/1/2 17:37
 */
@Component
public class WeaponStartService {

    @Autowired
    private MoneyCaculationService moneyCaculationService;
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private UserbagMapper userbagMapper;

    //  装备升星，先不注入后期注入
    public void upEquipmentStartlevel(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = ProjectContext.session2UserIds.get(channel);
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
        userbagService.refreshUserbagInfo(channel,null);
    }

}
