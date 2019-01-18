package config.impl.excel;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import config.impl.thread.ThreadPeriodTaskLoad;
import config.interf.IResourceLoad;
import core.component.monster.Monster;
import core.factory.MonsterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.buffservice.service.AttackBuffService;
import service.buffservice.service.MonsterBuffService;
import service.caculationservice.service.HpCaculationService;
import service.npcservice.entity.Npc;
import service.rewardservice.service.RewardService;
import service.sceneservice.entity.Scene;
import service.userservice.service.UserService;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @ClassName SceneResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:28
 * @Version 1.0
 **/
@Component
public class SceneResourceLoad implements IResourceLoad {
    @Autowired
    private AttackBuffService attackBuffService;
    @Autowired
    private RewardService rewardService;
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private MonsterBuffService monsterBuffService;
    @Autowired
    private UserService userService;

    @Autowired
    private MonsterResourceLoad monsterExcelParse;
    @Autowired
    private NpcResourceLoad npcExcelParse;
    @Autowired
    private MonsterFactory monsterFactory;


    /**
     * 地图缓存到内存中
     */
    public final static Map<String, Scene> sceneMap = Maps.newHashMap();
    /**
     * 初始化地图Set集合
     */
    public final static Set<String> sceneSet = Sets.newHashSet();

    @PostConstruct
    @Override
    public void load() {
        //      初始化场景start
        try {
            FileInputStream scenefis = new FileInputStream(new File("src/main/resources/Scene.xls"));
            LinkedHashMap<String, String> sceneAlias = new LinkedHashMap<>();
            sceneAlias.put("场景的名字", "name");
            sceneAlias.put("场景的id", "id");
            sceneAlias.put("关联的场景id", "sceneIds");
            sceneAlias.put("npcId", "npcS");
            sceneAlias.put("怪物id", "monsterS");
            sceneAlias.put("需要的等级", "needLevel");
            List<Scene> sceneList = ExcelUtil.excel2Pojo(scenefis, Scene.class, sceneAlias);
            for (Scene scene : sceneList) {
                scene.setRewardService(rewardService);
                scene.setUserService(userService);
                scene.setAttackBuffService(attackBuffService);
                scene.setHpCaculationService(hpCaculationService);
                scene.setMonsterBuffService(monsterBuffService);
//              初始化npc
                List<Npc> npcs = new ArrayList<>();
//              这里后面可以改成多个npc
                npcs.add(NpcResourceLoad.npcMap.get(Integer.parseInt(scene.getNpcS())));
                scene.setNpcs(npcs);

//          初始化怪物
                List<Monster> monsters = new ArrayList<>();
                monsters.add(monsterFactory.getMonster(Integer.parseInt(scene.getMonsterS())));
                scene.setMonsters(monsters);

                //          初始化关联地图
                Set<String> areaSet = new HashSet<String>();
                String sceneConnect = scene.getSceneIds();
                for (String sceneT : sceneConnect.split("-")) {
                    areaSet.add(sceneT);
                }
                scene.setSceneSet(areaSet);
                sceneMap.put(scene.getId(), scene);
                sceneSet.add(scene.getName());
                ThreadPeriodTaskLoad.SCENE_THREAD_POOL.execute(scene);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化场景end
    }
}
