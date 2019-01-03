package core.config;

/**
 * Description ：nettySpringServer 全局变量配置
 * Created by server on 2018/11/27 9:41
 */
public class GrobalConfig {
    //  人物死亡状态
    public static final String DEAD = "0";
    //  人物存活状态
    public static final String ALIVE = "1";
    //  最低值
    public static final String MINVALUE = "0";
    //  无效状态（用于一些奖励，有些怪物无奖励,一些技能无buff）
    public static final String NULL = "0";
    //  起始之地
    public static final String STARTSCENE = "0";
    //   人物每秒自动回复的蓝量
    public static final String MPSECONDVALUE = "10";
    //   游戏端口号
    public static final int PORTNUM = 8081;
}
