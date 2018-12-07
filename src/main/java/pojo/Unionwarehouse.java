package pojo;

public class Unionwarehouse {
    private String unionwarehouseid;

    private String userbagid;

    public String getUnionwarehouseid() {
        return unionwarehouseid;
    }

    public void setUnionwarehouseid(String unionwarehouseid) {
        this.unionwarehouseid = unionwarehouseid == null ? null : unionwarehouseid.trim();
    }

    public String getUserbagid() {
        return userbagid;
    }

    public void setUserbagid(String userbagid) {
        this.userbagid = userbagid == null ? null : userbagid.trim();
    }
}