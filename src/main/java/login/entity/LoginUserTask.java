package login.entity;

import io.netty.channel.Channel;

import java.util.Objects;

/**
 * @ClassName LoginUserTask
 * @Description 登陆比较特殊，单独抽出来
 * @Author xiaojianyu
 * @Date 2019/1/16 10:25
 * @Version 1.0
 **/
public class LoginUserTask {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 登陆渠道
     */
    private Channel channel;

    public LoginUserTask(String username, String password, Channel channel) {
        this.username = username;
        this.password = password;
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginUserTask loginUserTask = (LoginUserTask) o;
        return Objects.equals(username, loginUserTask.username) &&
                Objects.equals(password, loginUserTask.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
