package pojo;

import core.ServiceDistributor;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import packet.PacketProto;
import service.buffservice.service.BuffTask;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
//  用户名
    private String username;
//  密码
    private String password;
//  用户状态
    private String status;
//  用户位置
    private String pos;
//  用户mp
    private String mp;
//  用户hp
    private String hp;
//  用户金钱
    private String money;
//  用户队伍
    private String teamId;
//  用户交易单
    private String traceId;
//  用户角色
    private Integer roleid;
//  用户经验值
    private Integer experience;
//  用户工会id
    private String unionid;
//  用户的工会会员等级
    private Integer unionlevel;
//  用户背包
    private List<Userbag> userBag;
//  用户武器栏
    private List<Weaponequipmentbar> weaponequipmentbars;
//  用户buff
    private Map<String, Integer> buffMap;
//  用户成就任务
    private List<Achievementprocess> achievementprocesses;
//  用户是否交易
    private boolean ifTrade;
//  事件分发器
    private ServiceDistributor serviceDistributor;
//  用户buff
    private BuffTask buffTask;
//  用户buff刷新时间
    private Long buffRefreshTime;
//  在线状态
    private boolean ifOnline;
//  顶号转态处理
    private boolean occupied;
    //玩家的命令消费队列
    private ConcurrentLinkedQueue<PacketProto.Packet> packetsQueue = new ConcurrentLinkedQueue<>();

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

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

    public ServiceDistributor getServiceDistributor() {
        return serviceDistributor;
    }

    public void setServiceDistributor(ServiceDistributor serviceDistributor) {
        this.serviceDistributor = serviceDistributor;
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
        if(packetsQueue.peek() != null) {
            PacketProto.Packet packet = packetsQueue.poll();
            Channel channel = ProjectContext.userToChannelMap.get(this);
            try {
                serviceDistributor.distributeEvent(channel, packet.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}