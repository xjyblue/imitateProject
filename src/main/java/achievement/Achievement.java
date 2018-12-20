package achievement;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/12 14:52
 */
public class Achievement {
    //  杀怪任务
    public static final int ATTACKMONSTER = 1;
    //  升级任务
    public static final int UPLEVEL = 2;
    //  说话任务
    public static final int TALKTONPC = 3;
    //  收集任务
    public static final int COLLECT = 4;
    //  通关副本任务
    public static final int FINISHBOSSAREA = 5;
    //  第一次好友任务
    public static final int FRIEND = 6;
    //  第一次工会任务
    public static final int UNIONFIRST = 7;
    //  第一次和玩家交易
    public static final int TRADEFIRST = 8;
    //  金币数量任务
    public static final int MONEYFIRST = 9;
    //  第一次组队
    public static final int TEAMFIRST = 10;
    //  组合型任务
    public static final int COMBINATION = 11;
    //  第一次pk弄死对方
    public static final int PKFIRST = 12;
    //   装备星级
    public static final int EQUIPMENTSTARTLEVEL = 13;

    private Integer achievementId;

    private String name;

    private Integer type;

    private String target;

    private String begin;

    private String parent;

    private String sons;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getSons() {
        return sons;
    }

    public void setSons(String sons) {
        this.sons = sons;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public Integer getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Integer achievementId) {
        this.achievementId = achievementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
