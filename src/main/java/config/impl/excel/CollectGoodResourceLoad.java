package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.component.good.CollectGood;
import core.context.ProjectContext;
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
 * @ClassName CollectGoodResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:09
 * @Version 1.0
 **/
@Component
public class CollectGoodResourceLoad implements IResourceLoad {
    /**
     * 收集类物品的id
     */
    public final static Map<Integer, CollectGood> collectGoodMap = Maps.newConcurrentMap();

    @PostConstruct
    @Override
    public void load() {
        //      初始化收集类物品start
        try {
            FileInputStream collectGoodfis = new FileInputStream(new File("src/main/resources/CollectGood.xls"));
            LinkedHashMap<String, String> collectGoodAlias = new LinkedHashMap<>();
            collectGoodAlias.put("物品的名字", "name");
            collectGoodAlias.put("物品的id", "id");
            collectGoodAlias.put("物品的描述", "desc");
            collectGoodAlias.put("物品的价格", "buyMoney");
            collectGoodAlias.put("物品的种类", "type");
            List<CollectGood> collectGoodList = ExcelUtil.excel2Pojo(collectGoodfis, CollectGood.class, collectGoodAlias);
            for (CollectGood collectGood : collectGoodList) {
                collectGoodMap.put(collectGood.getId(), collectGood);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化收集类物品end
    }
}
