package core.factory;

import core.component.monster.Monster;
import org.springframework.beans.BeanUtils;
import service.buffservice.entity.BuffConstant;
import core.context.ProjectContext;
import org.springframework.stereotype.Component;
import core.component.monster.MonsterSkill;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName MonsterFactory
 * @Description 怪物工厂类
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class MonsterFactory {
    /**
     * 根据用户地点随机生成新的怪物
     *
     * @param areaId
     * @return
     * @throws IOException
     */
    public List<Monster> getMonsterByArea(String areaId) throws IOException {
        List<Monster> newMonsterList = new ArrayList<>();
        for (Map.Entry<Integer, Monster> entry : ProjectContext.monsterMap.entrySet()) {
            if (entry.getValue().getPos().equals(areaId)) {
                Monster monster = new Monster();
                try {
                    BeanUtils.copyProperties(entry.getValue(), monster);
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

    /**
     * 使用怪物id来获取怪物
     *
     * @param monsterId
     * @return
     * @throws IOException
     */
    public Monster getMonster(Integer monsterId) throws IOException {
        for (Map.Entry<Integer, Monster> entry : ProjectContext.monsterMap.entrySet()) {
            if (entry.getValue().getId().equals(monsterId)) {
//              生成怪物
                Monster monster = new Monster();
                try {
                    BeanUtils.copyProperties(entry.getValue(), monster);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initMonsterProperties(monster);
                return monster;
            }
        }
        return null;
    }

    /**
     * 初始化怪物的其他属性比如buff初始值，刷新时间等
     * @param monster
     */
    private void initMonsterProperties(Monster monster) {
//      怪物buff初始化
        Map<String, Integer> map = new HashMap<>(64);
        map.put(BuffConstant.MPBUFF, 1000);
        map.put(BuffConstant.POISONINGBUFF, 2000);
        map.put(BuffConstant.DEFENSEBUFF, 3000);
        monster.setBufMap(map);
//      初始化每个怪物buff的终止时间
        Map<String, Long> mapSecond = new HashMap<>(64);
        monster.getMonsterBuffEndTimeMap().put(BuffConstant.MPBUFF, 1000L);
        monster.getMonsterBuffEndTimeMap().put(BuffConstant.POISONINGBUFF, 2000L);
        monster.getMonsterBuffEndTimeMap().put(BuffConstant.DEFENSEBUFF, 3000L);
//      怪物buff初始化结束

//      初始化怪物技能
        String[] s = monster.getSkillIds().split("-");
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
