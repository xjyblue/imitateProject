package component;

import config.BuffConfig;
import memory.NettyMemory;
import pojo.User;
import skill.MonsterSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/19 16:24
 * 副本
 */
public class BossArea {

    private String teamId;

    private String name;

    private String bossName;

    private Map<String, Monster> map;

    private Long keepTime;

    private volatile boolean isEnd;

    private volatile boolean isFight;

    private Monster firstMonster;

    public boolean isFight() {
        return isFight;
    }

    public void setFight(boolean fight) {
        isFight = fight;
    }

    private Map<User, String> damageAll;

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

    public Monster getFirstMonster() {
        return firstMonster;
    }

    public void setFirstMonster(Monster firstMonster) {
        this.firstMonster = firstMonster;
    }

    public BossArea() {
        init();
    }

    public void init() {
        MonsterSkill monsterSkill = new MonsterSkill();
        monsterSkill.setSkillName("自爆");
        monsterSkill.setDamage("1000");

        MonsterSkill monsterSkill2 = new MonsterSkill();
        monsterSkill2.setSkillName("全体爆炸");
        monsterSkill2.setDamage("500");

        MonsterSkill monsterSkill3 = new MonsterSkill();
        monsterSkill3.setSkillName("针对眩晕");
        monsterSkill3.setDamage("50");

        MonsterSkill monsterSkill4 = new MonsterSkill();
        monsterSkill4.setSkillName("中毒绝技");
        monsterSkill4.setDamage("50");

        Map<String, Integer> map2 = new HashMap<>();
        Map<String, Integer> map3 = new HashMap<>();
        Map<String, Integer> map4 = new HashMap<>();

//      为怪物的攻击推送buff
        map2.put(BuffConfig.ALLPERSON, 4001);
        monsterSkill2.setBuffMap(map2);

        map3.put(BuffConfig.SLEEPBUFF, 5001);
        monsterSkill3.setBuffMap(map3);

        map4.put(BuffConfig.POISONINGBUFF,2001);
        monsterSkill4.setBuffMap(map4);
//      为怪物的攻击推送buff

        List<MonsterSkill> monsterSkills = new ArrayList<>();
        monsterSkills.add(monsterSkill);
        monsterSkills.add(monsterSkill2);
        monsterSkills.add(monsterSkill3);
        monsterSkills.add(monsterSkill4);
        Monster boss = new Monster("七天连锁酒店王", Monster.TYPEOFBOSS, "10000", monsterSkills, "1");


//      怪物buff初始化
        Map<String, Integer> map = new HashMap<>();
        map.put(BuffConfig.MPBUFF, 1000);
        map.put(BuffConfig.POISONINGBUFF, 2000);
        map.put(BuffConfig.DEFENSEBUFF, 3000);
        boss.setBufMap(map);
//      初始化每个怪物buff的终止时间
        Map<String, Long> mapSecond = new HashMap<>();
        mapSecond.put(BuffConfig.MPBUFF, 1000l);
        mapSecond.put(BuffConfig.POISONINGBUFF, 2000l);
        mapSecond.put(BuffConfig.DEFENSEBUFF, 3000l);
        NettyMemory.monsterBuffEndTime.put(boss,mapSecond);
//      怪物buff初始化结束

        firstMonster = boss;

        Monster bossSecond = new Monster("迪拜酒店王", Monster.TYPEOFBOSS, "20000", monsterSkills, "1");
        Map<String, Monster> monsterMap = new HashMap<>();
        monsterMap.put(boss.getName(), boss);
        monsterMap.put(bossSecond.getName(), bossSecond);

//      怪物buff初始化
        map = new HashMap<>();
        map.put(BuffConfig.MPBUFF, 1000);
        map.put(BuffConfig.POISONINGBUFF, 2000);
        map.put(BuffConfig.DEFENSEBUFF, 3000);
        bossSecond.setBufMap(map);
//      初始化每个怪物buff的终止时间
        mapSecond = new HashMap<>();
        mapSecond.put(BuffConfig.MPBUFF, 1000l);
        mapSecond.put(BuffConfig.POISONINGBUFF, 2000l);
        mapSecond.put(BuffConfig.DEFENSEBUFF, 3000l);
        NettyMemory.monsterBuffEndTime.put(bossSecond,mapSecond);
//      怪物buff初始化结束

        setMap(monsterMap);
        this.bossName = "酒店怪兽副本";
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
