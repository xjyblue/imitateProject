package event;

import component.Equipment;
import component.parent.Good;
import config.MessageConfig;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import pojo.Weaponequipmentbar;
import utils.MessageUtil;
import component.MpMedicine;

import java.math.BigInteger;

@Component("commonEvent")
public class CommonEvent {

    public void common(Channel channel, String msg) {
        if (msg.equals("b")) {
            User user = NettyMemory.session2UserIds.get(channel);
            String bagResp = System.getProperty("line.separator")
                    + "按b-物品编号使用蓝药"
                    + "-------按ww-物品编号装备武器";
            for (Userbag userbag : user.getUserBag()) {
                if (userbag.getTypeof().equals(Good.MPMEDICINE)) {
                    MpMedicine mpMedicine = NettyMemory.mpMedicineMap.get(userbag.getWid());
                    bagResp += System.getProperty("line.separator")
                            + "物品id" + mpMedicine.getId()
                            + "----药品恢复蓝量:" + mpMedicine.getReplyValue();
                    if (!mpMedicine.isImmediate()) {
                        bagResp += "----每秒恢复" + mpMedicine.getSecondValue() + "----持续" + mpMedicine.getKeepTime() + "秒";
                    } else {
                        bagResp += "----即时回复";
                    }
                    bagResp += "----数量:" + userbag.getNum();
                } else if (userbag.getTypeof().equals(Good.EQUIPMENT)) {
                    Equipment equipment = NettyMemory.equipmentMap.get(userbag.getWid());
                    bagResp += System.getProperty("line.separator")
                            + "物品id:" + equipment.getId()
                            + "----武器当前耐久度:" + userbag.getDurability()
                            + "----武器名称:" + equipment.getName()
                            + "----武器攻击力加成" + equipment.getAddValue()
                            + "----武器数量:" + userbag.getNum();
                }
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(bagResp));
        }
        if (msg.startsWith("b-")) {
            String temp[] = msg.split("-");
            User user = NettyMemory.session2UserIds.get(channel);
            if (NettyMemory.mpMedicineMap.containsKey(Integer.parseInt(temp[1]))) {
                MpMedicine mpMedicine = NettyMemory.mpMedicineMap.get(Integer.parseInt(temp[1]));
                Userbag userbagNow = null;
                Userbag userbagRemove = null;
                for (Userbag userbag : user.getUserBag()) {
                    if (userbag.getWid() == Integer.parseInt(temp[1])) {
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
                    if (user.getBufferMap().get("mpBuff") == mpMedicine.getId()) {
                        NettyMemory.buffEndTime.get(user).put("mpBuff",(System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                    } else {
                        user.getBufferMap().put("mpBuff",mpMedicine.getId());
                        NettyMemory.buffEndTime.get(user).put("mpBuff",(System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
//                        if (NettyMemory.mpEndTime.containsKey(user)) {
//                            NettyMemory.mpEndTime.remove(user);
//                        }
                    }
                    user.getBufferMap().put("mpBuff",mpMedicine.getId());
                }
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
            }
        }
        if (msg.equals("w")) {
            User user = NettyMemory.session2UserIds.get(channel);
            String wresp = "";
            wresp += System.getProperty("line.separator")
                    + "穿上装备按ww-装备编号-耐久度"
                    + "卸下按装备wq-装备编号"
                    + System.getProperty("line.separator");
            for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
                Equipment equipment = NettyMemory.equipmentMap.get(weaponequipmentbar.getWid());
                wresp += "----装备id:" + weaponequipmentbar.getWid()
                        + "----装备名称" + equipment.getName()
                        + "----装备攻击力" + equipment.getAddValue()
                        + "----装备总耐久" + equipment.getDurability()
                        + "----当前耐久" + weaponequipmentbar.getDurability()
                        + System.getProperty("line.separator");
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(wresp));
        }
        if (msg.startsWith("fix-")) {
            User user = NettyMemory.session2UserIds.get(channel);
            String temp[] = msg.split("-");
            if (!NettyMemory.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
                return;
            }
            for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
                if (weaponequipmentbar.getWid() == Integer.parseInt(temp[1])) {
                    if (weaponequipmentbar.getDurability() < NettyMemory.equipmentMap.get(weaponequipmentbar.getWid()).getDurability()) {
                        weaponequipmentbar.setDurability(NettyMemory.equipmentMap.get(weaponequipmentbar.getWid()).getDurability());
                        channel.writeAndFlush(MessageUtil.turnToPacket("[" + NettyMemory.equipmentMap.get(weaponequipmentbar.getWid()).getName() + "]" + "武器修复成功"));
                        return;
                    } else {
                        channel.writeAndFlush(MessageUtil.turnToPacket("[" + NettyMemory.equipmentMap.get(weaponequipmentbar.getWid()).getName() + "]" + "武器无需修复"));
                        return;
                    }
                }
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEQUIPGOOD));
        }

        if (msg.startsWith("wq-")) {
            User user = NettyMemory.session2UserIds.get(channel);
            String temp[] = msg.split("-");
            if (NettyMemory.equipmentMap.containsKey(Integer.parseInt(temp[1]))) {
                for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
                    if (weaponequipmentbar.getWid() == Integer.parseInt(temp[1])) {
                        Userbag userbag = new Userbag();
                        userbag.setNum(1);
                        userbag.setDurability(weaponequipmentbar.getDurability());
                        userbag.setId(200);
                        userbag.setName(weaponequipmentbar.getUsername());
                        userbag.setTypeof(Good.EQUIPMENT);
                        userbag.setWid(weaponequipmentbar.getWid());
                        user.getUserBag().add(userbag);
                        user.getWeaponequipmentbars().remove(weaponequipmentbar);
                        channel.writeAndFlush(MessageUtil.turnToPacket("你成功卸下" + NettyMemory.equipmentMap.get(weaponequipmentbar.getWid()).getName()));
                        return;
                    }
                }
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEQUIPGOOD));
                return;
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
            }
        }
        if(msg.startsWith("ww-")){
            User user = NettyMemory.session2UserIds.get(channel);
            String temp[] = msg.split("-");
            if(temp.length!=3){
                channel.writeAndFlush(MessageUtil.turnToPacket("请按照ww-物品id-耐久度来装备物品"));
                return;
            }
            if(user.getWeaponequipmentbars().size()>0){
                channel.writeAndFlush(MessageUtil.turnToPacket("请卸下你的主武器再进行装备"));
                return;
            }
            if(NettyMemory.equipmentMap.containsKey(Integer.parseInt(temp[1]))){
                for(Userbag userbag:user.getUserBag()){
                    if(userbag.getWid()==Integer.parseInt(temp[1])&&userbag.getDurability()==Integer.parseInt(temp[2])){
                        if(userbag.getTypeof().equals(Good.MPMEDICINE)){
                            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOBELONGTOEQUIP));
                            return;
                        }else{
                            Weaponequipmentbar weaponequipmentbar = new Weaponequipmentbar();
                            weaponequipmentbar.setId(200);
                            weaponequipmentbar.setDurability(userbag.getDurability());
                            weaponequipmentbar.setTypeof("1");
                            weaponequipmentbar.setUsername(user.getUsername());
                            weaponequipmentbar.setWid(userbag.getWid());
                            user.getWeaponequipmentbars().add(weaponequipmentbar);
                            user.getUserBag().remove(userbag);
                            channel.writeAndFlush(MessageUtil.turnToPacket("["+NettyMemory.equipmentMap.get(userbag.getWid()).getName()+"]"+"该装备穿戴成功"));
                            return;
                        }
                    }
                }
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXISTBAG));
            }else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXIST));
            }
        }
    }
}
