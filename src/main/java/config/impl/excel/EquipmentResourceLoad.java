package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.component.good.Equipment;
import core.component.good.parent.BaseGood;
import org.springframework.stereotype.Component;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName EquipmentResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:20
 * @Version 1.0
 **/
@Component
public class EquipmentResourceLoad implements IResourceLoad {

    /**
     * 初始化武器
     */
    public final static Map<Integer, Equipment> equipmentMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        //      初始化武器start
        try {
            FileInputStream equipFis = new FileInputStream(new File("src/main/resources/Equipment.xls"));
            LinkedHashMap<String, String> equipAlias = new LinkedHashMap<>();
            equipAlias.put("武器id", "id");
            equipAlias.put("武器名称", "name");
            equipAlias.put("武器耐久度", "durability");
            equipAlias.put("武器增加伤害", "addValue");
            equipAlias.put("购入价值", "buyMoney");
            equipAlias.put("装备星级", "startLevel");
            equipAlias.put("增加生命值", "lifeValue");
            List<Equipment> equipmentList = ExcelUtil.excel2Pojo(equipFis, Equipment.class, equipAlias);
            if (equipmentList != null && equipmentList.size() > 0) {
                for (Equipment equipment : equipmentList) {
                    equipment.setType(BaseGood.EQUIPMENT);
                    equipmentMap.put(equipment.getId(), equipment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化武器end
    }
}
