package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.component.good.HpMedicine;
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
 * @ClassName HpMedicineResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:22
 * @Version 1.0
 **/
@Component
public class HpMedicineResourceLoad implements IResourceLoad {

    /**
     * 红药恢复的时间
     */
    public final static Map<Integer, HpMedicine> hpMedicineMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        //      初始化红药系统start
        try {
            FileInputStream hpfis = new FileInputStream(new File("src/main/resources/HpMedicine.xls"));
            LinkedHashMap<String, String> hpMedicineAlias = new LinkedHashMap<>();
            hpMedicineAlias.put("红药的id", "id");
            hpMedicineAlias.put("红药是否为立即回复药品", "immediate");
            hpMedicineAlias.put("红药的cd", "cd");
            hpMedicineAlias.put("红药每秒恢复的血量", "replyValue");
            hpMedicineAlias.put("红药持续的时间", "keepTime");
            hpMedicineAlias.put("红药持续的名字", "name");
            hpMedicineAlias.put("物品的价值", "buyMoney");
            hpMedicineAlias.put("红药的种类", "type");
            List<HpMedicine> hpMedicineList = ExcelUtil.excel2Pojo(hpfis, HpMedicine.class, hpMedicineAlias);
            for (HpMedicine hpMedicine : hpMedicineList) {
                hpMedicineMap.put(hpMedicine.getId(), hpMedicine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化红药
    }
}
