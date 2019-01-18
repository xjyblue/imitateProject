package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import org.springframework.stereotype.Component;
import service.levelservice.entity.Level;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LevelResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:21
 * @Version 1.0
 **/
@Component
public class LevelResourceLoad implements IResourceLoad {

    /**
     * 人物经验表的建立
     */
    public final static Map<Integer, Level> levelMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        //      初始化人物经验表start
        try {
            FileInputStream levelfis = new FileInputStream(new File("src/main/resources/Level.xls"));
            LinkedHashMap<String, String> levelalias = new LinkedHashMap<>();
            levelalias.put("等级", "level");
            levelalias.put("经验上限", "experienceUp");
            levelalias.put("经验下限", "experienceDown");
            levelalias.put("血量上限", "maxHp");
            levelalias.put("蓝量上限", "maxMp");
            levelalias.put("攻击力加成", "upAttack");
            levelalias.put("计算比例", "caculatePercent");
            List<Level> levelList = ExcelUtil.excel2Pojo(levelfis, Level.class, levelalias);
            for (Level level : levelList) {
                levelMap.put(level.getLevel(), level);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化人物经验表end
    }
}
