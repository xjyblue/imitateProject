package pojo;

import component.parent.Good;
import memory.NettyMemory;

public class Userbag {
    private String id;

    private String name;

    private Integer wid;

    private Integer num;

    private String typeof;

    private Integer durability;

    public static String getGoodNameByUserBag(Userbag userbag) {
        switch (userbag.getTypeof()){
            case Good.EQUIPMENT:
                return NettyMemory.equipmentMap.get(userbag.getWid()).getName();
            case Good.MPMEDICINE:
                return NettyMemory.mpMedicineMap.get(userbag.getWid()).getName();
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getTypeof() {
        return typeof;
    }

    public void setTypeof(String typeof) {
        this.typeof = typeof == null ? null : typeof.trim();
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }
}