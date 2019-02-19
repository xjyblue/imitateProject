package server.nettySpringServer;

import service.luckydrawservice.entity.LuckDrawGoodItem;
import service.petservice.service.entity.PetConfig;
import service.petservice.service.entity.PetSkillConfig;
import utils.ExcelUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName InputUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/10 9:36
 * @Version 1.0
 **/
public class InputUtil {
    public static void main(String[] args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("src/main/resources/LuckDrawItem.xls");
        FileOutputStream luckDrawGoodItemfos = new FileOutputStream(file);

        //对象集合
        List<LuckDrawGoodItem> luckDrawGoodItemList = new ArrayList<>();

        LuckDrawGoodItem luckDrawGoodItem = new LuckDrawGoodItem();
        luckDrawGoodItem.setId(1);
        luckDrawGoodItem.setType("3");
        luckDrawGoodItem.setWid(3004);
        luckDrawGoodItem.setIfRandom(false);
        luckDrawGoodItem.setNum(1);
        luckDrawGoodItem.setStartCount(100);
        luckDrawGoodItem.setEndCount(120);
        luckDrawGoodItem.setLuckyDrawItemId(1);
        luckDrawGoodItemList.add(luckDrawGoodItem);

        //设置属性别名（列名）
        LinkedHashMap<String, String> luckDrawGoodAlias = new LinkedHashMap<String, String>();
        luckDrawGoodAlias.put("id", "抽奖单体编号id");
        luckDrawGoodAlias.put("wid", "物品的id");
        luckDrawGoodAlias.put("luckyDrawItemId", "抽奖项的编号id");
        luckDrawGoodAlias.put("ifRandom", "是否随机奖品");
        luckDrawGoodAlias.put("startCount", "触发初始几率");
        luckDrawGoodAlias.put("endCount", "触发结束几率");
        luckDrawGoodAlias.put("num", "奖品数量");
        luckDrawGoodAlias.put("type", "奖品种类");

        //标题
        String headLine = "luckDrawGoodItem表";
        ExcelUtil.pojo2Excel(luckDrawGoodItemList, luckDrawGoodItemfos, luckDrawGoodAlias, headLine);
    }
}
