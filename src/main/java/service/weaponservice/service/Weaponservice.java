package service.weaponservice.service;

import component.good.Equipment;
import component.good.parent.PGood;
import config.MessageConfig;
import context.ProjectContext;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import pojo.Weaponequipmentbar;
import service.achievementservice.service.AchievementExecutor;
import utils.MessageUtil;

import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2019/1/2 16:30
 */
@Component
public class Weaponservice {
    @Autowired
    private AchievementExecutor achievementExecutor;

    //  展示武器栏
    public void queryEquipmentBar(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String wresp = "";
        wresp += System.getProperty("line.separator")
                + "穿上装备按ww=背包id"
                + "卸下按装备wq-装备编号"
                + System.getProperty("line.separator");
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
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


    //  修复武器
    public void fixEquipment(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("-");
        if (!ProjectContext.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
            return;
        }
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            if (weaponequipmentbar.getWid() == Integer.parseInt(temp[1])) {
                if (weaponequipmentbar.getDurability() < ProjectContext.equipmentMap.get(weaponequipmentbar.getWid()).getDurability()) {
                    weaponequipmentbar.setDurability(ProjectContext.equipmentMap.get(weaponequipmentbar.getWid()).getDurability());
                    channel.writeAndFlush(MessageUtil.turnToPacket("[" + ProjectContext.equipmentMap.get(weaponequipmentbar.getWid()).getName() + "]" + "武器修复成功"));
                    return;
                } else {
                    channel.writeAndFlush(MessageUtil.turnToPacket("[" + ProjectContext.equipmentMap.get(weaponequipmentbar.getWid()).getName() + "]" + "武器无需修复"));
                    return;
                }
            }
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEQUIPGOOD));
    }

    //    卸下武器
    public void quitEquipment(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("-");
        if (ProjectContext.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
            for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
                if (weaponequipmentbar.getWid() == Integer.parseInt(temp[1])) {
                    Userbag userbag = new Userbag();
                    userbag.setNum(1);
                    userbag.setStartlevel(weaponequipmentbar.getStartlevel());
                    userbag.setDurability(weaponequipmentbar.getDurability());
                    userbag.setId(UUID.randomUUID().toString());
                    userbag.setName(weaponequipmentbar.getUsername());
                    userbag.setTypeof(PGood.EQUIPMENT);
                    userbag.setWid(weaponequipmentbar.getWid());
                    user.getUserBag().add(userbag);
                    user.getWeaponequipmentbars().remove(weaponequipmentbar);
                    channel.writeAndFlush(MessageUtil.turnToPacket("你成功卸下" + ProjectContext.equipmentMap.get(weaponequipmentbar.getWid()).getName()));
                    return;
                }
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEQUIPGOOD));
            return;
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
        }
    }

    //    穿戴武器
    public void takeEquipment(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("=");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket("请按照ww=背包格子id"));
            return;
        }
//          背包中是否存在该物品
        Userbag userbag = getUserBagById(temp[1], user);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
            return;
        }
//          检查该物品是否为可穿戴装备
        if (!ProjectContext.equipmentMap.containsKey(userbag.getWid())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
            return;
        }
//          检查是否穿戴主武器
        if (userbag.getWid() >= 3000 && userbag.getWid() < 3100 && checkHasCoreEquipment(user)) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.HASCOREEQUIP));
            return;
        }

//          检查是否已穿戴帽子
        if (userbag.getWid() >= 3100 && userbag.getWid() < 3200 && checkHasHatEquipment(user)) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.HASHATEQUIP));
            return;
        }

        Weaponequipmentbar weaponequipmentbar = new Weaponequipmentbar();
        weaponequipmentbar.setId(200);
        weaponequipmentbar.setDurability(userbag.getDurability());
        weaponequipmentbar.setTypeof(PGood.EQUIPMENT);
        weaponequipmentbar.setStartlevel(userbag.getStartlevel());
        weaponequipmentbar.setUsername(user.getUsername());
        weaponequipmentbar.setWid(userbag.getWid());
        user.getWeaponequipmentbars().add(weaponequipmentbar);
        user.getUserBag().remove(userbag);
        channel.writeAndFlush(MessageUtil.turnToPacket("[" + ProjectContext.equipmentMap.get(userbag.getWid()).getName() + "]" + "该装备穿戴成功"));
//          成就装备等级总和
        achievementExecutor.executeEquipmentStartLevel(user);

        return;
    }

//  判断是否为主武器
    private boolean checkHasCoreEquipment(User user) {
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            if (weaponequipmentbar.getWid() >= 3000 && weaponequipmentbar.getWid() < 3100) {
                return true;
            }
        }
        return false;
    }

//  判断是否为武器饰品
    private boolean checkHasHatEquipment(User user) {
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            if (weaponequipmentbar.getWid() >= 3100 && weaponequipmentbar.getWid() < 3200) {
                return true;
            }
        }
        return false;
    }

//  通过id拿到userbag
    private Userbag getUserBagById(String id, User user) {
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getId().equals(id)) {
                return userbag;
            }
        }
        return null;
    }
}
