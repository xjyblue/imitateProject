package pojo;

import com.google.common.collect.Maps;
import core.ServiceDistributor;
import core.component.monster.Monster;
import core.packet.ClientPacket;
import io.netty.channel.Channel;
import service.buffservice.service.UserBuffService;
import service.petservice.service.entity.Pet;
import utils.ChannelUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @ClassName User
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class User {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户状态
     */
    private String status;
    /**
     * 用户位置
     */
    private String pos;
    /**
     * 用户mp
     */
    private String mp;
    /**
     * 用户hp
     */
    private String hp;
    /**
     * 用户金钱
     */
    private String money;
    /**
     * 用户队伍
     */
    private String teamId;
    /**
     * 用户队伍
     */
    private String traceId;
    /**
     * 用户角色
     */
    private Integer roleid;
    /**
     * 用户经验值
     */
    private Integer experience;
    /**
     * 用户工会id
     */
    private String unionid;
    /**
     * 用户的工会会员等级
     */
    private Integer unionlevel;
    /**
     * 用户背包
     */
    private List<Userbag> userBag;
    /**
     * 用户武器栏
     */
    private List<Weaponequipmentbar> weaponequipmentbars;
    /**
     * 用户buff
     */
    private Map<String, Integer> buffMap;
    /**
     * 用户技能关联
     */
    private Map<String, Userskillrelation> userskillrelationMap = Maps.newHashMap();
    /**
     * 初始化Buff的截止时间
     */
    public Map<String, Long> userBuffEndTimeMap = Maps.newConcurrentMap();
    /**
     * 用户成就任务
     */
    private List<Achievementprocess> achievementprocesses;
    /**
     * 缓存用户所攻击的怪兽
     */
    public Map<Integer, Monster> userToMonsterMap = Maps.newConcurrentMap();
    /**
     * 用户是否交易
     */
    private boolean ifTrade;
    /**
     * 事件分发器
     */
    private ServiceDistributor serviceDistributor;
    /**
     * 用户buff
     */
    private UserBuffService userBuffService;
    /**
     * 用户buff刷新时间
     */
    private Long buffRefreshTime;
    /**
     * 玩家宠物
     */
    private Pet pet;
    /**
     * 被顶号标识
     */
    private boolean ifOccupy;
    /**
     * 玩家的命令消费队列
     */
    private ConcurrentLinkedQueue<Object> packetsQueue = new ConcurrentLinkedQueue<>();

    public Map<Integer, Monster> getUserToMonsterMap() {
        return userToMonsterMap;
    }

    public void setUserToMonsterMap(Map<Integer, Monster> userToMonsterMap) {
        this.userToMonsterMap = userToMonsterMap;
    }

    public Map<String, Long> getUserBuffEndTimeMap() {
        return userBuffEndTimeMap;
    }

    public void setUserBuffEndTimeMap(Map<String, Long> userBuffEndTimeMap) {
        this.userBuffEndTimeMap = userBuffEndTimeMap;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public boolean isIfOccupy() {
        return ifOccupy;
    }

    public void setIfOccupy(boolean ifOccupy) {
        this.ifOccupy = ifOccupy;
    }

    public Long getBuffRefreshTime() {
        return buffRefreshTime;
    }

    public void setBuffRefreshTime(Long buffRefreshTime) {
        this.buffRefreshTime = buffRefreshTime;
    }

    public UserBuffService getUserBuffService() {
        return userBuffService;
    }

    public void setUserBuffService(UserBuffService userBuffService) {
        this.userBuffService = userBuffService;
    }

    public ServiceDistributor getServiceDistributor() {
        return serviceDistributor;
    }

    public void setServiceDistributor(ServiceDistributor serviceDistributor) {
        this.serviceDistributor = serviceDistributor;
    }

    public Map<String, Userskillrelation> getUserskillrelationMap() {
        return userskillrelationMap;
    }

    public void setUserskillrelationMap(Map<String, Userskillrelation> userskillrelationMap) {
        this.userskillrelationMap = userskillrelationMap;
    }

    public ConcurrentLinkedQueue<Object> getPacketsQueue() {
        return packetsQueue;
    }

    public void setPacketsQueue(ConcurrentLinkedQueue<Object> packetsQueue) {
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

    public void keepCall() {
//      消费命令
        consumePacket();
//      buff更新
        buffRefresh();
    }

    private void buffRefresh() {
        if (buffRefreshTime < System.currentTimeMillis()) {
            userBuffService.refreshUserBuff(this);
            buffRefreshTime = System.currentTimeMillis() + 1000;
        }
    }

    private void consumePacket() {
        if (packetsQueue.peek() != null) {
            Object o = packetsQueue.poll();
            ClientPacket.NormalReq normalReq = (ClientPacket.NormalReq) o;
            Channel channel = ChannelUtil.userToChannelMap.get(this);
            try {
                serviceDistributor.distributeService(channel, normalReq.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}