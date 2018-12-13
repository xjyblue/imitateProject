package factory;

import component.Monster;
import config.BuffConfig;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import skill.MonsterSkill;
import test.ExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/29 12:11
 */
//怪物工厂类
@Component("monsterFactory")
public class MonsterFactory {

    //使用 怪物id来获取怪物
    public Monster getMonster(Integer monsterId) throws IOException {
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Monster.xls"));
        LinkedHashMap<String, String> alias = new LinkedHashMap<>();
        alias.put("怪物id", "id");
        alias.put("怪物名称", "name");
        alias.put("怪物类别", "type");
        alias.put("怪物生命值", "valueOfLife");
        alias.put("怪物状态", "status");
        alias.put("怪物技能", "skillIds");
        alias.put("出生地点", "pos");
        alias.put("怪物经验值", "experience");
        List<Monster> monsterList = ExcelUtil.excel2Pojo(fis, Monster.class, alias);


        for (Monster monster : monsterList) {
            if (monster.getId().equals(monsterId)) {
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
                for (int i = 0; i < s.length; i++) {
                    list.add(NettyMemory.monsterSkillMap.get(Integer.parseInt(s[i])));
                }
                monster.setMonsterSkillList(list);
                return monster;
            }
        }
        return null;
    }

//  根据用户地点随机生成新的怪物
    public Monster getMonsterByArea(String area) throws IOException {
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Monster.xls"));
        LinkedHashMap<String, String> alias = new LinkedHashMap<>();
        alias.put("怪物id", "id");
        alias.put("怪物名称", "name");
        alias.put("怪物类别", "type");
        alias.put("怪物生命值", "valueOfLife");
        alias.put("怪物状态", "status");
        alias.put("怪物技能", "skillIds");
        alias.put("出生地点", "pos");
        alias.put("怪物经验值", "experience");
        List<Monster> monsterList = ExcelUtil.excel2Pojo(fis, Monster.class, alias);

        List<Monster> newMonsterList = new ArrayList<>();
        for (Monster monster : monsterList) {
            if (monster.getPos().equals(area)) {
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
                for (int i = 0; i < s.length; i++) {
                    list.add(NettyMemory.monsterSkillMap.get(Integer.parseInt(s[i])));
                }
                monster.setMonsterSkillList(list);
                newMonsterList.add(monster);
            }
        }

        if(newMonsterList.size()>0){
            int size = newMonsterList.size();
            int randomNumber = (int) (Math.random() * size);
//          随机生成怪物
            return newMonsterList.get(randomNumber);
        }
        return null;
    }

}
