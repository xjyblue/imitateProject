package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.component.monster.MonsterSkill;
import core.config.GrobalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.buffservice.entity.Buff;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MonsterSkillResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:25
 * @Version 1.0
 **/
@Component
public class MonsterSkillResourceLoad implements IResourceLoad {
    /**
     * 初始化怪物技能
     */
    public final static Map<Integer, MonsterSkill> monsterSkillMap = Maps.newHashMap();

    @Autowired
    private BuffResourceLoad buffExcelParse;

    @PostConstruct
    @Override
    public void load() {
        //      初始化怪物所有技能以及技能所带的buff
        try {
            FileInputStream monsterBuffis = new FileInputStream(new File("src/main/resources/MonsterSkill.xls"));
            LinkedHashMap<String, String> monsterBufalias = new LinkedHashMap<>();
            monsterBufalias.put("技能id", "skillId");
            monsterBufalias.put("技能名称", "skillName");
            monsterBufalias.put("技能攻击时间", "attackCd");
            monsterBufalias.put("技能伤害", "damage");
            monsterBufalias.put("技能附带buff", "bufferMapId");
            List<MonsterSkill> skillList = ExcelUtil.excel2Pojo(monsterBuffis, MonsterSkill.class, monsterBufalias);
            for (MonsterSkill monsterSkillTemp : skillList) {
                if (!monsterSkillTemp.getBufferMapId().equals(GrobalConfig.NULL)) {
                    Map<String, Integer> map = new HashMap<>(64);
                    String[] temp = monsterSkillTemp.getBufferMapId().split("-");
                    for (int i = 0; i < temp.length; i++) {
                        Buff buffTemp = BuffResourceLoad.buffMap.get(Integer.parseInt(temp[i]));
                        map.put(buffTemp.getTypeOf(), buffTemp.getBufferId());
                    }
                    monsterSkillTemp.setBuffMap(map);
                }
                monsterSkillMap.put(monsterSkillTemp.getSkillId(), monsterSkillTemp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化怪物技能ends
    }
}
