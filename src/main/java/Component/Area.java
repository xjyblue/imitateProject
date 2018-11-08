package Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Area {

    private String name;

    Set<String> areaSet;

    List<NPC> npcs;

    List<Monster> monsters;

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
