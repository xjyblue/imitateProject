package factory;

import component.Monster;
import config.BuffConfig;
import context.ProjectContext;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;
import skill.MonsterSkill;
import test.ExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/29 12:11
 */
//怪物工厂类
@Component("monsterFactory")
public class MonsterFactory {


    //      根据用户地点随机生成新的怪物
    public List<Monster> getMonsterByArea(String areaId) throws IOException {
        List<Monster> newMonsterList = new ArrayList<>();
        for (Map.Entry<Integer, Monster> entry : ProjectContext.monsterMap.entrySet()) {
            if (entry.getValue().getPos().equals(areaId)) {
                Monster monster = new Monster();
                try {
                    BeanUtils.copyProperties(monster, entry.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
//              初始化怪物的其他属性
                initMonsterProperties(monster);
                newMonsterList.add(monster);
            }
        }
        return newMonsterList;
    }

    //使用怪物id来获取怪物
    public Monster getMonster(Integer monsterId) throws IOException {
        for (Map.Entry<Integer, Monster> entry : ProjectContext.monsterMap.entrySet()) {
            if (entry.getValue().getId().equals(monsterId)) {
//              生成怪物
                Monster monster = new Monster();
                try {
                    BeanUtils.copyProperties(monster, entry.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initMonsterProperties(monster);
                return monster;
            }
        }
        return null;
    }

    private void initMonsterProperties(Monster monster) {
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
        ProjectContext.monsterBuffEndTime.put(monster, mapSecond);
//      怪物buff初始化结束

//      初始化怪物技能
        String s[] = monster.getSkillIds().split("-");
        List<MonsterSkill> list = new ArrayList<>();
        for (int i = 0; i < s.length; i++) {
            list.add(ProjectContext.monsterSkillMap.get(Integer.parseInt(s[i])));
        }
//              初始化怪物buff刷新时间间隔
        monster.setBuffRefreshTime(0L);
//              初始化怪物攻击时间间隔
        monster.setAttackEndTime(0L);
        monster.setMonsterSkillList(list);
    }

}
