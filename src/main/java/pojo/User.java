package pojo;

import skill.UserSkill;

import java.util.Map;

public class User {
    private String username;

    private String password;

    private String status;

    private String pos;

    private String mp;

    private String hp;

    private Map<String, UserSkill> map;

    public Map<String, UserSkill> getMap() {
        return map;
    }

    public void setMap(Map<String, UserSkill> map) {
        this.map = map;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos == null ? null : pos.trim();
    }

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp == null ? null : mp.trim();
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp == null ? null : hp.trim();
    }
}