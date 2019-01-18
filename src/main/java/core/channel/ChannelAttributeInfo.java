package core.channel;

import pojo.User;

/**
 * @ClassName ChannelAttributeInfo
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/14 15:51
 * @Version 1.0
 **/
public class ChannelAttributeInfo {

    private String username;

    private User user;

    private String channelStatus;

    public String getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(String channelStatus) {
        this.channelStatus = channelStatus;
    }

    public ChannelAttributeInfo(String username, User user) {
        this.username = username;
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
