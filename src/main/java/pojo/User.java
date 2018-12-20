package pojo;

import event.EventDistributor;
import io.netty.channel.Channel;
import context.ProjectContext;
import packet.PacketProto;
import buff.BuffTask;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private String username;

    private String password;

    private String status;

    private String pos;

    private String mp;

    private String hp;

    private String money;

    private String teamId;

    private String traceId;

    private Integer roleid;

    private Integer experience;

    private String unionid;

    private Integer unionlevel;

    private List<Userbag> userBag;

    private List<Weaponequipmentbar> weaponequipmentbars;

    private Map<String, Integer> buffMap;

    private List<Achievementprocess> achievementprocesses;

    private boolean ifTrade;

    private EventDistributor eventDistributor;

    private BuffTask buffTask;

    private Long buffRefreshTime;

    private boolean ifOnline;

    //玩家的命令消费队列
    private ConcurrentLinkedQueue<PacketProto.Packet> packetsQueue = new ConcurrentLinkedQueue<>();

    public boolean isIfOnline() {
        return ifOnline;
    }

    public void setIfOnline(boolean ifOnline) {
        this.ifOnline = ifOnline;
    }

    public Long getBuffRefreshTime() {
        return buffRefreshTime;
    }

    public void setBuffRefreshTime(Long buffRefreshTime) {
        this.buffRefreshTime = buffRefreshTime;
    }

    public BuffTask getBuffTask() {
        return buffTask;
    }

    public void setBuffTask(BuffTask buffTask) {
        this.buffTask = buffTask;
    }

    public EventDistributor getEventDistributor() {
        return eventDistributor;
    }

    public void setEventDistributor(EventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

    public ConcurrentLinkedQueue<PacketProto.Packet> getPacketsQueue() {
        return packetsQueue;
    }

    public void setPacketsQueue(ConcurrentLinkedQueue<PacketProto.Packet> packetsQueue) {
        this.packetsQueue = packetsQueue;
    }

    public List<Achievementprocess> getAchievementprocesses() {
        return achievementprocesses;
    }

    public void setAchievementprocesses(List<Achievementprocess> achievementprocesses) {
        this.achievementprocesses = achievementprocesses;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<Userbag> getUserBag() {
        return userBag;
    }

    public void setUserBag(List<Userbag> userBag) {
        this.userBag = userBag;
    }

    public List<Weaponequipmentbar> getWeaponequipmentbars() {
        return weaponequipmentbars;
    }

    public void setWeaponequipmentbars(List<Weaponequipmentbar> weaponequipmentbars) {
        this.weaponequipmentbars = weaponequipmentbars;
    }

    public Map<String, Integer> getBuffMap() {
        return buffMap;
    }

    public void setBuffMap(Map<String, Integer> buffMap) {
        this.buffMap = buffMap;
    }

    public boolean isIfTrade() {
        return ifTrade;
    }

    public void setIfTrade(boolean ifTrade) {
        this.ifTrade = ifTrade;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos == null ? null : pos.trim();
    }

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp == null ? null : mp.trim();
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp == null ? null : hp.trim();
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money == null ? null : money.trim();
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid == null ? null : unionid.trim();
    }

    public Integer getUnionlevel() {
        return unionlevel;
    }

    public void setUnionlevel(Integer unionlevel) {
        this.unionlevel = unionlevel;
    }


    public synchronized void addMp(String changeNum) {
        BigInteger userMp = new BigInteger(this.getMp());
        BigInteger addMp = new BigInteger(changeNum);
        userMp = userMp.add(addMp);
        this.setMp(userMp.toString());
    }

    public synchronized void subMp(String changeNum) {
        BigInteger userMp = new BigInteger(this.getMp());
        BigInteger subMp = new BigInteger(changeNum);
        userMp = userMp.subtract(subMp);
        this.setMp(userMp.toString());
    }

    public synchronized void subHp(String changeNum) {
        BigInteger userHp = new BigInteger(this.getHp());
        BigInteger subHp = new BigInteger(changeNum);
        userHp = userHp.subtract(subHp);
        if (userHp.compareTo(new BigInteger("0")) < 0) {
            this.setHp("0");
        } else {
            this.setHp(userHp.toString());
        }
    }


    public void subMoney(BigInteger sendMoney) {
        BigInteger userMoney = new BigInteger(this.getMoney());
        userMoney = userMoney.subtract(sendMoney);
        this.setMoney(userMoney.toString());
    }


    public synchronized void addHp(String recoverValue) {
        BigInteger addHp = new BigInteger(recoverValue);
        BigInteger userHp = new BigInteger(this.hp);
        userHp = userHp.add(addHp);
        this.hp = userHp.toString();
    }

    public void keepCall() {
        if (this.isIfOnline()) {
//      消费命令
            consumePacket();
//      buff更新
            buffRefresh();
        }
    }

    private void buffRefresh() {
        if (buffRefreshTime < System.currentTimeMillis()) {
            buffTask.refresh(this);
            buffRefreshTime = System.currentTimeMillis() + 1000;
        }
    }

    private void consumePacket() {
        while (packetsQueue.peek() != null) {
            PacketProto.Packet packet = packetsQueue.poll();
            Channel channel = ProjectContext.userToChannelMap.get(this);
            try {
                eventDistributor.distributeEvent(channel, packet.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}