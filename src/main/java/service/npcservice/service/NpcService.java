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
import mapper.AchievementprocessMapper;
import service.achievementservice.entity.AchievementConfig;
import service.achievementservice.util.AchievementUtil;
import service.npcservice.entity.Npc;
import service.rewardservice.service.RewardService;
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
import java.util.Map;
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
    @Autowired
    private AchievementprocessMapper achievementprocessMapper;
    @Autowired
    private RewardService rewardService;

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
                return;
            }
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
                        if (achievementprocess.getType().equals(AchievementConfig.TALKTONPC)) {
                            achievementService.executeTalkNPC(achievementprocess, user, achievement, npc);
                        }
                    }
                    return;
                }
            }
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOFOUNDNPC);
            MessageUtil.sendMessage(channel, builder.build());

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
            Npc npc = getNpcByUserPos(channel, temp[1]);
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

    /**
     * 查看npc有哪些可接受的任务
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "npcTaskLook", status = {ChannelStatus.COMMONSCENE})
    public void showTaskFromNpc(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Npc npc = getNpcByUserPos(channel, temp[1]);
        if (npc == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOFOUNDNPC);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (npc.getAchievementMap().size() == 0) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NPC_NO_TASK);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
        String resp = "";
        for (Map.Entry<Integer, Achievement> entry : npc.getAchievementMap().entrySet()) {
            Achievement achievement = entry.getValue();
            int achievementStatus = checkAchievementStatus(achievement, user);
            if (achievementStatus == AchievementConfig.CAN_ACCEPT) {
                resp += "[未接受]";
            } else if (achievementStatus == AchievementConfig.DOING_TASK) {
                resp += "[进行中]";
            } else if (achievementStatus == AchievementConfig.COMPLETE_TASK) {
                resp += "[已完成,未交付]";
            } else if (achievementStatus == AchievementConfig.COMPLETE_AND_GIVE_TASK) {
                resp += "[已完成,已交付]";
            }
            resp += "[任务id为:" + achievement.getAchievementId() + "] [任务名称:" + achievement.getName() + "]" + System.getProperty("line.separator");
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(resp);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 接受npc的任务
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "npcTaskReceive", status = {ChannelStatus.COMMONSCENE})
    public void npcTaskReceive(Channel channel, String msg) {
        String[] temp = msg.split("=");
//      指令校验
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      npc校验
        Npc npc = getNpcByUserPos(channel, temp[1]);
        if (npc == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOFOUNDNPC);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      任务编号校验
        Map<Integer, Achievement> achievementMap = npc.getAchievementMap();
        if (!achievementMap.containsKey(Integer.parseInt(temp[2]))) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_FOUND_TASK);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
//      重复接受处理
        Achievementprocess achievementprocess = getAchievementProcessByUser(user, temp[2]);
        if (achievementprocess != null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_ACCEPT_REPEAT);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      接受任务
        Achievement achievement = achievementMap.get(Integer.parseInt(temp[2]));
        achievementprocess = new Achievementprocess();
        achievementprocess.setIffinish(AchievementConfig.DOING_TASK);
        achievementprocess.setType(achievement.getType());
        achievementprocess.setAchievementid(achievement.getAchievementId());
        achievementprocess.setUsername(user.getUsername());
        achievementprocess.setProcesss(achievement.getBegin());
//      插入jvm
        user.getAchievementprocesses().add(achievementprocess);
//      插入数据库
        achievementprocessMapper.insert(achievementprocess);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.SUCCESS_ACCEPT_TASK);
        MessageUtil.sendMessage(channel, builder.build());
//      刷新用户任务栏
        AchievementUtil.refreshAchievementInfo(user);
    }

    /**
     * 交付任务给npc获得奖励
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "npcTaskReward", status = {ChannelStatus.COMMONSCENE})
    public void npcTaskReward(Channel channel, String msg) {
        String[] temp = msg.split("=");
//      指令校验
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      npc校验
        Npc npc = getNpcByUserPos(channel, temp[1]);
        if (npc == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOFOUNDNPC);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
//      任务编号校验
        Map<Integer, Achievement> achievementMap = npc.getAchievementMap();
        if (!achievementMap.containsKey(Integer.parseInt(temp[2]))) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_FOUND_TASK);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Achievementprocess achievementprocess = getAchievementProcessByUser(user, temp[2]);

//      获得奖励条件校验
        if (!achievementprocess.getIffinish().equals(AchievementConfig.COMPLETE_TASK)) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_REPEAT_REWARD_TASK);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      执行奖励
        Achievement achievement = achievementMap.get(Integer.parseInt(temp[2]));
//      此处设为false才能触发执行奖励，奖励完后重置为true保持数据一致性
        achievement.setGetTask(false);
        rewardService.sloveAchievementReward(achievement, user);
//      更新jvm
        achievementprocess.setIffinish(AchievementConfig.COMPLETE_AND_GIVE_TASK);
//      更新数据库
        achievementprocessMapper.updateByPrimaryKeySelective(achievementprocess);

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.SUCCESS_REWARD_TASK);
        MessageUtil.sendMessage(channel, builder.build());
//      刷新用户任务栏
        AchievementUtil.refreshAchievementInfo(user);
    }

    private Achievementprocess getAchievementProcessByUser(User user, String id) {
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            if (achievementprocess.getAchievementid().equals(Integer.parseInt(id))) {
                return achievementprocess;
            }
        }
        return null;
    }


    /**
     * 校验是否有该任务
     *
     * @param achievement
     * @param user
     * @return
     */
    private int checkAchievementStatus(Achievement achievement, User user) {
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            if (achievementprocess.getAchievementid().equals(achievement.getAchievementId())) {
//              返回任务的初始状态
                return achievementprocess.getIffinish();
            }
        }
//      返回可接受的状态
        return AchievementConfig.CAN_ACCEPT;
    }


    /**
     * 通过用户的位置去获取当前场景的npc
     *
     * @param channel
     * @return
     */
    private Npc getNpcByUserPos(Channel channel, String npcName) {
        Npc npc = null;
        User user = ChannelUtil.channelToUserMap.get(channel);
        Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
        for (Npc npcT : scene.getNpcs()) {
            if (npcT.getName().equals(npcName)) {
                npc = npcT;
            }
        }
        return npc;
    }
}
