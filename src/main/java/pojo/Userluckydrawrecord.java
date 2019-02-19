package pojo;

import java.util.List;

public class Userluckydrawrecord {
    private Integer id;

    private String username;

    private Integer luckydrawid;

    private Integer nowcount;

    private List<Userluckydrawitemrecord> userluckydrawitemrecordList;

    public List<Userluckydrawitemrecord> getUserluckydrawitemrecordList() {
        return userluckydrawitemrecordList;
    }

    public void setUserluckydrawitemrecordList(List<Userluckydrawitemrecord> userluckydrawitemrecordList) {
        this.userluckydrawitemrecordList = userluckydrawitemrecordList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Integer getLuckydrawid() {
        return luckydrawid;
    }

    public void setLuckydrawid(Integer luckydrawid) {
        this.luckydrawid = luckydrawid;
    }

    public Integer getNowcount() {
        return nowcount;
    }

    public void setNowcount(Integer nowcount) {
        this.nowcount = nowcount;
    }
}