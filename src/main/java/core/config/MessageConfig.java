package core.config;

/**
 * @ClassName GrobalConfig
 * @Description 信息文字配置
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class MessageConfig {
    public static final String LOGINMESSAGE = "请输入：用户名-密码";
    public static final String REGISTERMESSAGE = "请输入：用户名-密码-确认密码-种族编号[1:星武 2:星魂 3:星法 4:星宠师]进行注册";
    public static final String ERRORPASSWORD = "账户密码出错";
    public static final String DOUBLEPASSWORDERROR = "两次密码不一致";
    public static final String REGISTERSUCCESS = "注册成功请输入：用户名-密码进行登录";
    public static final String NOROLE = "请输入有效的种族";

    public static final String UNMOVELOCAL = "原地无需移动";
    public static final String NOLEVELTOMOVE = "等级不够无法移动";
    public static final String NOTARGETTOMOVE = "移动地点不存在";
    public static final String REMOTEMOVEMESSAGE = "请充值才能启用传送门";
    public static final String NOFOUNDNPC = "找不到此NPC";
    public static final String NOENOUGHCHANGEGOOD = "所要交换的物品不足够";

    public static final String SKILLVIEWMESG = "请输入lookSkill查看技能，请输入change-技能名-键位配置技能,请输入quitSkill退出技能管理界面";

    public static final String ERRORORDER = "您输入的指令有误,请输入有效的指令";
    public static final String FAILGOODID = "请输入有效的物品ID";
    public static final String UNENOUGHMONEY = "您的金钱不足，请充值";
    public static final String NOUSERBAGID = "背包ID不存在";
    public static final String NOTOUPSTARTLEVEL = "此物品无法升星";
    public static final String HASHATEQUIP = "请卸下你穿戴的帽子再进行穿戴";
    public static final String HASCOREEQUIP = "请卸下你的主武器再进行装备";

    public static final String RETREATFIGHT = "您已退出战斗";
    public static final String UNENOUGHMP = "您当前蓝量(MP)不足";
    public static final String NOFOUNDMONSTER = "找不到该怪物";
    public static final String UNSKILLCD = "你当前技能CD冷却中，请稍后重试";
    public static final String YOUARENOLEADER = "你无权带队进入副本，你不是队长";
    public static final String ENTERFIGHT = "你已进入战斗状态";
    public static final String BOSSFAIL = "挑战副本失败，人物已死光,按Y复活到起始之地";
    public static final String BOSSAREATIMEOUT = "时间结束挑战副本失败,你已退出副本世界，重刷副本请按F";

    public static final String GOODNOEXISTBAG = "背包中该物品为空";
    public static final String GOODNOEXIST = "不存在该ID的物品";
    public static final String NOEQUIPGOOD = "您未装备该武器";
    public static final String NOBELONGTOEQUIP = "该装备不属于穿戴物品";

    public static final String NOTEAMMESSAGE = "你还没有队伍，可以输入t-create创建队伍或者t-add-（已有队伍的玩家名）加入队伍";
    public static final String INTEAMNOCREATETEAM = "你已在队伍中无法创建队伍";
    public static final String NOFOUNDTEAM = "你查找的队伍不存在";
    public static final String CREATETEAMSUCCESSMESSAGE = "你成功创建队伍";
    public static final String NOINTEAMERRORMESSAGE = "你不在队伍中请不要做无效操作";
    public static final String DISSOLUTIONTEAM = "你已经解散当前队伍";
    public static final String SIGNOUTTEAM = "你已退出当前队伍";
    public static final String SUCCESSTOAPPLY = "你成功申请加入该队伍";
    public static final String SUCCESSENTERTEAM = "你成功加入当前队伍";
    public static final String YOUARENINTEAM = "你已在队伍中，请退出队伍再加入";
    public static final String NOFOUNDTEAMAPPLYINFO = "请输入正确的队伍申请单记录";
    public static final String USERNOONLINETOADDTEAM = "玩家掉线，无法加入队伍";
    public static final String ENTERTEAMMANAGERVIEW = "欢迎进入团队管理界面";
    public static final String OUTTEAMVIEW = "你已退出队伍管理界面";

    public static final String EMPTYEMAIL = "您当前的邮箱为空";
    public static final String NOEMAILUSER = "系统无此用户，无法发送邮件";
    public static final String SUCCESSSENDEMIAL = "邮件发送成功";
    public static final String RECEIVEEMAILSUCCESS = "成功接收邮件";
    public static final String RECEIVEEMAILFAIL = "接收邮件编号不存在";
    public static final String NORECEIVEEMAIL = "该邮件无附带物品无法接受";
    public static final String NOFOUNDPKPERSON = "你要PK的玩家不存在";
    public static final String NOPKSELF = "PK对象不能是自己";
    public static final String NOKEYSKILL = "您无此键位的技能";


    public static final String DEADNOOPERATE = "请不要做死亡后的无效操作";
    public static final String SELECTLIVEWAY = "你可以选择Y复活到起始之地,或者在商城购买复活符按P立刻原地复活";
    public static final String LIVEINSTART = "你已在起始之地复活";
    public static final String NOONLINEUSER = "用户离线无法收到聊天消息";
    public static final String NOSUPPORTREMOTEPK = "不支持跨场景pk";
    public static final String RESURRECTIONNOPK = "起始之地不允许玩家pk";
    public static final String REBRORNANDCONNECTBOSSAREA = "你复活后重新加入当前副本";
    public static final String DONOTATTACKDEADMONSTER = "无法攻击死亡的怪物";
    public static final String SOMEBODYDEAD = "队伍中有人死亡，无法进入副本，请选择t出玩家或者让玩家自己复活";


    public static final String SLEEPMESSAGE = "你已被怪物击晕，等待时间即可攻击，也可在商城购买免击晕符";
    public static final String ONLEAVEFORREMOVE = "只有一个玩家，自动解散队伍";
    public static final String NOTRACEUSER = "无此交易用户";
    public static final String NOTRADERECORD = "无此交易记录";
    public static final String TRADEING = "玩家正在交易中无法进行交易";
    public static final String SUCCESSCREATETRADE = "成功建立交易";
    public static final String NOGOODINTRADE = "交易单号上无此物品";
    public static final String SUCCESSTRADEEND = "交易结束，双方完成交易";
    public static final String FAILTRADEEND = "一方终止了交易，交易失败";
    public static final String TRADENOENOUGHMONEY = "你的个人金额少于交易金额，请重新修改你的交易金额";
    public static final String NOENOUGHMONEYTORESET = "你撤回的金额不能超过放在交易单上的金额";
    public static final String TRADETARGETHASMAN = "所交易的用户已和他人在交易中";
    public static final String MANISINGTRADING = "你当前还有未处理完成的交易记录，请使用ntrade取消你当前的交易记录再进行交易";
    public static final String NOCREATETRADE = "你还没有建立交易，无法取消";
    public static final String CANCELTRADE = "你已取消交易申请";
    public static final String REPEATYESTRADE = "您已确认了此次交易,请不要瞎操作";
    public static final String YOUCOMFIRMTRADE = "你确认了此次交易";
    public static final String NOENOUGHGOODFORTRADE = "请输入正确的交易物品数量";
    public static final String TRADEMSG = "欢迎来到交易界面,jbjy=金币数额 可以增加所要交易的金币,jbjyx=金币数额 可以减少所要交易的金币,jy=交易格子号可以把交易物品填充到交易栏," +
            System.getProperty("line.separator") + "jyx=交易格子号可以把物品从交易格子取下来，jy=y确认交易物品，双方都确认后交易完成";


    public static final String MESSAGESTART = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
    public static final String MESSAGEEND = "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
    public static final String MESSAGEMID = "===========================================================================================================";

    public static final String ENTERLABOURVIEW = "你已进入工会管理界面";
    public static final String OUTLABOURVIEW = "你已退出工会管理界面";
    public static final String YOUARENOUNON = "你还没有工会";
    public static final String UNIONMSG = "[t=用户id 踢出某个成员][lsu 查看入会申请] [ls=y=申请id 同意入会申请] [ls=n=申请id 否决入会申请][jxjb-金币数额]"
            + System.getProperty("line.separator") + "[lu查看已有的工会] [sq=工会编号 申请加入工会] [cu-工会名称 创建自己的工会] [zsry 展示工会所有成员信息] [zsck 展示工会仓库]" + System.getProperty("line.separator")
            + "[jxwp=格子编号=数量 捐献工会物品] [tc 退出工会] [sj=工会成员id=等级 提升工会会员等级] [hq=格子编号=数量 取得工会仓库物品]" + System.getProperty("line.separator");
    public static final String NOCREATEUNION = "无法创建工会，你目前已有工会";
    public static final String NOOUTUNION = "创始人无法退出工会，你可将工会移交他人再退出";
    public static final String NOAPPLYUNION = "无法加入工会，你已有工会";
    public static final String SUCCESSUNIONAPPLY = "成功创建加入工会申请";
    public static final String NOEXISTUNIONID = "不存在该id的工会";
    public static final String NOREPEATUNIONAPPLY = "请不要重复的提交加入同一个工会申请记录";
    public static final String FOURZEROTHREE = "工会权限不足";
    public static final String NOAPPLYINFO = "不存在该申请记录";
    public static final String DISAGREEUSEAPPLY = "你拒绝了用户的申请";
    public static final String NOUSER = "无此用户";
    public static final String ERRORUSERBAGNUM = "请输入正确的物品数量";
    public static final String SUCCESSGIVEGOODTOUNION = "捐献成功";
    public static final String SUCCESSGETUNIONGOOD = "获取物品成功";
    public static final String NOENOUGHMONEYTOGIVE = "你的金币数额不足";
    public static final String SUCCESSGIVEMONEYTOUNION = "成功向工会捐献了金币";

    public static final String FRIENDMSG = "欢迎来到好友管理界面，sq-用户名申请新的好友，ls 查看好友申请" + System.getProperty("line.separator")
            + "lu 查看已有好友 ty=申请编号同意好友申请 tn=否决好友申请" + System.getProperty("line.separator");
    public static final String ENTERFRIENDVIEW = "已进入好友管理界面";
    public static final String OUTFRIENDVIEW = "已退出好友管理界面";
    public static final String NOFOUNDMAN = "系统无此玩家";
    public static final String NOFRIEND = "您目前还没有好友";
    public static final String NOFRIENDRECORD = "无此好友申请记录";

    public static final String NOENOUGHMANTOFIGHT = "三层boss需多人组队才能挑战，游戏结束";
    public static final String OUTBOSSAREA = "你成功退出副本";
    public static final String NORECOVERUSERHPANDMP = "用户的血量和蓝量无法恢复";
    public static final String RECOVRESUCCESS = "人物的蓝量和血量恢复成功";
    public static final String MANDEAD = "人物已死亡，无法进行攻击";
    public static final String NOEXACHANGEFORNPC = "该npc无法进行交换物品";
}
