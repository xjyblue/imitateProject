package service.achievementservice.entity;

/**
 * @ClassName AchievementConfig
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/31 9:49
 * @Version 1.0
 **/
public class AchievementConfig {
    /**
     * 杀怪任务
     */
    public static final int ATTACKMONSTER = 1;
    /**
     * 升级任务
     */
    public static final int UPLEVEL = 2;
    /**
     * 收集任务
     */
    public static final int COLLECT = 4;
    /**
     * 装备星级
     */
    public static final int EQUIPMENTSTARTLEVEL = 13;
    /**
     * 说话任务
     */
    public static final int TALKTONPC = 3;
    /**
     * 通关副本任务
     */
    public static final int FINISHBOSSAREA = 5;
    /**
     * 第一次好友任务
     */
    public static final int FRIEND = 6;
    /**
     * 第一次工会任务
     */
    public static final int UNIONFIRST = 7;
    /**
     * 第一次和玩家交易
     */
    public static final int TRADEFIRST = 8;
    /**
     * 金币数量任务
     */
    public static final int MONEYFIRST = 9;
    /**
     * 第一次组队
     */
    public static final int TEAMFIRST = 10;
    /**
     * 组合型任务
     */
    public static final int COMBINATION = 11;
    /**
     * 第一次pk弄死对方
     */
    public static final int PKFIRST = 12;


    /**
     * 任务已接受状态
     */
    public static final int DOING_TASK = 0;
    /**
     * 任务已完成状态
     */
    public static final int COMPLETE_TASK = 1;
    /**
     * 任务未接受但可接受状态
     */
    public static final int CAN_ACCEPT = 2;
    /**
     * 任务完成并且交付
     */
    public static final int COMPLETE_AND_GIVE_TASK = 4;
}
