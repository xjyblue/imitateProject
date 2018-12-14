package achievement;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/12 14:52
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

    private Integer achievementId;

    private String name;

    private Integer type;

    private String target;

    private String begin;

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
