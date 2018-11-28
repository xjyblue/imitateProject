package config;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/21 15:14
 */
public class MessageConfig {
    public static final String LOGINMESSAGE = "请输入：用户名-密码";
    public static final String REGISTERMESSAGE = "请输入：用户名-密码-确认密码";
    public static final String ERRORPASSWORD = "账户密码出错";
    public static final String DOUBLEPASSWORDERROR = "两次密码不一致";
    public static final String REGISTERSUCCESS = "注册成功请输入：用户名-密码进行登录";
    public static final String REPEATUSER = "你的账号已在其他地方登陆，无法重复登陆";

    public static final String UNMOVELOCAL = "原地无需移动";
    public static final String NOTARGETTOMOVE = "移动地点不存在";
    public static final String REMOTEMOVEMESSAGE = "请充值才能启用传送门";
    public static final String NOFOUNDNPC = "找不到此NPC";

    public static final String ERRORORDER = "您输入的指令有误,请输入有效的指令";
    public static final String FAILGOODID = "请输入有效的物品ID";
    public static final String UNENOUGHMONEY = "您的金钱不足，请充值";
    public static final String NOUSERBAGID = "背包ID不存在";

    public static final String RETREATFIGHT = "您已退出战斗";
    public static final String UNENOUGHMP = "您当前蓝量(MP)不足";
    public static final String UNSKILLCD = "你当前技能CD冷却中，请稍后重试";
    public static final String YOUARENOLEADER = "你无权带队进入副本，你不是队长";
    public static final String ENTERFIGHT = "你已进入战斗状态";
    public static final String BOSSAREASUCCESS = "副本攻略成功，热烈庆祝各位参与的小伙伴";
    public static final String BOSSFAIL = "挑战副本失败，人物已死光,按Y复活到起始之地";
    public static final String BOSSAREATIMEOUT = "时间结束挑战副本失败,你已退出副本世界，重刷副本请按F";

    public static final String GOODNOEXISTBAG = "背包中该物品为空";
    public static final String GOODNOEXIST = "不存在该ID的物品";
    public static final String NOEQUIPGOOD = "您未装备该武器";
    public static final String NOBELONGTOEQUIP = "该装备不属于穿戴物品";

    public static final String NOTEAMMESSAGE = "你还没有队伍，可以输入t-create创建队伍或者t-add-（已有队伍的玩家名）加入队伍";
    public static final String INTEAMNOCREATETEAM = "你已在队伍中无法创建队伍";
    public static final String CREATETEAMSUCCESSMESSAGE = "你成功创建队伍";
    public static final String NOINTEAMERRORMESSAGE = "你不在队伍中请不要做无效操作";
    public static final String DISSOLUTIONTEAM = "你已经解散当前队伍";
    public static final String SIGNOUTTEAM = "你已退出当前队伍";
    public static final String NOFOUNDTEAM = "你所要加入的玩家队伍不存在";

    public static final String EMPTYEMAIL = "您当前的邮箱为空";
    public static final String NOEMAILUSER = "系统无此用户，无法发送邮件";
    public static final String SUCCESSSENDEMIAL = "邮件发送成功";
    public static final String RECEIVEEMAILSUCCESS = "成功接收邮件";
    public static final String RECEIVEEMAILFAIL = "接收邮件编号不存在";
    public static final String NOFOUNDPKPERSON = "你要PK的玩家不存在";
    public static final String NOPKSELF = "PK对象不能是自己";
    public static final String NOKEYSKILL = "您无此键位的技能";


    public static final String DEADNOOPERATE = "请不要做死亡后的无效操作";
    public static final String SELECTLIVEWAY = "你可以选择Y复活到起始之地,或者在商城购买复活符按P立刻原地复活";
    public static final String LIVEINSTART = "你已在起始之地复活";
    public static final String NOONLINEUSER = "用户离线无法收到聊天消息";
    public static final String NOSUPPORTREMOTEPK = "不支持跨场景pk";
    public static final String RESURRECTIONNOPK = "起始之地不允许玩家pk";
    public static final String DEADNOOPERATEINBOSSAREA = "您已在副本中死亡，请不要做无效操作";
    public static final String ALLDEADINBOSSAREA = "全队已死亡，挑战副本失败";
    public static final String REBRORNANDCONNECTBOSSAREA = "你复活后重新加入当前副本";
    public static final String DONOTATTACKDEADMONSTER = "无法攻击死亡的怪物";


    public static final String SLEEPMESSAGE = "你已被怪物击晕，等待时间即可攻击，也可在商城购买免击晕符";
}
