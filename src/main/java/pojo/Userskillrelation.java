package pojo;
/**
 * @ClassName Userskillrelation
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Userskillrelation {
    private Integer id;

    private String username;

    private Integer skillid;

    private String keypos;

    private Long skillcds;

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

    public Integer getSkillid() {
        return skillid;
    }

    public void setSkillid(Integer skillid) {
        this.skillid = skillid;
    }

    public String getKeypos() {
        return keypos;
    }

    public void setKeypos(String keypos) {
        this.keypos = keypos == null ? null : keypos.trim();
    }

    public Long getSkillcds() {
        return skillcds;
    }

    public void setSkillcds(Long skillcds) {
        this.skillcds = skillcds;
    }
}