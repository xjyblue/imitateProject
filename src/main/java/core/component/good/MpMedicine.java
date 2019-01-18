package core.component.good;

import com.google.common.collect.Maps;
import core.component.good.parent.BaseGood;

import java.util.Map;

/**
 * @ClassName MpMedicine
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class MpMedicine extends BaseGood {

    public MpMedicine(){
    }

    public MpMedicine(Integer id,String replyValue, boolean immediate, String secondValue, Integer keepTime) {
        this.id =id;
        this.replyValue = replyValue;
        this.immediate = immediate;
        this.secondValue = secondValue;
        this.keepTime = keepTime;
    }

    /**
     * 蓝药id
     */
    private Integer id;
    /**
     * 蓝药回复总量
     */
    private String replyValue;
    /**
     * 是否为即时回复的药品
     */
    private boolean immediate;
    /**
     * 每秒增幅
     */
    private String secondValue;
    /**
     * 持续的时间
     */
    private Integer keepTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReplyValue() {
        return replyValue;
    }

    public void setReplyValue(String replyValue) {
        this.replyValue = replyValue;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public String getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(String secondValue) {
        this.secondValue = secondValue;
    }

    public Integer getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Integer keepTime) {
        this.keepTime = keepTime;
    }
}
