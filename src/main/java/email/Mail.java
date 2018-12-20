package email;

import pojo.Userbag;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/23 10:29
 */
public class Mail {
    private boolean ifUserBag;

    private String emailId;

    private String emailText;

    private String fromUser;

    private String toUser;

    private Userbag userbag;

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
