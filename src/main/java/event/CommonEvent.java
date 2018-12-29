package event;

import achievement.AchievementExecutor;
import component.*;
import component.parent.PGood;
import config.BuffConfig;
import config.MessageConfig;
import io.netty.channel.Channel;
import context.ProjectContext;
import mapper.UserbagMapper;
import mapper.WeaponequipmentbarMapper;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import pojo.Weaponequipmentbar;
import utils.LevelUtil;
import utils.MessageUtil;
import utils.UserbagUtil;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

@Component("commonEvent")
public class CommonEvent {
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private WeaponequipmentbarMapper weaponequipmentbarMapper;
    @Autowired
    private UserbagMapper userbagMapper;
    //  展示背包
    @Order(orderMsg = "qb")
    public void refreshUserbagInfo(Channel channel, String msg) {
        UserbagUtil.refreshUserbagInfo(channel);
    }

    //  使用背包
    @Order(orderMsg = "ub-")
    public void userUserbag(Channel channel, String msg) {
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
                if (user.getBuffMap().get(BuffConfig.MPBUFF) == mpMedicine.getId()) {
                    ProjectContext.userBuffEndTime.get(user).put(BuffConfig.MPBUFF, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                } else {
                    user.getBuffMap().put(BuffConfig.MPBUFF, mpMedicine.getId());
                    ProjectContext.userBuffEndTime.get(user).put(BuffConfig.MPBUFF, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                }
                user.getBuffMap().put(BuffConfig.MPBUFF, mpMedicine.getId());
            }
        } else if (ProjectContext.hpMedicineMap.containsKey(key)) {
//              处理使用红药的过程
            HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(key);
            if (ProjectContext.userBuffEndTime.get(user).get(BuffConfig.TREATMENTBUFF) < System.currentTimeMillis()) {
                user.getBuffMap().put(BuffConfig.TREATMENTBUFF, hpMedicine.getId());
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

    @Order(orderMsg = "qw")
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

    @Order(orderMsg = "fix")
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

    @Order(orderMsg = "wq-")
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

    @Order(orderMsg = "ww")
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


    @Order(orderMsg = "aoi")
    public void aoiMethod(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String allStatus = System.getProperty("line.separator")
                + "玩家[" + user.getUsername()
                + "] 玩家的状态[" + user.getStatus()
                + "] 玩家的经验[" + user.getExperience()
                + "] 玩家的等级[" + LevelUtil.getLevelByExperience(user.getExperience())
                + "] 处于[" + ProjectContext.sceneMap.get(user.getPos()).getName()
                + "] 玩家的HP量：[" + user.getHp()
                + "] 玩家的HP上限 [" + LevelUtil.getMaxHp(user)
                + "] 玩家的MP量：[" + user.getMp()
                + "] 玩家的MP上限: [" + LevelUtil.getMaxMp(user)
                + "] 玩家的金币：[" + user.getMoney()
                + "]" + System.getProperty("line.separator");
        for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
            if (!user.getUsername().equals(entry.getValue().getUsername()) && user.getPos().equals(entry.getValue().getPos())) {
                allStatus += "其他玩家" + entry.getValue().getUsername() + "---" + entry.getValue().getStatus() + System.getProperty("line.separator");
            }
        }
        for (NPC npc : ProjectContext.sceneMap.get(user.getPos()).getNpcs()) {
            allStatus += "NPC:" + npc.getName() + " 状态[" + npc.getStatus() + "]" + System.getProperty("line.separator");
        }
        for (Monster monster : ProjectContext.sceneMap.get(user.getPos()).getMonsters()) {
            allStatus += "怪物有" + monster.getName() + " 生命值[" + monster.getValueOfLife()
                    + "] 攻击技能为[" + monster.getMonsterSkillList().get(0).getSkillName()
                    + "] 伤害为：[" + monster.getMonsterSkillList().get(0).getDamage() + "]";
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(allStatus));
    }

    @Order(orderMsg = "arrange-b")
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

    private boolean checkHasCoreEquipment(User user) {
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            if (weaponequipmentbar.getWid() >= 3000 && weaponequipmentbar.getWid() < 3100) {
                return true;
            }
        }
        return false;
    }

    private boolean checkHasHatEquipment(User user) {
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            if (weaponequipmentbar.getWid() >= 3100 && weaponequipmentbar.getWid() < 3200) {
                return true;
            }
        }
        return false;
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

    private Userbag getUserBagById(String id, User user) {
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getId().equals(id)) {
                return userbag;
            }
        }
        return null;
    }
}
