package core.config;

import com.google.protobuf.MessageLite;

/**
 * @ClassName GrobalConfig
 * @Description 信息文字配置
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class MessageConfig {
    public static final String LOGIN_MESSAGE = "请输入：login=用户名=密码";
    public static final String REGISTER_MESSAGE = "请输入：register=用户名=密码=确认密码=种族编号[1:星武 2:星魂 3:星法 4:星宠师]进行注册";
    public static final String ERROR_PASSWORD = "账户密码出错";
    public static final String DOUBLE_PASSWORD_ERROR = "两次密码不一致";
    public static final String REGISTER_SUCCESS = "注册成功请输入：login=用户名=密码进行登录";
    public static final String NO_ROLE = "请输入有效的种族";
    public static final String REPEAT_USERNAME = "重复的用户名";

    public static final String YOU_NO_MOVE_LOCAL = "原地无需移动";
    public static final String NO_LEVEL_TO_MOVE = "等级不够无法移动";
    public static final String NO_TARGET_TO_MOVE = "移动地点不存在";
    public static final String REMOTE_MOVE_MESSAGE = "请充值才能启用传送门";
    public static final String NO_FOUND_NPC = "找不到此NPC";
    public static final String NO_ENOUGH_CHANGE_GOOD = "所要交换的物品不足够";

    public static final String SKILL_VIEW_MESG = "请输入viewSkill查看技能，请输入change=技能名=键位配置技能,请输入quitSkill退出技能管理界面";

    public static final String ERROR_ORDER = "您输入的指令有误,请输入有效的指令";
    public static final String FAIL_GOOD_ID = "请输入有效的物品ID";
    public static final String UNENOUGH_MONEY = "您的金钱不足，请充值";
    public static final String NO_USERBAG_ID = "背包ID不存在";
    public static final String NO_TO_UP_STARTLEVEL = "此物品无法升星";
    public static final String HAS_HAT_EQUIP = "完成帽子的替换";
    public static final String HAS_CORE_EQUIP = "完成主武器的替换";

    public static final String RETREAT_FIGHT = "您已退出战斗";
    public static final String UNENOUGH_MP = "您当前蓝量(MP)不足";
    public static final String NO_FOUND_MONSTER = "找不到该怪物";
    public static final String NO_SKILL_CD = "你当前技能CD冷却中，请稍后重试";
    public static final String YOU_ARE_NO_LEADER = "你无权带队进入副本，你不是队长";
    public static final String ENTER_FIGHT = "你已进入战斗状态";
    public static final String BOSS_FAIL = "挑战副本失败，人物已死光";
    public static final String BOSS_AREA_TIME_OUT = "时间结束挑战副本失败,你已退出副本世界，重刷副本请按F";

    public static final String GOOD_NO_EXIST_BAG = "背包中该物品为空";
    public static final String GOOD_NO_EXIST = "不存在该ID的物品";
    public static final String NO_EQUIP_GOOD = "您未装备该武器";

    public static final String NO_TEAM_MESSAGE = "你还没有队伍，可以输入tcreate创建队伍或者tadd=（已有队伍的玩家名）加入队伍";
    public static final String IN_TEAM_NO_CREATE_TEAM = "你已在队伍中无法创建队伍";
    public static final String NO_FOUND_TEAM = "你查找的队伍不存在";
    public static final String CREATE_TEAM_SUCCESS_MESSAGE = "你成功创建队伍";
    public static final String NO_IN_TEAM_ERROR_MESSAGE = "你不在队伍中请不要做无效操作";
    public static final String DISSOLUTION_TEAM = "你已经解散当前队伍";
    public static final String SIGN_OUT_TEAM = "你已退出当前队伍";
    public static final String SUCCESS_TO_APPLY = "你成功申请加入该队伍";
    public static final String SUCCESS_ENTER_TEAM = "你成功加入当前队伍";
    public static final String YOU_ARE_IN_TEAM = "你已在队伍中，请退出队伍再加入";
    public static final String NO_FOUND_TEAM_APPLY_INFO = "请输入正确的队伍申请单记录";
    public static final String USER_NO_ONLINE_TO_ADD_TEAM = "玩家掉线，无法加入队伍";
    public static final String ENTER_TEAM_MANAGER_VIEW = "欢迎进入团队管理界面";
    public static final String OUT_TEAM_VIEW = "你已退出队伍管理界面";

    public static final String EMPTY_EMAIL = "您当前的邮箱为空";
    public static final String NO_EMAIL_USER = "系统无此用户，无法发送邮件";
    public static final String SUCCESSS_END_EMIAL = "邮件发送成功";
    public static final String RECEIVE_EMAIL_SUCCESS = "成功接收邮件";
    public static final String RECEIVE_EMAIL_FAIL = "接收邮件编号不存在";
    public static final String NO_FOUND_PK_PERSON = "你要PK的玩家不存在";
    public static final String NO_PK_SELF = "PK对象不能是自己";
    public static final String NO_KEY_SKILL = "您无此键位的技能";


    public static final String SELECT_LIVE_WAY = "你可以选择Y复活到起始之地,或者在商城购买复活符按P立刻原地复活";
    public static final String LIVE_IN_START = "你已在起始之地复活";
    public static final String NO_ONLINE_USER = "用户离线无法收到聊天消息";
    public static final String NO_SUPPORT_REMOTE_PK = "不支持跨场景pk";
    public static final String RESURRECTION_NO_PK = "起始之地不允许玩家pk";
    public static final String DEAD_NOALLOW_CONNECT_BOSSAREA = "你复活后无法重新加入当前副本,你可以等待副本结束或者退出队伍";
    public static final String SOMEBODY_DEAD = "队伍中有人死亡，无法进入副本，请选择t出玩家或者让玩家自己复活";


    public static final String SLEEP_MESSAGE = "你已被怪物击晕，等待时间即可攻击，也可在商城购买免击晕符";
    public static final String ONE_MAN_REMOVE_TEAM = "只有一个玩家，自动解散队伍";
    public static final String NO_TRACE_USER = "无此交易用户";
    public static final String NO_TRADE_RECORD = "无此交易记录";
    public static final String TRADEING = "玩家正在交易中无法进行交易";
    public static final String SUCCESS_CREATE_TRADE = "成功建立交易";
    public static final String SUCCESS_TRADE_END = "交易结束，双方完成交易";
    public static final String FAIL_TRADE_END = "一方终止了交易，交易失败";
    public static final String TRADE_NO_ENOUGH_MONEY = "你的个人金额少于交易金额，请重新修改你的交易金额";
    public static final String NO_ENOUGH_MONEY_TO_RESET = "你撤回的金额不能超过放在交易单上的金额";
    public static final String OTHER_TRADE_IS_ING = "所交易的用户已和他人在交易中";
    public static final String YOU_TRADE_IS_ING = "你当前还有未处理完成的交易记录，请使用ntrade取消你当前的交易记录再进行交易";
    public static final String NO_CREATE_TRADE = "你还没有建立交易，无法取消";
    public static final String CANCEL_TRADE = "你已取消交易申请";
    public static final String REPEAT_YES_TRADE = "您已确认了此次交易,请不要瞎操作";
    public static final String YOU_COMFIRM_TRADE = "你确认了此次交易";
    public static final String NO_ENOUGH_GOOD_FOR_TRADE = "请输入正确的交易物品数量";
    public static final String TRADE_MSG = "欢迎来到交易界面,jbjy=金币数额 可以增加所要交易的金币,xjbjy=金币数额 可以减少所要交易的金币,jyg=交易格子号可以把交易物品填充到交易栏," +
            System.getProperty("line.separator") + "xjyg=交易格子号可以把物品从交易格子取下来，jyy确认交易物品，jyq可以取消交易，双方都确认后交易完成";


    public static final String MESSAGE_START = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
    public static final String MESSAGE_END = "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
    public static final String MESSAGE_MID = "===========================================================================================================";

    public static final String ENTER_LABOUR_VIEW = "你已进入工会管理界面";
    public static final String OUT_LABOUR_VIEW = "你已退出工会管理界面";
    public static final String YOU_ARE_NO_UNON = "你还没有工会";
    public static final String UNION_MSG = "[tg=用户id 踢出某个成员][lsg 查看入会申请] [gy=申请id 同意入会申请] [gn=申请id 否决入会申请][jxjb=金币数额] [qtg 退出工会管理界面]"
            + System.getProperty("line.separator") + "[lgu查看已有的工会] [sqg=工会编号 申请加入工会] [cgu=工会名称 创建自己的工会] [zsry 展示工会所有成员信息] [zsck 展示工会仓库]" + System.getProperty("line.separator")
            + "[jxwp=格子编号=数量 捐献工会物品] [backg 退出工会] [sjg=工会成员id=等级 提升工会会员等级] [hq=格子编号=数量 取得工会仓库物品]" + System.getProperty("line.separator");
    public static final String NO_CREATE_UNION = "无法创建工会，你目前已有工会";
    public static final String REPEAT_UNION_NAME = "重复的工会名,无法创建公户";
    public static final String NO_OUT_UNION = "创始人无法退出工会，你可将工会移交他人再退出";
    public static final String NO_APPLY_UNION = "无法加入工会，你已有工会";
    public static final String SUCCESS_UNION_APPLY = "成功创建加入工会申请";
    public static final String NO_EXIST_UNION_ID = "不存在该id的工会";
    public static final String NO_REPEAT_UNION_APPLY = "请不要重复的提交加入同一个工会申请记录";
    public static final String NO_ENOUGH_POWER_IN_UNION = "工会权限不足";
    public static final String NO_APPLY_INFO = "不存在该申请记录";
    public static final String DISAGREE_USE_APPLY = "你拒绝了用户的申请";
    public static final String NO_USER = "无此用户";
    public static final String ERROR_USERBAG_NUM = "请输入正确的物品数量";
    public static final String SUCCESS_GIVE_GOOD_TO_UNION = "捐献成功";
    public static final String SUCCESS_GET_UNION_GOOD = "获取物品成功";
    public static final String NO_ENOUGH_MONEY_TO_GIVE = "你的金币数额不足";
    public static final String SUCCESS_GIVE_MONEY_TO_UNION = "成功向工会捐献了金币";
    public static final String SUCCESS_OUT_UNION = "成功退出工会";
    public static final String SHOW_UNION_MEN = "用户名 [ %s ] 用户工会等级 [ %s ] " + System.getProperty("line.separator");

    public static final String FRIEND_MSG = "欢迎来到好友管理界面，sqfriend=用户名申请新的好友，lsfriend 查看好友申请"
            + "removefriend=好友名称 解除好友关系" + System.getProperty("line.separator")
            + "lufriend 查看已有好友 tyf=申请编号同意好友申请 tnf=否决好友申请" + "qfriend 退出好友管理界面" + System.getProperty("line.separator");
    public static final String ENTER_FRIEND_VIEW = "已进入好友管理界面";
    public static final String OUT_FRIEND_VIEW = "已退出好友管理界面";
    public static final String NO_FOUND_MAN = "系统无此玩家";
    public static final String NO_FRIEND = "您目前还没有好友";
    public static final String NO_FRIEND_RECORD = "无此好友申请记录";

    public static final String NO_ENOUGH_MAN_TO_FIGHT = "三层boss需多人组队才能挑战，游戏结束";
    public static final String OUT_BOSS_AREA = "你成功退出副本";
    public static final String NO_RECOVER_USER_HP_AND_MP = "用户的血量和蓝量无法恢复";
    public static final String RECOVRE_SUCCESS = "人物的蓝量和血量恢复成功";
    public static final String NO_EXACHANGE_FOR_NPC = "该npc无法进行交换物品";

    public static final String ENTER_AUCTION_VIEW = "欢迎进入拍卖行,queryau 刷新拍卖行,auqp=id=价格 编号参与拍卖（一口价商品直接拍下不用输入价格）," +
            "ausj=背包格子=数量=金钱=是否竞拍（1：否，2 是） 上架拍卖行,qau 退出拍卖，aoi 查看人物金钱信息 auxj=拍卖行id 下架拍卖行";
    public static final String OUT_AUCTION_VIEW = "你退出了拍卖行";
    public static final String NO_AUCTION_ITEMS = "拍卖行无任何交易物品";
    public static final String SUCCESS_UP_AUCTION_ITEM = "成功上架物品";
    public static final String NO_THIS_AUCTION_ITEM = "拍卖行无此交易物品";
    public static final String SUCCESS_BUY_GOOD_IMMEDIATE = "一口价竞拍成功，请在背包查看物品";
    public static final String SUCCESS_BUY_GOOD_UP_PRICE = "成功竞价，拍卖结束后成功竞拍者可在仓库查看物品";
    public static final String NO_ENOUGH_MONEY_FOR_AUCTION = "参与竞拍的价格不得低于竞拍单的现价";
    public static final String AUCTION_IS_END = "拍卖结束";
    public static final String NO_AUCTION_FOR_SELF = "用户不能拍卖自己的物品";
    public static final String NO_PK_DEAD_MAN = "无法pk已死亡的玩家";
    public static final String NO_TEAM_RECORD = "无玩家队伍记录";
    public static final String NO_TEAM_NO_TALK = "无队伍无法队伍聊天";
    public static final String FINISH_TASK = "[------已完成的任务-----]" + System.getProperty("line.separator");
    public static final String DOING_TASK = "[-----正在进行中的任务-----]" + System.getProperty("line.separator");
    public static final String NPC_NO_TASK = "该npc无任务可接受";
    public static final String NO_FOUND_TASK = "找不到该任务";
    public static final String NO_ACCEPT_REPEAT = "不可重复接受该任务";
    public static final String SUCCESS_ACCEPT_TASK = "成功接受任务";
    public static final String NO_REPEAT_REWARD_TASK = "任务没有完成或者你重复想获取奖励，不行哦";
    public static final String SUCCESS_REWARD_TASK = "成功交付任务获得奖励";
    public static final String IF_ENTER_BOSSAREA = "是否随队长加入副本，同意请输入yenterb";
    public static final String YOU_ALREADY_AGREE_VOTE = "你已经同意投票入队，请勿做其他操作";
    public static final String ALREADY_ENTER_BOSSAREA = "你已同意加入副本";
    public static final String ENTER_BOSSAREA_BEGIN_VOTE = "进入副本开始投票";
}
