package service.weaponservice.service;

import config.impl.excel.EquipmentResourceLoad;
import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.component.good.Equipment;
import core.component.good.parent.BaseGood;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import pojo.Weaponequipmentbar;
import service.achievementservice.service.AchievementService;
import service.userbagservice.service.UserbagService;
import utils.ChannelUtil;
import utils.MessageUtil;

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
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid());
            wresp += "[装备id:" + weaponequipmentbar.getWid()
                    + "] [装备名称" + equipment.getName()
                    + "] [装备攻击力" + equipment.getAddValue()
                    + "] [装备总耐久" + equipment.getDurability()
                    + "] [当前耐久" + weaponequipmentbar.getDurability()
                    + "] [当前星级" + weaponequipmentbar.getStartlevel() + "]"
                    + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(wresp));
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
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
            return;
        }
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            if (weaponequipmentbar.getWid() == Integer.parseInt(temp[1])) {
                if (weaponequipmentbar.getDurability() < EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getDurability()) {
                    weaponequipmentbar.setDurability(EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getDurability());
                    channel.writeAndFlush(MessageUtil.turnToPacket("[" + EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getName() + "]" + "武器修复成功"));
                    return;
                } else {
                    channel.writeAndFlush(MessageUtil.turnToPacket("[" + EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getName() + "]" + "武器无需修复"));
                    return;
                }
            }
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEQUIPGOOD));
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
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (EquipmentResourceLoad.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
            for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
                if (weaponequipmentbar.getWid() == Integer.parseInt(temp[1])) {
                    Userbag userbag = new Userbag();
                    userbag.setNum(1);
                    userbag.setStartlevel(weaponequipmentbar.getStartlevel());
                    userbag.setDurability(weaponequipmentbar.getDurability());
                    userbag.setId(UUID.randomUUID().toString());
                    userbag.setName(weaponequipmentbar.getUsername());
                    userbag.setTypeof(BaseGood.EQUIPMENT);
                    userbag.setWid(weaponequipmentbar.getWid());
                    user.getUserBag().add(userbag);
                    user.getWeaponequipmentbars().remove(weaponequipmentbar);
                    channel.writeAndFlush(MessageUtil.turnToPacket("你成功卸下" + EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid()).getName()));
                    return;
                }
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEQUIPGOOD));
            return;
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
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
            channel.writeAndFlush(MessageUtil.turnToPacket("请按照ww=背包格子id"));
            return;
        }
//          背包中是否存在该物品
        Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
            return;
        }
//          检查该物品是否为可穿戴装备
        if (!EquipmentResourceLoad.equipmentMap.containsKey(userbag.getWid())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
            return;
        }
//          检查是否穿戴主武器
        if (userbag.getWid() >= GrobalConfig.EQUIPMENT_WEAPON_START && userbag.getWid() < GrobalConfig.EQUIPMENT_WEAPON_END && checkHasCoreEquipment(user)) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.HASCOREEQUIP));
            return;
        }

//          检查是否已穿戴帽子
        if (userbag.getWid() >= GrobalConfig.HAT_WEAPON_START && userbag.getWid() < GrobalConfig.HAT_WEAPON_END && checkHasHatEquipment(user)) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.HASHATEQUIP));
            return;
        }

        Weaponequipmentbar weaponequipmentbar = new Weaponequipmentbar();
        weaponequipmentbar.setId(200);
        weaponequipmentbar.setDurability(userbag.getDurability());
        weaponequipmentbar.setTypeof(BaseGood.EQUIPMENT);
        weaponequipmentbar.setStartlevel(userbag.getStartlevel());
        weaponequipmentbar.setUsername(user.getUsername());
        weaponequipmentbar.setWid(userbag.getWid());
        user.getWeaponequipmentbars().add(weaponequipmentbar);
        user.getUserBag().remove(userbag);
        channel.writeAndFlush(MessageUtil.turnToPacket("[" + EquipmentResourceLoad.equipmentMap.get(userbag.getWid()).getName() + "]" + "该装备穿戴成功"));
//          成就装备等级总和
        achievementService.executeEquipmentStartLevel(user);

        return;
    }

    /**
     * 检查是否为主武器
     *
     * @param user
     * @return
     */
    private boolean checkHasCoreEquipment(User user) {
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            if (weaponequipmentbar.getWid() >= GrobalConfig.EQUIPMENT_WEAPON_START && weaponequipmentbar.getWid() < GrobalConfig.EQUIPMENT_WEAPON_END) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为帽子
     *
     * @param user
     * @return
     */
    private boolean checkHasHatEquipment(User user) {
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            if (weaponequipmentbar.getWid() >= GrobalConfig.HAT_WEAPON_START && weaponequipmentbar.getWid() < GrobalConfig.HAT_WEAPON_END) {
                return true;
            }
        }
        return false;
    }
}
