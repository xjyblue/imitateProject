package service.transactionservice.entity;

import pojo.User;
import pojo.Userbag;

import java.math.BigInteger;
import java.util.Map;

/**
 * @ClassName Trade
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Trade {
    /**
     * 交易单号
     */
    private String tradeId;
    /**
     * 交易截止时间
     */
    private Long endTime;
    /**
     * 发起方
     */
    private User userStart;
    /**
     * 接收方
     */
    private User userTo;
    /**
     * 交易金钱发起方
     */
    private BigInteger startMoney;
    /**
     * 交易金钱接收方
     */
    private BigInteger toMoney;
    /**
     * 是否已经交易
     */
    private boolean ifexe;
    /**
     * 发起方背包
     */
    private Map<String, Userbag> startUserBag;
    /**
     * 接收方背包
     */
    private Map<String, Userbag> toUserBag;
    /**
     * 发起方同意
     */
    private Boolean startUserAgree;
    /**
     * 接收方同意
     */
    private Boolean toUserAgree;

    public boolean isIfexe() {
        return ifexe;
    }

    public void setIfexe(boolean ifexe) {
        this.ifexe = ifexe;
    }

    public BigInteger getStartMoney() {
        return startMoney;
    }

    public void setStartMoney(BigInteger startMoney) {
        this.startMoney = startMoney;
    }

    public BigInteger getToMoney() {
        return toMoney;
    }

    public void setToMoney(BigInteger toMoney) {
        this.toMoney = toMoney;
    }

    public Map<String, Userbag> getStartUserBag() {
        return startUserBag;
    }

    public Boolean getStartUserAgree() {
        return startUserAgree;
    }

    public void setStartUserAgree(Boolean startUserAgree) {
        this.startUserAgree = startUserAgree;
    }

    public Boolean getToUserAgree() {
        return toUserAgree;
    }

    public void setToUserAgree(Boolean toUserAgree) {
        this.toUserAgree = toUserAgree;
    }

    public void setStartUserBag(Map<String, Userbag> startUserBag) {
        this.startUserBag = startUserBag;
    }

    public Map<String, Userbag> getToUserBag() {
        return toUserBag;
    }

    public void setToUserBag(Map<String, Userbag> toUserBag) {
        this.toUserBag = toUserBag;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public User getUserStart() {
        return userStart;
    }

    public void setUserStart(User userStart) {
        this.userStart = userStart;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

}
