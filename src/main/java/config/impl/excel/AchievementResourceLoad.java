package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import service.achievementservice.entity.Achievement;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AchievementResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:23
 * @Version 1.0
 **/
@Component
@Slf4j
public class AchievementResourceLoad implements IResourceLoad {

    /**
     * 成就表的建立
     */
    public final static Map<Integer, Achievement> achievementMap = Maps.newHashMap();


    @PostConstruct
    @Override
    public void load() {
        //      初始化任务系统
        try {
            FileInputStream achievementfis = new FileInputStream(new File("src/main/resources/Achievement.xls"));
            LinkedHashMap<String, String> achievementalias = new LinkedHashMap<>();
            achievementalias.put("任务id", "achievementId");
            achievementalias.put("任务名称", "name");
            achievementalias.put("任务类别", "type");
            achievementalias.put("任务目标", "target");
            achievementalias.put("开始阶段", "begin");
            achievementalias.put("父任务", "parent");
            achievementalias.put("子任务", "sons");
            achievementalias.put("奖励", "reward");
            List<Achievement> achievementList = ExcelUtil.excel2Pojo(achievementfis, Achievement.class, achievementalias);
            for (Achievement achievement : achievementList) {
                achievementMap.put(achievement.getAchievementId(), achievement);
            }
            log.info("成就配置资源加载完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
