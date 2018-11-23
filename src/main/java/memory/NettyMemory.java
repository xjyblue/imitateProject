package memory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import buff.Buff;
import component.*;
import email.Mail;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import pojo.User;
import pojo.Userskillrelation;
import skill.UserSkill;
import team.Team;

/**
 * @author xiaojianyu
 */
public class NettyMemory {
	public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	/** 缓存通信上下文环境对应的登录用户 */ 
	public static Map<Channel, User> session2UserIds  = new ConcurrentHashMap<Channel,User>();
	/** 根据用户拿去对应的渠道 */
	public static Map<User,Channel> userToChannelMap  = new ConcurrentHashMap<User,Channel>();
	/** 信道所处事件的装填*/
	public static Map<Channel,String>eventStatus  = new ConcurrentHashMap<Channel,String>();
	/** 地图缓存到内存中 */
	public static Map<String,Area>areaMap = new HashMap<String,Area>();
	/** 地图名称和编号缓存到内存中 */
	public static Map<String,String>areaToNum = new HashMap<String,String>();
	/** 初始化全局技能伤害*/
	public static Map<Integer,UserSkill>SkillMap = new HashMap<Integer, UserSkill>();
	/**初始化地图Set集合*/
	public static Set<String>areaSet = new HashSet<String>();
	/**初始化channel人物回蓝定时人物*/
	public static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
	/**初始化玩家技能*/
	public static Map<Channel, Map<String,Userskillrelation>> userskillrelationMap = new ConcurrentHashMap<Channel, Map<String,Userskillrelation>>();
	/**缓存用户所攻击的怪兽*/
	public static Map<User, List<Monster>>monsterMap = new HashMap<>();
	/**初始化Buff的截止时间*/
	public static Map<User,Map<String,Long>> buffEndTime = new ConcurrentHashMap<>();
	/**初始化药物属性*/
	public static Map<Integer, MpMedicine> mpMedicineMap = new HashMap<>();
	/**初始化武器*/
	public static Map<Integer, Equipment>equipmentMap = new HashMap<>();
	/**初始化全局武器特殊buf效果*/
	public static Map<Integer, Buff> buffMap = new HashMap<>();
	/**记录玩家队伍*/
	public static Map<String, Team> teamMap = new ConcurrentHashMap<>();
	/**每个boss副本和teamId挂钩*/
	public static Map<String, BossArea> bossAreaMap = new ConcurrentHashMap<>();
	/**记录每个队伍挑战副本的截止时间，超过时间就GG*/
	public static Map<String,Long> endBossAreaTime = new ConcurrentHashMap<>();
	/** 副本boss定时攻击的任务*/
	public static ScheduledExecutorService bossAreaThreadPool = Executors.newScheduledThreadPool(5);
	/** 普通小怪定时攻击的任务*/
	public static ScheduledExecutorService monsterThreadPool = Executors.newScheduledThreadPool(5);
	/** 辅助定时任务关闭的工具类*/
	public static ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<String, Future>();
	/** 缓存邮件信息 */
	public static Map<String,ConcurrentHashMap<String, Mail>> userEmailMap = new ConcurrentHashMap<String,  ConcurrentHashMap<String, Mail>>();
}
