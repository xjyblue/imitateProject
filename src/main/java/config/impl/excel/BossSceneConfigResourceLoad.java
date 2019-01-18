package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.component.boss.BossSceneConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import service.sceneservice.entity.BossScene;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BossSceneConfigResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 14:33
 * @Version 1.0
 **/
@Component
@Slf4j
public class BossSceneConfigResourceLoad implements IResourceLoad {

    /**
     * 缓存副本的配置，为生成副本而用
     */
    public final static Map<String, BossSceneConfig> bossSceneConfigMap = Maps.newHashMap();
    /**
     * 每个boss副本和teamId挂钩
     */
    public final static Map<String, BossScene> bossAreaMap = Maps.newConcurrentMap();

    @PostConstruct
    @Override
    public void load() {
        //      初始化副本配置类start
        try {
            FileInputStream bossSceneConfigfis = new FileInputStream(new File("src/main/resources/BossSceneConfig.xls"));
            LinkedHashMap<String, String> bossSceneConfigfisAlias = new LinkedHashMap<>();
            bossSceneConfigfisAlias.put("副本的id", "bossSceneId");
            bossSceneConfigfisAlias.put("副本场景的顺序", "sequences");
            bossSceneConfigfisAlias.put("副本的持续时间", "keeptime");
            bossSceneConfigfisAlias.put("副本的名字", "bossSceneName");
            bossSceneConfigfisAlias.put("最后一击奖励场景", "finalReward");
            bossSceneConfigfisAlias.put("需要组队的场景", "needMoreMen");
            List<BossSceneConfig> bossSceneConfigList = ExcelUtil.excel2Pojo(bossSceneConfigfis, BossSceneConfig.class, bossSceneConfigfisAlias);
            for (BossSceneConfig bossSceneConfig : bossSceneConfigList) {
                bossSceneConfigMap.put(bossSceneConfig.getBossSceneId(), bossSceneConfig);
            }
            log.info("boss副本配置加载完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化副本配置类end
    }
}
