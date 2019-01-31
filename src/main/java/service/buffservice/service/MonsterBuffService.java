package service.buffservice.service;

import config.impl.excel.BuffResourceLoad;
import core.component.monster.Monster;
import core.config.GrobalConfig;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.broadcastservice.service.BroadcastService;
import service.buffservice.entity.Buff;
import service.buffservice.entity.BuffConstant;
import service.caculationservice.service.HpCaculationService;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.Map;

/**
 * @ClassName MonsterBuffService
 * @Description 心跳持续更新怪物buff产生效果
 * @Author xiaojianyu
 * @Date 2019/1/7 11:58
 * @Version 1.0
 **/
@Component
public class MonsterBuffService {
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private BroadcastService broadcastService;

    /**
     * 刷新怪物buff
     *
     * @param monster
     */
    public void bossBuffRefresh(Monster monster, String teamId) {
        if (monster != null && monster.getBuffRefreshTime() < System.currentTimeMillis()) {
            monster.setBuffRefreshTime(System.currentTimeMillis() + 1000);
        } else {
            return;
        }

        if (monster.getBufMap().containsKey(BuffConstant.POISONINGBUFF) && monster.getBufMap().get(BuffConstant.POISONINGBUFF) != GrobalConfig.POISONINGBUFF_DEFAULTVALUE) {
            Long endTime = monster.getMonsterBuffEndTimeMap().get(BuffConstant.POISONINGBUFF);
            if (System.currentTimeMillis() < endTime && !monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
                Buff buff = BuffResourceLoad.buffMap.get(monster.getBufMap().get(BuffConstant.POISONINGBUFF));
                hpCaculationService.subMonsterHp(monster, Integer.parseInt(buff.getAddSecondValue()));
//               处理中毒扣死
                if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
                    monster.setValueOfLife(GrobalConfig.MINVALUE);
                    monster.setStatus(GrobalConfig.DEAD);
                    monster.getBufMap().put(BuffConstant.POISONINGBUFF, 2000);
                }
//              怪物中毒将buff推送给所有玩家
                ServerPacket.MonsterbufResp.Builder builder = ServerPacket.MonsterbufResp.newBuilder();
                builder.setData("怪物中毒掉血[" + buff.getAddSecondValue() + "]+怪物剩余血量为:" + monster.getValueOfLife());
                broadcastService.sendMessageToAll(teamId, builder.build());
            } else {
                monster.getBufMap().put(BuffConstant.POISONINGBUFF, 2000);
            }
        }
    }

    public void monsterBuffRefresh(Monster monster, Map<String, User> map) {
        if (monster != null && monster.getBuffRefreshTime() < System.currentTimeMillis()) {
            monster.setBuffRefreshTime(System.currentTimeMillis() + 1000);
        } else {
            return;
        }

        if (monster.getBufMap().containsKey(BuffConstant.POISONINGBUFF) && monster.getBufMap().get(BuffConstant.POISONINGBUFF) != GrobalConfig.POISONINGBUFF_DEFAULTVALUE) {
            Long endTime = monster.getMonsterBuffEndTimeMap().get(BuffConstant.POISONINGBUFF);
            if (System.currentTimeMillis() < endTime && !monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
                Buff buff = BuffResourceLoad.buffMap.get(monster.getBufMap().get(BuffConstant.POISONINGBUFF));
                hpCaculationService.subMonsterHp(monster, Integer.parseInt(buff.getAddSecondValue()));
//               处理中毒扣死
                if (Integer.parseInt(monster.getValueOfLife()) < GrobalConfig.ZERO) {
                    monster.setValueOfLife(GrobalConfig.MINVALUE);
                    monster.setStatus(GrobalConfig.DEAD);
                    monster.getBufMap().put(BuffConstant.POISONINGBUFF, 2000);
                }
                for (Map.Entry<String, User> entry : map.entrySet()) {
                    Channel channel = ChannelUtil.userToChannelMap.get(entry.getValue());
                    ServerPacket.MonsterbufResp.Builder builder = ServerPacket.MonsterbufResp.newBuilder();
                    builder.setData("怪物中毒掉血[" + buff.getAddSecondValue() + "]+怪物剩余血量为:" + monster.getValueOfLife());
                    MessageUtil.sendMessage(channel, builder.build());
                }
            } else {
                monster.getBufMap().put(BuffConstant.POISONINGBUFF, 2000);
            }
        }

    }

}
