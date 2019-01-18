package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.component.good.MpMedicine;
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
 * @ClassName MpMedicineResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:18
 * @Version 1.0
 **/
@Component
public class MpMedicineResourceLoad implements IResourceLoad {

    /**
     * 初始化药物属性
     */
    public final static Map<Integer, MpMedicine> mpMedicineMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        //       初始化即时回复MP start
        try {
            FileInputStream mpMedicineFis = new FileInputStream(new File("src/main/resources/MpMedicine.xls"));
            LinkedHashMap<String, String> mpMedicineAlias = new LinkedHashMap<>();
            mpMedicineAlias.put("蓝药id", "id");
            mpMedicineAlias.put("回复总值", "replyValue");
            mpMedicineAlias.put("是否立刻回复", "immediate");
            mpMedicineAlias.put("每秒回复的值", "secondValue");
            mpMedicineAlias.put("持续时间", "keepTime");
            mpMedicineAlias.put("蓝药名称", "name");
            mpMedicineAlias.put("购入价值", "buyMoney");
            List<MpMedicine> mpMedicineList = ExcelUtil.excel2Pojo(mpMedicineFis, MpMedicine.class, mpMedicineAlias);
            if (mpMedicineList != null && mpMedicineList.size() > 0) {
                for (MpMedicine mpMedicine : mpMedicineList) {
                    mpMedicine.setType(BaseGood.MPMEDICINE);
                    mpMedicineMap.put(mpMedicine.getId(), mpMedicine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//       初始化即时回复MP end
    }
}
