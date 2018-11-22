package component;

import pojo.User;
import skill.MonsterSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/19 16:24
 * 副本
 */
public class BossArea {

    private String teamId;

    private String name;

    private String bossName;

    private Map<String,Monster> map;

    private Long keepTime;

    private volatile boolean isEnd;

    private volatile boolean isFight;

    public boolean isFight() {
        return isFight;
    }

    public void setFight(boolean fight) {
        isFight = fight;
    }

    private Map<User,String>damageAll;

    public Map<User, String> getDamageAll() {
        return damageAll;
    }

    public void setDamageAll(Map<User, String> damageAll) {
        this.damageAll = damageAll;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public Long getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Long keepTime) {
        this.keepTime = keepTime;
    }

    public BossArea(){
        init();
    }

    public void init(){
        MonsterSkill monsterSkill = new MonsterSkill();
        monsterSkill.setSkillName("自爆");
        monsterSkill.setDamage("1000");
        monsterSkill.setAttackCd("5");
        List<MonsterSkill> monsterSkills = new ArrayList<>();
        monsterSkills.add(monsterSkill);
        Monster boss = new Monster("七天连锁酒店王",Monster.TYPEOFBOSS,"10000",monsterSkills,"1");
        Map<String,Monster>monsterMap = new HashMap<>();
        monsterMap.put(boss.getName(),boss);
        setMap(monsterMap);
        this.bossName = "七天连锁酒店王";
        this.isEnd = false;
        this.isFight = false;
        this.damageAll = new HashMap<User, String>();
    }

    public Map<String, Monster> getMap() {
        return map;
    }

    public void setMap(Map<String, Monster> map) {
        this.map = map;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
