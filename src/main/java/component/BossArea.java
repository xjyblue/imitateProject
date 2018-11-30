package component;

import buff.Buff;
import config.BuffConfig;
import memory.NettyMemory;
import pojo.User;
import skill.MonsterSkill;
import test.ExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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

        Map<String, Monster> monsterMap = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Monster.xls"));
            LinkedHashMap<String, String> alias = new LinkedHashMap<>();
            alias.put("怪物id", "id");
            alias.put("怪物名称", "name");
            alias.put("怪物类别", "type");
            alias.put("怪物生命值", "valueOfLife");
            alias.put("怪物状态", "status");
            alias.put("怪物技能", "skillIds");
            alias.put("出生地点", "pos");
            List<Monster> monsterList = ExcelUtil.excel2Pojo(fis, Monster.class, alias);
            for (Monster monster : monsterList) {
                if(monster.getPos().equals("A1")){
                    //      怪物buff初始化
                    Map<String, Integer> map = new HashMap<>();
                    map.put(BuffConfig.MPBUFF, 1000);
                    map.put(BuffConfig.POISONINGBUFF, 2000);
                    map.put(BuffConfig.DEFENSEBUFF, 3000);
                    monster.setBufMap(map);
//      初始化每个怪物buff的终止时间
                    Map<String, Long> mapSecond = new HashMap<>();
                    mapSecond.put(BuffConfig.MPBUFF, 1000l);
                    mapSecond.put(BuffConfig.POISONINGBUFF, 2000l);
                    mapSecond.put(BuffConfig.DEFENSEBUFF, 3000l);
                    NettyMemory.monsterBuffEndTime.put(monster, mapSecond);
//      怪物buff初始化结束

                    String s[] = monster.getSkillIds().split("-");
                    List<MonsterSkill> list = new ArrayList<>();
                    for(int i=0;i<s.length;i++){
                        list.add(NettyMemory.monsterSkillMap.get(Integer.parseInt(s[i])));
                    }
                    monster.setMonsterSkillList(list);
                    monsterMap.put(monster.getName(),monster);
                    if(monster.getName().equals("七天连锁酒店王")){
                        firstMonster = monster;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
