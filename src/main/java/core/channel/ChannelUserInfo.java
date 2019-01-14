package core.channel;

import pojo.User;

/**
 * @ClassName ChannelUserInfo
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/14 15:51
 * @Version 1.0
 **/
public class ChannelUserInfo {

    private String name;

    private User user;

    public ChannelUserInfo(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
