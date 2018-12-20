package pojo;

public class Unioninfo {
    private String unionid;

    private String unionname;

    private String unionwarehourseid;

    private Integer unionmoney;

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid == null ? null : unionid.trim();
    }

    public String getUnionname() {
        return unionname;
    }

    public void setUnionname(String unionname) {
        this.unionname = unionname == null ? null : unionname.trim();
    }

    public String getUnionwarehourseid() {
        return unionwarehourseid;
    }

    public void setUnionwarehourseid(String unionwarehourseid) {
        this.unionwarehourseid = unionwarehourseid == null ? null : unionwarehourseid.trim();
    }

    public Integer getUnionmoney() {
        return unionmoney;
    }

    public void setUnionmoney(Integer unionmoney) {
        this.unionmoney = unionmoney;
    }
}