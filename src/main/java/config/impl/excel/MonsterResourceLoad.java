package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.component.monster.Monster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MonsterResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:07
 * @Version 1.0
 **/
@Service
public class MonsterResourceLoad implements IResourceLoad {
    /**
     * 怪物的构建
     */
    public final static Map<Integer, Monster> monsterMap = Maps.newHashMap();

    @Autowired
    public MonsterSkillResourceLoad monsterSkillExcelParse;

    @PostConstruct
    @Override
    public void load() {
        //      初始化怪物start
        try {
            FileInputStream monsterfis = new FileInputStream(new File("src/main/resources/monster.xls"));
            LinkedHashMap<String, String> monsteralias = new LinkedHashMap<>();
            monsteralias.put("怪物id", "id");
            monsteralias.put("怪物名称", "name");
            monsteralias.put("怪物类别", "type");
            monsteralias.put("怪物生命值", "valueOfLife");
            monsteralias.put("怪物状态", "status");
            monsteralias.put("怪物技能", "skillIds");
            monsteralias.put("出生地点", "pos");
            monsteralias.put("怪物经验值", "experience");
            monsteralias.put("击杀获得的奖励", "reward");
            List<Monster> monsterList = ExcelUtil.excel2Pojo(monsterfis, Monster.class, monsteralias);
            for (Monster monster : monsterList) {
                monsterMap.put(monster.getId(), monster);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化怪物end
    }
}
