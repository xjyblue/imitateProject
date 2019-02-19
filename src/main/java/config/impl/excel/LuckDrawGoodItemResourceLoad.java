package config.impl.excel;

import com.google.common.collect.Lists;
import config.interf.IResourceLoad;
import org.springframework.stereotype.Component;
import service.levelservice.entity.Level;
import service.luckydrawservice.entity.LuckDrawGoodItem;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName LuckDrawGoodItemResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/15 16:11
 * @Version 1.0
 **/
@Component
public class LuckDrawGoodItemResourceLoad implements IResourceLoad {

    public static List<LuckDrawGoodItem> luckDrawGoodItems;



    @PostConstruct
    @Override
    public void load() {
        try {
            FileInputStream luckDrawGoodfis = new FileInputStream(new File("src/main/resources/LuckDrawGoodItem.xls"));
            LinkedHashMap<String, String> luckDrawGoodAlias = new LinkedHashMap<>();
            luckDrawGoodAlias.put( "抽奖单体编号id","id");
            luckDrawGoodAlias.put( "物品的id","wid");
            luckDrawGoodAlias.put( "抽奖项的编号id","luckyDrawItemId");
            luckDrawGoodAlias.put( "是否随机奖品","ifRandom");
            luckDrawGoodAlias.put( "触发初始几率","startCount");
            luckDrawGoodAlias.put( "触发结束几率","endCount");
            luckDrawGoodAlias.put( "奖品数量","num");
            luckDrawGoodAlias.put( "奖品种类","type");
            List<LuckDrawGoodItem> luckDrawGoodList = ExcelUtil.excel2Pojo(luckDrawGoodfis, LuckDrawGoodItem.class, luckDrawGoodAlias);
            luckDrawGoodItems = luckDrawGoodList;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
