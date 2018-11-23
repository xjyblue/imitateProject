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
}
