package service.weaponservice.service;

import config.impl.excel.EquipmentResourceLoad;
import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.component.good.Equipment;
import core.component.good.parent.BaseGood;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import mapper.UserbagMapper;
import mapper.WeaponequipmentbarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import pojo.Weaponequipmentbar;
import service.achievementservice.service.AchievementService;
import service.caculationservice.service.HpCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import service.userbagservice.service.UserbagService;
import service.weaponservice.entity.WeaponUtil;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.Map;
import java.util.UUID;

/**
 * @ClassName Weaponservice
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class Weaponservice {
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private WeaponequipmentbarMapper weaponequipmentbarMapper;
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private UserbagMapper userbagMapper;

    /**
     * 展示武器栏
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qw", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE})
    public void queryEquipmentBar(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String wresp = "";
        wresp += System.getProperty("line.separator")
                + "穿上装备按ww=背包id"
                + "卸下按装备wq=装备编号"
                + System.getProperty("line.separator");
        for (Map.Entry<Integer, Weaponequipmentbar> entry : user.getWeaponequipmentbarMap().entrySet()) {
            Weaponequipmentbar weaponequipmentbar = entry.getValue();
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid());
            wresp += "[装备id:" + weaponequipmentbar.getWid()
                    + "] [装备名称" + equipment.getName()
                    + "] [装备攻击力" + equipment.getAddValue()
                    + "] [装备总耐久" + equipment.getDurability()
                    + "] [当前耐久" + weaponequipmentbar.getDurability()
                    + "] [当前星级" + weaponequipmentbar.getStartlevel() + "]"
                    + System.getProperty("line.separator");
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(wresp);
        MessageUtil.sendMessage(channel, builder.build());
    }


    /**
     * 修复武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "fix", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE})
    public void fixEquipment(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (!EquipmentResourceLoad.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.GOODNOEXIST);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        for (Map.Entry<Integer, Weaponequipmentbar> entry : user.getWeaponequipmentbarMap().entrySet()) {
            Weaponequipmentbar weaponequipmentbar = entry.getValue();
            if (weaponequipmentbar.getWid() == Integer.parseInt(temp[1])) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                if (weaponequipmentbar.getDurability() < EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getDurability()) {
                    weaponequipmentbar.setDurability(EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getDurability());
                    builder.setData("[" + EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getName() + "]" + "武器修复成功");
                    MessageUtil.sendMessage(channel, builder.build());

//                  数据库更新 修复过后的耐久度
                    weaponequipmentbarMapper.updateByPrimaryKeySelective(weaponequipmentbar);
                    return;
                } else {
                    builder.setData("[" + EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getName() + "]" + "武器无需修复");
                    MessageUtil.sendMessage(channel, builder.build());
                    return;
                }
            }
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.NOEQUIPGOOD);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 卸下武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "wq", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE})
    public void quitEquipment(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (EquipmentResourceLoad.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
            for (Map.Entry<Integer, Weaponequipmentbar> entry : user.getWeaponequipmentbarMap().entrySet()) {
                Weaponequipmentbar weaponequipmentbar = entry.getValue();
                if (weaponequipmentbar.getWid() == Integer.parseInt(temp[1])) {
                    Userbag userbag = new Userbag();
                    userbag.setNum(1);
                    userbag.setStartlevel(weaponequipmentbar.getStartlevel());
                    userbag.setDurability(weaponequipmentbar.getDurability());
                    userbag.setId(UUID.randomUUID().toString());
                    userbag.setName(weaponequipmentbar.getUsername());
                    userbag.setTypeof(BaseGood.EQUIPMENT);
                    userbag.setWid(weaponequipmentbar.getWid());
//                  jvm内存更新
                    user.getUserBag().add(userbag);
                    user.getWeaponequipmentbarMap().remove(weaponequipmentbar.getWpos());
//                  数据库更新
                    userbagMapper.insertSelective(userbag);
                    weaponequipmentbarMapper.deleteByPrimaryKey(weaponequipmentbar.getId());

//                  处理用户脱下装备血量蓝量上限更新
                    Equipment equipment = EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid());
                    hpCaculationService.subUserHpByTakeOffEquip(user, equipment);

                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData("你成功卸下" + EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getName());
                    MessageUtil.sendMessage(channel, builder.build());
                    return;
                }
            }
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOEQUIPGOOD);
            MessageUtil.sendMessage(channel, builder.build());
        } else {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.GOODNOEXIST);
            MessageUtil.sendMessage(channel, builder.build());
        }
    }

    /**
     * 穿戴武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ww", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE})
    public void takeEquipment(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("请按照ww=背包格子id");
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//          背包中是否存在该物品
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOUSERBAGID);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//          检查该物品是否为可穿戴装备
        if (!EquipmentResourceLoad.equipmentMap.containsKey(userbag.getWid())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.GOODNOEXIST);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        Integer pos = WeaponUtil.getWeaponPos(userbag);
//          检查是否穿戴主武器
        if (pos.equals(WeaponUtil.WEAPON_ONE) && user.getWeaponequipmentbarMap().containsKey(pos)) {
//          替换操作
            exchangeEquip(channel, user, userbag, pos);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.HASCOREEQUIP);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//          检查是否已穿戴帽子
        if (pos.equals(WeaponUtil.WEAPON_TWO) && user.getWeaponequipmentbarMap().containsKey(pos)) {
//          替换操作
            exchangeEquip(channel, user, userbag, pos);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.HASHATEQUIP);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        exchangeEquip(channel, user, userbag, pos);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("[" + EquipmentResourceLoad.equipmentMap.get(userbag.getWid()).getName() + "]" + "该装备穿戴成功");
        MessageUtil.sendMessage(channel, builder.build());
//      成就装备等级总和
        achievementService.executeEquipmentStartLevel(user);
        return;
    }

    private void exchangeEquip(Channel channel, User user, Userbag userbag, Integer pos) {
        if (user.getWeaponequipmentbarMap().containsKey(pos)) {
//          旧的拿下来
            Weaponequipmentbar weaponequipmentbar = user.getWeaponequipmentbarMap().get(pos);
            Userbag userbagNew = new Userbag();
            userbagNew.setNum(1);
            userbagNew.setTypeof(weaponequipmentbar.getTypeof());
            userbagNew.setDurability(weaponequipmentbar.getDurability());
            userbagNew.setId(UUID.randomUUID().toString());
            userbagNew.setName(user.getUsername());
            userbagNew.setWid(weaponequipmentbar.getWid());
            userbagNew.setStartlevel(weaponequipmentbar.getStartlevel());
//          jvm内存更新
            user.getUserBag().add(userbagNew);
//          数据库更新
            userbagMapper.insertSelective(userbagNew);
            weaponequipmentbarMapper.deleteByPrimaryKey(weaponequipmentbar.getId());
        }
//      新的顶上去
        Weaponequipmentbar weaponequipmentbar = new Weaponequipmentbar();
        weaponequipmentbar.setDurability(userbag.getDurability());
        weaponequipmentbar.setTypeof(BaseGood.EQUIPMENT);
        weaponequipmentbar.setStartlevel(userbag.getStartlevel());
        weaponequipmentbar.setUsername(user.getUsername());
        weaponequipmentbar.setWid(userbag.getWid());
        weaponequipmentbar.setWpos(pos);
//      jvm内存更新
        user.getWeaponequipmentbarMap().put(pos, weaponequipmentbar);
        user.getUserBag().remove(userbag);
//      数据库更新
        weaponequipmentbarMapper.insertSelective(weaponequipmentbar);
        userbagMapper.deleteByPrimaryKey(userbag.getId());

//      刷新背包
        userbagService.refreshUserbagInfo(channel, null);
//      刷新武器栏
        queryEquipmentBar(channel, null);
    }


}
