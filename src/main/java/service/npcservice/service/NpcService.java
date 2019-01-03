package service.npcservice.service;

import core.component.good.Equipment;
import core.component.good.parent.PGood;
import service.sceneservice.entity.Scene;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.context.ProjectContext;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import service.achievementservice.entity.Achievement;
import service.achievementservice.service.AchievementService;
import service.caculationservice.service.UserbagCaculationService;
import service.npcservice.entity.NPC;
import utils.MessageUtil;

import java.util.List;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2019/1/2 17:00
 */
@Component
public class NpcService {
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserbagCaculationService userbagCaculationService;

    public void talkMethod(Channel channel, String msg) {
        String[] temp = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
        } else {
            List<NPC> npcs = ProjectContext.sceneMap.get(ProjectContext.session2UserIds.get(channel).getPos())
                    .getNpcs();
            for (NPC npc : npcs) {
                if (npc.getName().equals(temp[1])) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(npc.getTalk()));
//                      人物任务触发
                    for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
                        Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
                        if (achievementprocess.getType().equals(Achievement.TALKTONPC)) {
                            achievementService.executeTalkNPC(achievementprocess, user, achievement, npc);
                        }
                    }
                    return;
                }
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDNPC));
        }
    }

    public void getEquipFromNpc(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = ProjectContext.session2UserIds.get(channel);
        Scene scene = ProjectContext.sceneMap.get(user.getPos());
        NPC npc = null;
        for (NPC npcT : scene.getNpcs()) {
            if (npcT.getName().equals(temp[1])) {
                npc = npcT;
            }
        }
        if (npc == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDNPC));
            return;
        }
        if (npc.getGetGoods().equals(GrobalConfig.NULL)) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEXACHANGEFORNPC));
            return;
        }

//      扣去兑换品
        String[] target = npc.getGetTarget().split(":");
        Userbag userbagTarget = null;
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getWid().equals(Integer.parseInt(target[0]))) {
                if (userbag.getNum() < Integer.parseInt(target[1])) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHCHANGEGOOD));
                    return;
                }
                userbagTarget = userbag;
            }
        }
        userbagCaculationService.removeUserbagFromUser(user, userbagTarget, Integer.parseInt(target[1]));

//        新增武器
        String[] getGoods = npc.getGetGoods().split("-");
        for(String getGood : getGoods){
            Equipment equipment = ProjectContext.equipmentMap.get(Integer.parseInt(getGood));
            Userbag userbag1 = new Userbag();
            userbag1.setWid(equipment.getId());
            userbag1.setNum(1);
            userbag1.setStartlevel(equipment.getStartLevel());
            userbag1.setId(UUID.randomUUID().toString());
            userbag1.setDurability(equipment.getDurability());
            userbag1.setName(user.getUsername());
            userbag1.setTypeof(PGood.EQUIPMENT);
            userbagCaculationService.addUserBagForUser(user, userbag1);
        }
    }
}
