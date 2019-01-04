package pojo;
/**
 * @ClassName Unionwarehouse
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
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