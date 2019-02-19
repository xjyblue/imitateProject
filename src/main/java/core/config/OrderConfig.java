package core.config;

/**
 * @ClassName OrderConfig
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/15 11:22
 * @Version 1.0
 **/
public class OrderConfig {
    /**
     * 选择登陆操作
     */
    public static final String SELECT_LOGIN_ORDER = "dl";
    /**
     * 选择注册操作
     */
    public static final String SELECT_REGISTER_ORDER = "zc";


    /**
     * 登录
     */
    public static final String LOGIN_ORDER = "login";
    /**
     * 注册
     */
    public static final String REGISTER_ORDER = "register";


    /**
     * 退出普通战斗
     */
    public static final String QUIT_COMMON_FIGHT_ORDER = "cqf";
    /**
     * 退出副本战斗
     */
    public static final String QUIT_BOSS_FIGHT_CONFIG_ORDER = "bqf";
    /**
     * 转移攻击目标
     */
    public static final String CHANGE_FIGHT_TARGET_ORDER = "attack";
    /**
     * 战斗中回复血量
     */
    public static final String RECOVER_STATUS_BOSSAREA_ORDER = "recover";
    /**
     * 第一次普通场景的攻击
     */
    public static final String FIRST_COMMON_FIGHT_ORDER = "cattack";
    /**
     * 副本场景的第一次攻击
     */
    public static final String FIRST_BOSS_FIGHT_ATTACK_ORDER = "battack";


    /**
     * 进入拍卖行
     */
    public static final String ENTER_AUTION_VIEW_ORDER = "eau";
    /**
     * 退出拍卖行
     */
    public static final String QUIT_AUCTION_ORDER = "qau";
    /**
     * 展示拍卖行
     */
    public static final String SHOW_AUTION_ITEM_ORDER = "queryau";
    /**
     * 上架拍卖行
     */
    public static final String UP_AUCTION_ITEM_ORDER = "ausj";
    /**
     * 参与竞拍
     */
    public static final String GET_AUCTION_ITEM_ORDER = "auqp";


    /**
     * 进入boss副本
     */
    public static final String ENTER_BOSSAREA_CONFIG = "ef";
    /**
     * 同意进入boss副本
     */
    public static final String AGREE_ENTER_BOSSAREA_ORDER = "yenterb";


    /**
     * 和多数人聊天
     */
    public static final String CHAT_ALL_ORDER = "chatAll";
    /**
     * 和单个人聊天
     */
    public static final String CHAT_ONE_ORDER = "chatOne";
    /**
     * 团队聊天
     */
    public static final String CHAT_TEAM_ORDER = "chatTeam";

    /**
     * 重新复活
     */
    public static final String REBORN_ORDER = "y";

    
    /**
     * 展示所有邮件
     */
    public static final String SHOW_EAMIL_ORDER = "qmail";
    /**
     * 发送邮件
     */
    public static final String SEND_EMAIL_ORDER = "sendmail";
    /**
     * 接受邮件
     */
    public static final String RECEIVE_EMAIL_ORDER = "receivemail";

    /**
     * 进入朋友管理界面
     */
    public static final String ENTER_FRIEND_VIEW_ORDER = "efriend";
    /**
     * 退出朋友管理界面
     */
    public static final String QUIT_FRIEND_VIEW_ORDER = "qfriend";
    /**
     * 同意交友
     */
    public static final String AGREE_GET_NEW_FRIEND_ORDER = "tyf";
    /**
     * 展示所有好友
     */
    public static final String SHOW_ALL_FRIEND_ORDER = "lufriend";
    /**
     * 申请好友
     */
    public static final String ASK_FRIEND_ORDER = "sqfriend";
    /**
     * 展示新的好友请求
     */
    public static final String SHOW_NEW_FRIEND_REQUEST_ORDER = "lsfriend";
    /**
     * 移除好友
     */
    public static final String REMOVE_FRIEND_ORDER = "removefriend";


    /**
     * 装备升星
     */
    public static final String UP_WEAPON_START_LEVEL_ORDER = "iu";
    /**
     * 展示装备栏
     */
    public static final String SHOW_WEAPON_MESG_ORDER = "qw";
    /**
     * 维修装备
     */
    public static final String FIX_EQUIPMENT_ORDER = "fix";
    /**
     * 卸下装备
     */
    public static final String TAKE_OFF_WEAPON_ORDER = "wq";
    /**
     * 穿戴装备
     */
    public static final String TAKE_ON_WEAPON_ORDER = "ww";


    /**
     * aoi指令
     */
    public static final String AOI_METHOD_ORDER = "aoi";
    /**
     * 使用背包格子的物品
     */
    public static final String USE_USERBAG_ORDER = "ub";
    /**
     * 展示背包格子的信息
     */
    public static final String SHOW_USERBAG_INFO = "qb";


    /**
     * 不同意交易
     */
    public static final String DISAGREE_TRADE_ORDER = "ntrade";
    /**
     * 同意交易
     */
    public static final String AGREE_TRADE_ORDER = "ytrade";
    /**
     * 请求交易
     */
    public static final String TRADE_REQUEST_ORDER = "iftrade";
    /**
     * 交易台上取消交易
     */
    public static final String TRADING_CANCEL_TRADE_ORDER = "jyq";
    /**
     * 交易台上同意交易
     */
    public static final String TRADING_AGREE_TRADE_ORDER = "jyy";
    /**
     * 交易台上减少金钱
     */
    public static final String TRADING_REDUCE_MONEY_ORDER = "xjbjy";
    /**
     * 交易台上增加金钱
     */
    public static final String TRADING_ADD_MONEY_ORDER = "jbjy";
    /**
     * 交易台上增加交易物品
     */
    public static final String TRADING_ADD_GOOD_ORDER = "jyg";
    /**
     * 交易台上取消减少交易物品
     */
    public static final String TRADING_REMOVE_GOOD_ORDER = "xjyg";


