package pojo;

public class Weaponequipmentbar {
    private Integer id;

    private String username;

    private Integer wid;

    private Integer durability;

    private String typeof;

    private Integer startlevel;

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

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }

    public String getTypeof() {
        return typeof;
    }

    public void setTypeof(String typeof) {
        this.typeof = typeof == null ? null : typeof.trim();
    }

    public Integer getStartlevel() {
        return startlevel;
    }

    public void setStartlevel(Integer startlevel) {
        this.startlevel = startlevel;
    }
}