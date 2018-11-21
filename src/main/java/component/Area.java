package component;

import java.util.List;
import java.util.Set;

public class Area {
    private Integer id;

    private String name;

    public Set<String> areaSet;

    public List<NPC> npcs;

    public List<Monster> monsters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters = monsters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<NPC> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<NPC> npcs) {
        this.npcs = npcs;
    }

    public Set<String> getAreaSet() {
        return areaSet;
    }

    public void setAreaSet(Set<String> areaSet) {
        this.areaSet = areaSet;
    }

}
