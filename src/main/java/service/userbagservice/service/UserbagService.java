package service.userbagservice.service;

import component.good.HpMedicine;
import component.good.MpMedicine;
import component.good.parent.PGood;
import config.MessageConfig;
import context.ProjectContext;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.buffservice.entity.BuffConstant;
import utils.MessageUtil;
import utils.UserbagUtil;

import java.math.BigInteger;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2019/1/2 16:13
 */
@Component
public class UserbagService {

//  展示背包
    public void refreshUserbagInfo(Channel channel, String msg) {
        UserbagUtil.refreshUserbagInfo(channel);
    }

//  整理背包物品
    public void arrangeUserBag(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        for (Userbag userbag : user.getUserBag()) {
            for (Userbag userbag2 : user.getUserBag()) {
                if (userbag != userbag2 && userbag.getWid() == userbag2.getWid() && !userbag.getTypeof().equals(PGood.EQUIPMENT)) {
//                      相同物品不同格子叠加
                    userbag.setNum(userbag.getNum() + userbag2.getNum());
                }
            }
        }
    }

//  使用背包物品
    public void useUserbag(Channel channel, String msg) {
        String temp[] = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
        Integer key = Integer.parseInt(temp[1]);

        if (!checkGoodInUserbag(user, key)) {
            channel.writeAndFlush(MessageUtil.turnToPacket("背包中无此物品"));
            return;
        }

        if (ProjectContext.mpMedicineMap.containsKey(key)) {
            MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(key);

//              处理用户背包
            sloveUserbag(user, channel, key);

            if (mpMedicine.isImmediate()) {
                BigInteger userMp = new BigInteger(user.getMp());
                userMp = userMp.add(new BigInteger(mpMedicine.getReplyValue()));
                BigInteger maxMp = new BigInteger("10000");
                if (userMp.compareTo(maxMp) >= 0) {
                    user.setMp(maxMp.toString());
                } else {
                    user.addMp(mpMedicine.getReplyValue());
                }
            } else {
                if (user.getBuffMap().get(BuffConstant.MPBUFF) == mpMedicine.getId()) {
                    ProjectContext.userBuffEndTime.get(user).put(BuffConstant.MPBUFF, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                } else {
                    user.getBuffMap().put(BuffConstant.MPBUFF, mpMedicine.getId());
                    ProjectContext.userBuffEndTime.get(user).put(BuffConstant.MPBUFF, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                }
                user.getBuffMap().put(BuffConstant.MPBUFF, mpMedicine.getId());
            }
        } else if (ProjectContext.hpMedicineMap.containsKey(key)) {
//              处理使用红药的过程
            HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(key);
            if (ProjectContext.userBuffEndTime.get(user).get(BuffConstant.TREATMENTBUFF) < System.currentTimeMillis()) {
                user.getBuffMap().put(BuffConstant.TREATMENTBUFF, hpMedicine.getId());
                channel.writeAndFlush(MessageUtil.turnToPacket("你使用了" + hpMedicine.getName() + "回复红量：" + hpMedicine.getReplyValue()));
//                  处理用户背包
                sloveUserbag(user, channel, key);
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
                return;
            }
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
        }
    }

    private boolean checkGoodInUserbag(User user, Integer key) {
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getWid().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private void sloveUserbag(User user, Channel channel, Integer key) {
        Userbag userbagNow = null;
        Userbag userbagRemove = null;
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getWid().equals(key)) {
                userbagNow = userbag;
                userbag.setNum(userbag.getNum() - 1);
                if (userbag.getNum() == 0) {
                    userbagRemove = userbag;
                }
            }
        }

        if (userbagRemove != null) {
            user.getUserBag().remove(userbagRemove);
        }

        if (userbagNow == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXISTBAG));
            return;
        }
    }
}
