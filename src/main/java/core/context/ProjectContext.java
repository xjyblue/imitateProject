package core.context;

import java.util.*;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import core.component.boss.BossSceneConfig;
import core.component.good.CollectGood;
import core.component.good.Equipment;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import core.component.monster.Monster;
import service.auctionservice.entity.AuctionItem;
import service.npcservice.entity.Npc;
import service.sceneservice.entity.BossScene;
import service.sceneservice.entity.Scene;
import service.achievementservice.entity.Achievement;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import service.levelservice.entity.Level;
import core.component.role.Role;
import service.buffservice.entity.Buff;
import service.emailservice.entity.Mail;
import io.netty.channel.Channel;
import pojo.User;
import pojo.Userskillrelation;
import core.component.monster.MonsterSkill;
import service.skillservice.entity.UserSkill;
import service.teamservice.entity.Team;
import service.transactionservice.entity.Trade;

/**
 * @ClassName ProjectContext
 * @Description 项目上下文
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class ProjectContext {
    /**
     * 初始化普通线程池工厂
     */
    public final static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-scene-runner-%d").build();
    /**
     * 缓存通信上下文环境对应的登录用户
     */
    public final static Map<Channel, User> channelToUserMap = Maps.newConcurrentMap();
    /**
     * 根据用户拿去对应的渠道
     */
    public final static Map<User, Channel> userToChannelMap = Maps.newConcurrentMap();
    /**
     * 收集类物品的id
     */
    public final static Map<Integer, CollectGood> collectGoodMap = Maps.newConcurrentMap();
    /**
     * 信道所处事件的装填
     */
    public final static Map<Channel, String> channelStatus = Maps.newConcurrentMap();
    /**
     * 地图缓存到内存中
     */
    public final static Map<String, Scene> sceneMap = Maps.newHashMap();
    /**
     * 初始化全局技能伤害
     */
    public final static Map<Integer, UserSkill> skillMap = Maps.newHashMap();
    /**
     * 初始化地图Set集合
     */
    public final static Set<String> sceneSet = Sets.newHashSet();
    /**
     * 初始化 地图线程池
     **/
    public final static ExecutorService sceneThreadPool = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
    /**
     * 初始化玩家技能
     */
    public final static Map<User, Map<String, Userskillrelation>> userskillrelationMap = Maps.newConcurrentMap();
    /**
     * 缓存用户所攻击的怪兽
     */
    public final static Map<User, Map<Integer, Monster>> userToMonsterMap = Maps.newConcurrentMap();
    /**
     * 初始化Buff的截止时间
     */
    public final static Map<User, Map<String, Long>> userBuffEndTime = Maps.newConcurrentMap();
    /**
     * 初始化怪物Buff的截止时间
     */
    public final static Map<Monster, Map<String, Long>> monsterBuffEndTime = Maps.newConcurrentMap();
    /**
     * 初始化药物属性
     */
    public final static Map<Integer, MpMedicine> mpMedicineMap = Maps.newHashMap();
    /**
     * 初始化武器
     */
    public final static Map<Integer, Equipment> equipmentMap = Maps.newHashMap();
    /**
     * 初始化全局武器特殊buf效果
     */
    public final static Map<Integer, Buff> buffMap = Maps.newHashMap();
    /**
     * 记录玩家队伍
     */
    public final static Map<String, Team> teamMap = Maps.newConcurrentMap();
    /**
     * 每个boss副本和teamId挂钩
     */
    public final static Map<String, BossScene> bossAreaMap = Maps.newConcurrentMap();
    /**
     * 记录每个队伍挑战副本的截止时间，超过时间就GG
     */
    public final static Map<String, Long> endBossAreaTime = Maps.newConcurrentMap();
    /**
     * 副本boss攻击线程池
     */
    public final static ScheduledExecutorService bossAreaThreadPool = new ScheduledThreadPoolExecutor(5, namedThreadFactory);
    /**
     * 缓存副本的配置，为生成副本而用
     */
    public final static Map<String, BossSceneConfig> bossSceneConfigMap = Maps.newHashMap();
    /**
     * 辅助定时任务关闭的线程的futuremap
     */
    public final static Map<String, Future> futureMap = Maps.newConcurrentMap();
    /**
     * 缓存邮件信息
     */
    public final static Map<String, ConcurrentHashMap<String, Mail>> userEmailMap = Maps.newHashMap();
    /**
     * 初始化怪物技能
     */
    public final static Map<Integer, MonsterSkill> monsterSkillMap = Maps.newHashMap();
    /**
     * 职业的构建
     */
    public final static Map<Integer, Role> roleMap = Maps.newHashMap();
    /**
     * 怪物的构建
     */
    public final static Map<Integer, Monster> monsterMap = Maps.newHashMap();
    /**
     * 交易单的建立
     */
    public final static Map<String, Trade> tradeMap = Maps.newConcurrentMap();
    /**
     * 人物经验表的建立
     */
    public final static Map<Integer, Level> levelMap = Maps.newHashMap();
    /**
     * 成就表的建立
     */
    public final static Map<Integer, Achievement> achievementMap = Maps.newHashMap();
    /**
     * npc
     */
    public final static Map<Integer, Npc> npcMap = Maps.newHashMap();
    /**
     * 红药恢复的时间
     */
    public final static Map<Integer, HpMedicine> hpMedicineMap = Maps.newHashMap();
    /**
     * 拍卖物品
     */
    public final static Map<String, AuctionItem> auctionItemMap = Maps.newConcurrentMap();
    /**
     * 公共周期线程池，清除过期任务，只用于公共场合
     */
    public final static ExecutorService periodThreadPool = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
}
