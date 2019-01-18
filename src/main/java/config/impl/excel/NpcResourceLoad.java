package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import org.springframework.stereotype.Component;
import service.npcservice.entity.Npc;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName NpcResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:12
 * @Version 1.0
 **/
@Component
public class NpcResourceLoad implements IResourceLoad {

    /**
     * npc
     */
    public final static Map<Integer, Npc> npcMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        //      初始化npc start
        try {
            FileInputStream npcfis = new FileInputStream(new File("src/main/resources/Npc.xls"));
            LinkedHashMap<String, String> npcalias = new LinkedHashMap<>();
            npcalias.put("NPC的id", "id");
            npcalias.put("NPC的状态", "status");
            npcalias.put("NPC的名字", "name");
            npcalias.put("NPC的话", "talk");
            npcalias.put("NPC所在的地点", "areaId");
            npcalias.put("换取的条件", "getTarget");
            npcalias.put("换取的物品", "getGoods");
            List<Npc> npcList = ExcelUtil.excel2Pojo(npcfis, Npc.class, npcalias);
            for (Npc npc : npcList) {
                npcMap.put(npc.getId(), npc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化npc end
    }
}
