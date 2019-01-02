package service.transactionservice.entity;

import pojo.User;
import pojo.Userbag;

import java.math.BigInteger;
import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/3 14:44
 */
public class Trade {

    private String tradeId;

    private Long endTime;

    private User userStart;

    private User userTo;

    private BigInteger startMoney;

    private BigInteger toMoney;

    private volatile boolean ifexe;

    private Map<String,Userbag> startUserBag;

    private Map<String,Userbag> toUserBag;

    private volatile Boolean startUserAgree;

    private volatile Boolean toUserAgree;

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
