package context;
import java.util.*;
import java.util.concurrent.*;

import achievement.Achievement;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import component.parent.Good;
import level.Level;
import role.Role;
import buff.Buff;
import component.*;
import email.Mail;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import pojo.User;
import pojo.Userskillrelation;
import skill.MonsterSkill;
import skill.UserSkill;
import team.Team;
import trade.Trade;

/**
 * @author server
 */
public class ProjectContext {

//	此处用户和通道发生关联
	/** 缓存通信上下文环境对应的登录用户 */ 
	public static Map<Channel, User> session2UserIds  = Maps.newConcurrentMap();
	/** 根据用户拿去对应的渠道 */
	public static Map<User,Channel> userToChannelMap  = Maps.newConcurrentMap();


	/** 收集类物品的id */
	public static Map<Integer, CollectGood> collectGoodMap = Maps.newConcurrentMap();
	/** 信道所处事件的装填*/
	public static Map<Channel,String>eventStatus  = Maps.newConcurrentMap();
	/** 地图缓存到内存中 */
	public static Map<String, Scene> sceneMap = Maps.newHashMap();
	/** 初始化全局技能伤害*/
	public static Map<Integer,UserSkill> skillMap = Maps.newHashMap();
	/**初始化地图Set集合*/
	public static Set<String> sceneSet = Sets.newHashSet();
	/**初始化 地图线程池**/
	public static ExecutorService sceneThreadPool = Executors.newFixedThreadPool(4);
	/**初始化玩家技能*/
	public static Map<User, Map<String,Userskillrelation>> userskillrelationMap = Maps.newConcurrentMap();
	/**缓存用户所攻击的怪兽*/
	public static Map<User, Map<Integer,Monster>> userToMonsterMap = Maps.newConcurrentMap();
	/**初始化Buff的截止时间*/
	public static Map<User,Map<String,Long>> userBuffEndTime = Maps.newConcurrentMap();
	/**初始化怪物Buff的截止时间*/
	public static Map<Monster,Map<String,Long>> monsterBuffEndTime = Maps.newConcurrentMap();
	/**初始化药物属性*/
	public static Map<Integer, MpMedicine> mpMedicineMap = Maps.newHashMap();
	/**初始化武器*/
	public static Map<Integer, Equipment>equipmentMap = Maps.newHashMap();
	/**初始化全局武器特殊buf效果*/
	public static Map<Integer, Buff> buffMap = Maps.newHashMap();
	/**记录玩家队伍*/
	public static Map<String, Team> teamMap = Maps.newConcurrentMap();
	/**每个boss副本和teamId挂钩*/
	public static Map<String, BossScene> bossAreaMap = Maps.newConcurrentMap();
	/**记录每个队伍挑战副本的截止时间，超过时间就GG*/
	public static Map<String,Long> endBossAreaTime = Maps.newConcurrentMap();
	/** 副本boss定时攻击的任务*/
	public static ScheduledExecutorService bossAreaThreadPool = Executors.newScheduledThreadPool(5);
	/** 辅助定时任务关闭的线程的futuremap*/
	public static Map<String, Future> futureMap = Maps.newConcurrentMap();
	/** 缓存邮件信息 */
	public static Map<String,ConcurrentHashMap<String, Mail>> userEmailMap = Maps.newHashMap();
	/** 初始化怪物技能*/
	public static Map<Integer,MonsterSkill> monsterSkillMap = Maps.newHashMap();
	/** 职业的构建 */
	public static Map<Integer, Role> roleMap = Maps.newHashMap();
	/** 交易单的建立*/
	public static Map<String, Trade> tradeMap = Maps.newConcurrentMap();
	/** 人物经验表的建立*/
	public static Map<Integer, Level> levelMap = Maps.newHashMap();
	/** 成就表的建立 */
	public static Map<Integer, Achievement>achievementMap =  Maps.newHashMap();
	/** npc */
	public static Map<Integer,NPC> npcMap =  Maps.newHashMap();
	/** 红药恢复的时间 */
	public static Map<Integer,HpMedicine> hpMedicineMap =  Maps.newHashMap();
}