    /**
     * 展示队伍信息
     */
    public static final String SHOW_TEAM_INFO_ORDER = "team";
    /**
     * 创建队伍
     */
    public static final String CREATE_TEAM_ORDER = "tcreate";
    /**
     * 解散队伍
     */
    public static final String REMOVE_TEAM_ALL_MEMBER_ORDER = "tremove";
    /**
     * 退出队伍
     */
    public static final String BACK_TEAM_ORDER = "tback";
    /**
     * 加入新队伍
     */
    public static final String REQUEST_NEW_TEAM_ORDER = "tadd";
    /**
     * 同意某个玩家加入新队伍
     */
    public static final String AGREE_ADD_NEW_TEAM_ORDER = "ty";
    /**
     * 展示所有申请者的信息
     */
    public static final String SHOW_ALL_TEAM_APPLY_INFO_ORDER = "tlu";
    /**
     * 展示所有可申请队伍信息
     */
    public static final String SHOW_ALL_TEAM_INFO_ORDER = "showteam";
    /**
     * 进入队伍管理界面
     */
    public static final String ENTER_TEAM_VIEW_ORDER = "eteam";
    /**
     * 退出队伍管理界面
     */
    public static final String QUIT_TEAM_VIEW_ORDER = "qteam";


    /**
     * 查看用户技能
     */
    public static final String LOOK_USER_SKILL_ORDER = "viewSkill";
    /**
     * 改变用户技能键位
     */
    public static final String CHANGE_USER_SKILL_KEY_ORDER = "change";
    /**
     * 退出用户技能管理界面
     */
    public static final String QUIT_USER_SKILL_VIEW_ORDER = "quitSkill";
    /**
     * 进入用户技能管理界面
     */
    public static final String ENTER_USER_SKILL_VIEW_ORDER = "enterSkill";


    /**
     * 展示商城信息
     */
    public static final String SHOW_SHOP_INFO_ORDER = "qshop";
    /**
     * 购买商城物品信息
     */
    public static final String BUY_SHOP_GOOD_ORDER = "bshop";


    /**
     * 场景移动
     */
    public static final String MOVE_SCENE_ORDER = "move";


    /**
     * 人物pk的命令
     */
    public static final String PK_ORDER = "pk";


    /**
     * 和npc进行交流
     */
    public static final String NPC_TALK_ORDER = "npcTalk";
    /**
     * 和npc获取装备
     */
    public static final String NPC_GET_EQUIP_ORDER = "npcGet";
    /**
     * 查看npc有哪些可以接受的任务
     */
    public static final String LOOK_NPC_ALL_TASK_ORDER = "npcTaskLook";
    /**
     * 向npc接受任务
     */
    public static final String GET_TASK_FROM_NPC_ORDER = "npcTaskReceive";
    /**
     * 和npc获取任务奖励
     */
    public static final String RECEIVE_TASK_REWARD_FROM_NPC_ORDER = "npcTaskReward";


    /**
     * 捐献金币给工会
     */
    public static final String ADD_MONEY_TO_UNION_ORDER = "jxjb";
    /**
     * 从工会获取物品到用户背包
     */
    public static final String GET_UNION_GOOD_TO_USERBAG_ORDER = "hq";
    /**
     * 捐献物品给工会
     */
    public static final String GIVE_GOOD_TO_UNION_ORDER = "jxwp";
    /**
     * 展示工会仓库物品
     */
    public static final String SHOW_UNION_GOODS_ORDER = "zsck";
    /**
     * 从工会中踢出某个玩家
     */
    public static final String REMOVE_MENBER_FROM_UNION_ORDER = "tg";
    /**
     * 拒绝玩家加入工会
     */
    public static final String DISAGREE_MAN_ENTER_UNION_ORDER = "gn";
    /**
     * 修改玩家工会等级
     */
    public static final String CHANGE_USER_UNION_LEVEL_ORDER = "sjg";
    /**
     * 展示工会人员信息
     */
    public static final String SHOW_UNION_MEN_INFO_ORDER = "zsry";
    /**
     * 同意玩家加入工会
     */
    public static final String AGREE_MAN_ENTER_UNION_ORDER = "gy";
    /**
     * 展示所有玩家申请信息
     */
    public static final String SHOW_ALL_UNION_APPLY_INFO_ORDER = "lsg";
    /**
     * 玩家申请加入工会
     */
    public static final String APPLY_ENTER_UNION_ORDER = "sqg";
    /**
     * 玩家退出工会
     */
    public static final String BACK_UNION_ORDER =  "backg";
    /**
     * 玩家创建工会
     */
    public static final String CREATE_UNION_ORDER = "cgu";
    /**
     * 展示所有可加入的工会
     */
    public static final String SHOW_ALL_UNION_INFO_TO_APPLY_ORDER = "lgu";
    /**
     * 进入工会管理界面
     */
    public static final String ENTER_UNION_ORDER = "eg";
    /**
     * 退出工会管理界面
     */
    public static final String QUIT_UNION_ORDER = "qtg";


    /**
     * 展示所有抽奖的方式
     */
    public static final String SHOW_ALL_LUCKY_DRAW = "showLuckyDraw";
    /**
     * 开始抽奖
     */
    public static final String START_LUCKY_DRAW_ORDER = "luckyDraw";
}
