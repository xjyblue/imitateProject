package service.npcservice.service;

import config.impl.excel.AchievementResourceLoad;
import config.impl.excel.EquipmentResourceLoad;
import config.impl.excel.SceneResourceLoad;
import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.component.good.Equipment;
import core.component.good.parent.BaseGood;
import core.packet.ServerPacket;
import service.npcservice.entity.Npc;
import service.sceneservice.entity.Scene;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import service.achievementservice.entity.Achievement;
import service.achievementservice.service.AchievementService;
import service.caculationservice.service.UserbagCaculationService;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.List;
import java.util.UUID;

/**
 * @ClassName NpcService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class NpcService {
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserbagCaculationService userbagCaculationService;

    /**
     * 和npc交流
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "npcTalk", status = {ChannelStatus.COMMONSCENE})
    public void talkMethod(Channel channel, String msg) {
        try {
            String[] temp = msg.split("=");
            User user = ChannelUtil.channelToUserMap.get(channel);
            if (temp.length != GrobalConfig.TWO) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.ERRORORDER);
                MessageUtil.sendMessage(channel, builder.build());
            } else {
                List<Npc> npcs = SceneResourceLoad.sceneMap.get(ChannelUtil.channelToUserMap.get(channel).getPos())
                        .getNpcs();
                for (Npc npc : npcs) {
                    if (npc.getName().equals(temp[1])) {
                        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                        builder.setData(npc.getTalk());
                        MessageUtil.sendMessage(channel, builder.build());
//                      人物任务触发
                        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
                            Achievement achievement = AchievementResourceLoad.achievementMap.get(achievementprocess.getAchievementid());
                            if (achievementprocess.getType().equals(Achievement.TALKTONPC)) {
                                achievementService.executeTalkNPC(achievementprocess, user, achievement, npc);
                            }
                        }
                        return;
                    }
                }
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NOFOUNDNPC);
                MessageUtil.sendMessage(channel, builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向npc兑换装备
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "npcGet", status = {ChannelStatus.COMMONSCENE})
    public void getEquipFromNpc(Channel channel, String msg) {
        try {
            String[] temp = msg.split("=");
            if (temp.length != GrobalConfig.TWO) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.ERRORORDER);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
            User user = ChannelUtil.channelToUserMap.get(channel);
            Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
            Npc npc = null;
            for (Npc npcT : scene.getNpcs()) {
                if (npcT.getName().equals(temp[1])) {
                    npc = npcT;
                }
            }
            if (npc == null) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NOFOUNDNPC);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
            if (npc.getGetGoods().equals(GrobalConfig.NULL)) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NOEXACHANGEFORNPC);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }

//      扣去兑换品
            String[] target = npc.getGetTarget().split(":");
            Userbag userbagTarget = null;
            for (Userbag userbag : user.getUserBag()) {
                if (userbag.getWid().equals(Integer.parseInt(target[0]))) {
                    if (userbag.getNum() < Integer.parseInt(target[1])) {
                        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                        builder.setData(MessageConfig.NOENOUGHCHANGEGOOD + "需要的物品数量为:" + Integer.parseInt(target[1]));
                        MessageUtil.sendMessage(channel, builder.build());
                        return;
                    }
                    userbagTarget = userbag;
                }
            }
            userbagCaculationService.removeUserbagFromUser(user, userbagTarget, Integer.parseInt(target[1]));

//        新增武器
            String[] getGoods = npc.getGetGoods().split("-");
            for (String getGood : getGoods) {
                Equipment equipment = EquipmentResourceLoad.equipmentMap.get(Integer.parseInt(getGood));
                Userbag userbag1 = new Userbag();
                userbag1.setWid(equipment.getId());
                userbag1.setNum(1);
                userbag1.setStartlevel(equipment.getStartLevel());
                userbag1.setId(UUID.randomUUID().toString());
                userbag1.setDurability(equipment.getDurability());
                userbag1.setName(user.getUsername());
                userbag1.setTypeof(BaseGood.EQUIPMENT);
                userbagCaculationService.addUserBagForUser(user, userbag1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
