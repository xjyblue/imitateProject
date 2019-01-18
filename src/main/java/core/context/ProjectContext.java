package core.context;

import java.util.*;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import core.reflect.InvokeMethod;
import core.component.boss.BossSceneConfig;
import core.component.good.CollectGood;
import core.component.good.Equipment;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import core.component.monster.Monster;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import service.auctionservice.entity.AuctionItem;
import service.npcservice.entity.Npc;
import service.petservice.service.entity.PetConfig;
import service.petservice.service.entity.PetSkillConfig;
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
     * 交易单的建立
     */
    public final static Map<String, Trade> tradeMap = Maps.newConcurrentMap();
    /**
     * 拍卖物品
     */
    public final static Map<String, AuctionItem> auctionItemMap = Maps.newConcurrentMap();
    /**
     * 记录玩家队伍
     */
    public final static Map<String, Team> teamMap = Maps.newConcurrentMap();
}
