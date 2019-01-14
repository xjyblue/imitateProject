package core.config;

import io.netty.channel.Channel;

/**
 * @ClassName GrobalConfig
 * @Description 全局配置
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class GrobalConfig {
    /**
     * 0，遵循阿里规范
     */
    public static final int ZERO = 0;
    /**
     * 1，遵循阿里规范
     */
    public static final int ONE = 1;
    /**
     * 2，遵循阿里规范
     */
    public static final int TWO = 2;
    /**
     * 3,遵循阿里规范
     */
    public static final int THREE = 3;
    /**
     * 4,遵循阿里规范
     */
    public static final int FOUR = 4;
    /**
     * 5,遵循阿里规范
     */
    public static final int FIVE = 5;
    /**
     * 6,遵循阿里规范
     */
    public static final int SIX = 6;
    /**
     * 10 遵循阿里规范
     */
    public static final int TEN = 10;
    /**
     * 人物死亡状态
     */
    public static final String DEAD = "0";
    /**
     * 人物存活状态
     */
    public static final String ALIVE = "1";
    /**
     * 最低值
     */
    public static final String MINVALUE = "0";
    /**
     * 无效状态（用于一些奖励，有些怪物无奖励,一些技能无buff）
     */
    public static final String NULL = "0";
    /**
     * 起始之地
     */
    public static final String STARTSCENE = "0";
    /**
     * 人物每秒自动回复的蓝量
     */
    public static final String MPSECONDVALUE = "10";
    /**
     * 进入副本的最低等级
     */
    public static final int MIN_ENTER_BOSSSCENE = 10;
    /**
     * 游戏端口号
     */
    public static final int PORTNUM = 8081;
    /**
     * 复活场景配置
     */
    public static final String RECOVER_SCENE = "起始之地";
    /**
     * 丢失多少次心跳后销毁渠道
     */
    public static final int HEARTLOSE = 15;
    /**
     * 默认防御buff的初始值
     */
    public static final int DEFENSEBUFF_DEFAULTVALUE = 3000;
    /**
     * 默认睡眠buff的初始值
     */
    public static final int SLEEPBUFF_DEFAULTVALUE = 5000;
    /**
     * 默认中毒buff的初始值
     */
    public static final int POISONINGBUFF_DEFAULTVALUE = 2000;
    /**
     * 默认Mp buff的初始值
     */
    public static final int MP_DEFAULTVALUE = 1000;
    /**
     * 嘲讽 buff初始值
     */
    public static final int TAUNT_DEFAULTVALUE = 9000;
    /**
     * 武器的范围起点
     */
    public static final int EQUIPMENT_WEAPON_START = 3000;
    /**
     * 武器的范围终点
     */
    public static final int EQUIPMENT_WEAPON_END = 3100;
    /**
     * 帽子的范围起点
     */
    public static final int HAT_WEAPON_START = 3100;
    /**
     * 帽子的范围终点点
     */
    public static final int HAT_WEAPON_END = 3200;
    /**
     * 拍卖截止时间
     */
    public static final Long AUCTION_END_TIME = 360000L;
    /**
     * 系统邮件名称
     */
    public static final String SYSTEM_EMAIL = "系统邮件";
    /**
     * 系统默认宠物
     */
    public static final String DEFAULT_PET = "1";
}
