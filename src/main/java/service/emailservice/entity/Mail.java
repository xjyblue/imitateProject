package service.emailservice.entity;

import pojo.Userbag;

/**
 * @ClassName Mail
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Mail {
    /**
     * 是否有附件
     */
    private boolean ifUserBag;
    /**
     * email的id
     */
    private String emailId;
    /**
     * email的正文
     */
    private String emailText;
    /**
     * email来自的人
     */
    private String fromUser;
    /**
     * email发给的人
     */
    private String toUser;
    /**
     * 附带的物品
     */
    private Userbag userbag;
    /**
     * 附带的金币
     *
     * @return
     */
    private Integer money;

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public boolean isIfUserBag() {
        return ifUserBag;
    }

    public void setIfUserBag(boolean ifUserBag) {
        this.ifUserBag = ifUserBag;
    }

    public String getEmailText() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Userbag getUserbag() {
        return userbag;
    }

    public void setUserbag(Userbag userbag) {
        this.userbag = userbag;
    }
}
