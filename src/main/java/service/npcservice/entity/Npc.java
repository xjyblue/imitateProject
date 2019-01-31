package service.npcservice.entity;

import service.achievementservice.entity.Achievement;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName Npc
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Npc {
    /**
     * npc的id
     */
    private Integer id;
    /**
     * npc的状态
     */
    private String status;
    /**
     * npc的名字
     */
    private String name;
    /**
     * npc的对话
     */
    private String talk;
    /**
     * npc的位置
     */
    private Integer areaId;
    /**
     * npc兑换物品的条件
     */
    private String getTarget;
    /**
     * npc能兑换的物品
     */
    private String getGoods;

    /**
     * npc可接的任务
     */
    private Map<Integer, Achievement> achievementMap = new HashMap<>();

    public Map<Integer, Achievement> getAchievementMap() {
        return achievementMap;
    }

    public void setAchievementMap(Map<Integer, Achievement> achievementMap) {
        this.achievementMap = achievementMap;
    }

    public Npc() {
    }

    public String getGetTarget() {
        return getTarget;
    }

    public void setGetTarget(String getTarget) {
        this.getTarget = getTarget;
    }

    public String getGetGoods() {
        return getGoods;
    }

    public void setGetGoods(String getGoods) {
        this.getGoods = getGoods;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Npc(String status, String name) {
        super();
        this.status = status;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
