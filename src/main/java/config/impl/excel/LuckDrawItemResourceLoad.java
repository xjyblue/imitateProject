package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.luckydrawservice.entity.LuckDrawGoodItem;
import service.luckydrawservice.entity.LuckyDrawItem;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LuckDrawItemResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/15 17:06
 * @Version 1.0
 **/
@Component
public class LuckDrawItemResourceLoad implements IResourceLoad {

    @Autowired
    private LuckDrawGoodItemResourceLoad luckDrawGoodItemResourceLoad;

    public static final Map<Integer, LuckyDrawItem> luckDrawItemMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        try {
            FileInputStream luckDrawfis = new FileInputStream(new File("src/main/resources/LuckDrawItem.xls"));
            LinkedHashMap<String, String> luckDrawAlias = new LinkedHashMap<>();
            luckDrawAlias.put("抽奖id", "id");
            luckDrawAlias.put("所需金币", "needMoney");
            luckDrawAlias.put("名称", "name");
            luckDrawAlias.put("刷新次数", "refreshCount");
            List<LuckyDrawItem> luckDrawList = ExcelUtil.excel2Pojo(luckDrawfis, LuckyDrawItem.class, luckDrawAlias);
            for (LuckyDrawItem luckyDrawItem : luckDrawList) {
                for (LuckDrawGoodItem luckDrawGoodItem : LuckDrawGoodItemResourceLoad.luckDrawGoodItems) {
                    if (luckDrawGoodItem.getLuckyDrawItemId().equals(luckyDrawItem.getId())) {
                        if(luckDrawGoodItem.isIfRandom()){
                            luckyDrawItem.getRandomLuckDrawGoodItemMap().put(luckDrawGoodItem.getId(),luckDrawGoodItem);
                        }else {
                            luckyDrawItem.getConfirmLuckDrawGoodItemMap().put(luckDrawGoodItem.getId(),luckDrawGoodItem);
                        }
                    }
                }
                luckDrawItemMap.put(luckyDrawItem.getId(),luckyDrawItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
