package pojo;

public class Userskillrelation {
    private Integer id;

    private String username;

    private Integer skillid;

    private String keypos;

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
}