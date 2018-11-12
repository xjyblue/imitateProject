package memory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import Component.Area;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import pojo.User;
import pojo.Userskillrelation;
import skill.UserSkill;

/**
 * 内存中存放channel
 * @author xiaojianyu
 *
 */
public class NettyMemory {
	public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	/** 缓存通信上下文环境对应的登录用户 */ 
	public static Map<Channel,User> session2UserIds  = new ConcurrentHashMap<Channel,User>();
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
	/**初始化channel怪物攻击定时任务*/
	public static Map<Channel,Timer>monsterAttackMap = new ConcurrentHashMap<Channel,Timer>();
	/**初始化channel人物回蓝定时人物*/
	public static Map<Channel, Timer>mpReplyMap = new ConcurrentHashMap<Channel, Timer>();
	/**初始化玩家技能*/
	public static Map<Channel, Map<String,Userskillrelation>> userskillrelationMap = new ConcurrentHashMap<Channel, Map<String,Userskillrelation>>();
}
