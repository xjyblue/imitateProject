package service.auctionservice.entity;

import pojo.Userbag;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName AuctionItem
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/8 21:26
 * @Version 1.0
 **/
public class AuctionItem {
    /**
     * id
     */
    private String id;
    /**
     * 是否一口价
     */
    private boolean immediate;
    /**
     * 卖家
     */
    private String fromUsername;
    /**
     * 买家
     */
    private String buyUsername;
    /**
     * 卖出价格
     */
    private Integer saleMoney;
    /**
     * 购入价格
     */
    private Integer buyMoney;
    /**
     * 拍卖截止时间
     */
    private Long endTime;
    /**
     * 拍卖的物品
     */
    private Userbag userbag;
    /**
     * 交易是否结束
     */
    private boolean isEnd;
    /**
     * 此次交易单的锁
     */
    private Lock auctionItemLock = new ReentrantLock();

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public Lock getAuctionItemLock() {
        return auctionItemLock;
    }

    public void setAuctionItemLock(Lock auctionItemLock) {
        this.auctionItemLock = auctionItemLock;
    }

    public Integer getSaleMoney() {
        return saleMoney;
    }

    public void setSaleMoney(Integer saleMoney) {
        this.saleMoney = saleMoney;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getBuyUsername() {
        return buyUsername;
    }

    public void setBuyUsername(String buyUsername) {
        this.buyUsername = buyUsername;
    }

    public Integer getBuyMoney() {
        return buyMoney;
    }

    public void setBuyMoney(Integer buyMoney) {
        this.buyMoney = buyMoney;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Userbag getUserbag() {
        return userbag;
    }

    public void setUserbag(Userbag userbag) {
        this.userbag = userbag;
    }
}
