package service.luckydrawservice.entity;
import core.component.good.parent.BaseGood;

/**
 * @ClassName LuckDrawGoodItem
 * @Description 单体的种类
 * @Author xiaojianyu
 * @Date 2019/2/15 11:14
 * @Version 1.0
 **/
public class LuckDrawGoodItem extends BaseGood {
    /**
     * 奖品单体的id
     */
    private Integer id;
    /**
     * 物品的编号
     */
    private Integer wid;
    /**
     * 奖品所属抽奖项的id
     */
    private Integer luckyDrawItemId;
    /**
     * 是否为随机奖品
     */
    private boolean ifRandom;
    /**
     * 中奖的概率触发点
     */
    private Integer startCount;
    /**
     * 中奖的概率终止点
     */
    private Integer endCount;
    /**
     * 奖品的数量
     */
    private Integer num;

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLuckyDrawItemId() {
        return luckyDrawItemId;
    }

    public void setLuckyDrawItemId(Integer luckyDrawItemId) {
        this.luckyDrawItemId = luckyDrawItemId;
    }

    public boolean isIfRandom() {
        return ifRandom;
    }

    public void setIfRandom(boolean ifRandom) {
        this.ifRandom = ifRandom;
    }

    public Integer getStartCount() {
        return startCount;
    }

    public void setStartCount(Integer startCount) {
        this.startCount = startCount;
    }

    public Integer getEndCount() {
        return endCount;
    }

    public void setEndCount(Integer endCount) {
        this.endCount = endCount;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
