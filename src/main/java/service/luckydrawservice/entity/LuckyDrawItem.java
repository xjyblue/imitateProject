package service.luckydrawservice.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @ClassName LuckyDrawItem
 * @Description 幸运抽奖项目
 * @Author xiaojianyu
 * @Date 2019/2/14 10:39
 * @Version 1.0
 **/
public class LuckyDrawItem {
    /**
     * id
     */
    private Integer id;
    /**
     * 所需金币
     */
    private Integer needMoney;
    /**
     * 随机奖品
     */
    private Map<Integer,LuckDrawGoodItem> randomLuckDrawGoodItemMap = Maps.newHashMap();
    /**
     * 内定奖品
     */
    private Map<Integer,LuckDrawGoodItem> confirmLuckDrawGoodItemMap = Maps.newHashMap();
    /**
     * 抽奖名称
     */
    private String name;
    /**
     * 刷新次数
     */
    private Integer refreshCount;

    public Map<Integer, LuckDrawGoodItem> getRandomLuckDrawGoodItemMap() {
        return randomLuckDrawGoodItemMap;
    }

    public void setRandomLuckDrawGoodItemMap(Map<Integer, LuckDrawGoodItem> randomLuckDrawGoodItemMap) {
        this.randomLuckDrawGoodItemMap = randomLuckDrawGoodItemMap;
    }

    public Map<Integer, LuckDrawGoodItem> getConfirmLuckDrawGoodItemMap() {
        return confirmLuckDrawGoodItemMap;
    }

    public void setConfirmLuckDrawGoodItemMap(Map<Integer, LuckDrawGoodItem> confirmLuckDrawGoodItemMap) {
        this.confirmLuckDrawGoodItemMap = confirmLuckDrawGoodItemMap;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNeedMoney() {
        return needMoney;
    }

    public void setNeedMoney(Integer needMoney) {
        this.needMoney = needMoney;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRefreshCount() {
        return refreshCount;
    }

    public void setRefreshCount(Integer refreshCount) {
        this.refreshCount = refreshCount;
    }
}
