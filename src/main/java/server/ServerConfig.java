package server;

import service.caculationservice.service.HpCaculationService;
import core.component.boss.BossSceneConfig;
import core.component.good.CollectGood;
import core.component.good.Equipment;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import core.component.monster.Monster;
import service.npcservice.entity.NPC;
import service.sceneservice.entity.Scene;
import service.achievementservice.entity.Achievement;
import core.config.GrobalConfig;
import service.buffservice.service.BuffService;
import service.rewardservice.service.RewardService;
import io.netty.handler.timeout.IdleStateHandler;
import service.levelservice.entity.Level;
import core.context.ProjectContext;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.UserExample;
import core.component.role.Role;
import service.buffservice.entity.Buff;
import core.component.good.parent.PGood;
import service.emailservice.entity.Mail;
import core.factory.MonsterFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import org.springframework.beans.factory.annotation.Autowired;

import packet.PacketProto;
import core.component.monster.MonsterSkill;
import service.skillservice.entity.UserSkill;
import test.ExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author server
 */
@Component
@Slf4j
public class ServerConfig {
    @Autowired
    private ServerLoginHandler serverLoginHandler;
    @Autowired
    private ServerDistributeHandler serverDistributeHandler;
    @Autowired
    private MonsterFactory monsterFactory;
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private BuffService buffService;
    @Autowired
    private RewardService rewardService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ServerNetHandler serverNetHandler;

    // 程序初始方法入口注解，提示spring这个程序先执行这里
    public void serverStart() throws InterruptedException, IOException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 初始化编码器，解码器，处理器
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ProtobufDecoder(PacketProto.Packet.getDefaultInstance()));
//                          写空闲,每隔3秒触发心跳包丢失统计
                            ch.pipeline().addLast(new IdleStateHandler(3, 0, 0));
//                          处理空闲客户端网络心跳包，网络波动处理在这里
                            ch.pipeline().addLast(serverNetHandler);
//                          这里会有并发问题，记得处理
                            ch.pipeline().addLast(serverLoginHandler);
//                          这里只负责派发任务给用户，用户自己去消费packet
                            ch.pipeline().addLast(serverDistributeHandler);
                        }
                    });
            log.info("开始加载游戏资源");
            initServer();
            log.info("游戏资源加载完毕，开放8080端口");
            ChannelFuture f = b.bind(GrobalConfig.PORTNUM).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public void initServer() throws IOException {
//      初始化任务系统
        FileInputStream achievementfis = new FileInputStream(new File("src/main/resources/Achievement.xls"));
        LinkedHashMap<String, String> achievementalias = new LinkedHashMap<>();
        achievementalias.put("任务id", "achievementId");
        achievementalias.put("任务名称", "name");
        achievementalias.put("任务类别", "type");
        achievementalias.put("任务目标", "target");
        achievementalias.put("开始阶段", "begin");
        achievementalias.put("父任务", "parent");
        achievementalias.put("子任务", "sons");
        achievementalias.put("奖励", "reward");
        List<Achievement> achievementList = ExcelUtil.excel2Pojo(achievementfis, Achievement.class, achievementalias);
        for (Achievement achievement : achievementList) {
            ProjectContext.achievementMap.put(achievement.getAchievementId(), achievement);
        }

//      初始化红药系统start
        FileInputStream hpfis = new FileInputStream(new File("src/main/resources/HpMedicine.xls"));
        LinkedHashMap<String, String> hpMedicineAlias = new LinkedHashMap<>();
        hpMedicineAlias.put("红药的id", "id");
        hpMedicineAlias.put("红药是否为立即回复药品", "immediate");
        hpMedicineAlias.put("红药的cd", "cd");
        hpMedicineAlias.put("红药每秒恢复的血量", "replyValue");
        hpMedicineAlias.put("红药持续的时间", "keepTime");
        hpMedicineAlias.put("红药持续的名字", "name");
        hpMedicineAlias.put("物品的价值", "buyMoney");
        hpMedicineAlias.put("红药的种类", "type");
        List<HpMedicine> hpMedicineList = ExcelUtil.excel2Pojo(hpfis, HpMedicine.class, hpMedicineAlias);
        for (HpMedicine hpMedicine : hpMedicineList) {
            ProjectContext.hpMedicineMap.put(hpMedicine.getId(), hpMedicine);
        }
//      初始化红药

//      模拟从数据库初始化所有用户的邮件系统
        UserExample userExample = new UserExample();
        List<User> list = userMapper.selectByExample(userExample);
        for (User user : list) {
            ProjectContext.userEmailMap.put(user.getUsername(), new ConcurrentHashMap<String, Mail>());
        }

//      初始化人物经验表start
        FileInputStream levelfis = new FileInputStream(new File("src/main/resources/Level.xls"));
        LinkedHashMap<String, String> levelalias = new LinkedHashMap<>();
        levelalias.put("等级", "level");
        levelalias.put("经验上限", "experienceUp");
        levelalias.put("经验下限", "experienceDown");
        levelalias.put("血量上限", "maxHp");
        levelalias.put("蓝量上限", "maxMp");
        levelalias.put("攻击力加成", "upAttack");
        levelalias.put("计算比例", "caculatePercent");
        List<Level> levelList = ExcelUtil.excel2Pojo(levelfis, Level.class, levelalias);
        for (Level level : levelList) {
            ProjectContext.levelMap.put(level.getLevel(), level);
        }
//      初始化人物经验表end

//      初始化武器start
        FileInputStream equipFis = new FileInputStream(new File("src/main/resources/Equipment.xls"));
        LinkedHashMap<String, String> equipAlias = new LinkedHashMap<>();
        equipAlias.put("武器id", "id");
        equipAlias.put("武器名称", "name");
        equipAlias.put("武器耐久度", "durability");
        equipAlias.put("武器增加伤害", "addValue");
        equipAlias.put("购入价值", "buyMoney");
        equipAlias.put("装备星级", "startLevel");
        equipAlias.put("增加生命值", "lifeValue");
        List<Equipment> equipmentList = ExcelUtil.excel2Pojo(equipFis, Equipment.class, equipAlias);
        if (equipmentList != null && equipmentList.size() > 0) {
            for (Equipment equipment : equipmentList) {
                equipment.setType(PGood.EQUIPMENT);
                ProjectContext.equipmentMap.put(equipment.getId(), equipment);
            }
        }
//      初始化武器end

//       初始化即时回复MP start
        FileInputStream mpMedicineFis = new FileInputStream(new File("src/main/resources/Medicine.xls"));
        LinkedHashMap<String, String> mpMedicineAlias = new LinkedHashMap<>();
        mpMedicineAlias.put("蓝药id", "id");
        mpMedicineAlias.put("回复总值", "replyValue");
        mpMedicineAlias.put("是否立刻回复", "immediate");
        mpMedicineAlias.put("每秒回复的值", "secondValue");
        mpMedicineAlias.put("持续时间", "keepTime");
        mpMedicineAlias.put("蓝药名称", "name");
        mpMedicineAlias.put("购入价值", "buyMoney");
        List<MpMedicine> mpMedicineList = ExcelUtil.excel2Pojo(mpMedicineFis, MpMedicine.class, mpMedicineAlias);
        if (mpMedicineList != null && mpMedicineList.size() > 0) {
            for (MpMedicine mpMedicine : mpMedicineList) {
                mpMedicine.setType(PGood.MPMEDICINE);
                ProjectContext.mpMedicineMap.put(mpMedicine.getId(), mpMedicine);
            }
        }
//       初始化即时回复MP end

//      初始化全图buff
        FileInputStream fis = new FileInputStream(new File("src/main/resources/Buff.xls"));
        LinkedHashMap<String, String> alias = new LinkedHashMap<>();
        alias.put("buff名称", "name");
        alias.put("每秒造成伤害", "addSecondValue");
        alias.put("buff类别", "type");
        alias.put("buffId", "bufferId");
        alias.put("持续时间", "keepTime");
        alias.put("每秒减免伤害", "injurySecondValue");
        alias.put("buff类型", "typeOf");
        alias.put("每秒回复生命值", "recoverValue");
        alias.put("buff的刷新时间", "endTime");
        List<Buff> buffList = ExcelUtil.excel2Pojo(fis, Buff.class, alias);
        for (Buff buff : buffList) {
            ProjectContext.buffMap.put(buff.getBufferId(), buff);
        }
//      初始化全图buff

//      初始化角色start
        FileInputStream rolefis = new FileInputStream(new File("src/main/resources/Role.xls"));
        LinkedHashMap<String, String> rolealias = new LinkedHashMap<>();
        rolealias.put("角色id", "roleId");
        rolealias.put("角色名称", "name");
        rolealias.put("角色技能", "skills");
        rolealias.put("角色防御力", "defense");
        List<Role> roleList = ExcelUtil.excel2Pojo(rolefis, Role.class, rolealias);
        for (Role role : roleList) {
            ProjectContext.roleMap.put(role.getRoleId(), role);
        }
//      初始化角色end


//      初始化怪物技能start
//      初始化怪物所有技能以及技能所带的buff
        FileInputStream monsterBuffis = new FileInputStream(new File("src/main/resources/MonsterSkill.xls"));
        LinkedHashMap<String, String> monsterBufalias = new LinkedHashMap<>();
        monsterBufalias.put("技能id", "skillId");
        monsterBufalias.put("技能名称", "skillName");
        monsterBufalias.put("技能攻击时间", "attackCd");
        monsterBufalias.put("技能伤害", "damage");
        monsterBufalias.put("技能附带buff", "bufferMapId");
        List<MonsterSkill> skillList = ExcelUtil.excel2Pojo(monsterBuffis, MonsterSkill.class, monsterBufalias);
        for (MonsterSkill monsterSkillTemp : skillList) {
            if (!monsterSkillTemp.getBufferMapId().equals(GrobalConfig.NULL)) {
                Map<String, Integer> map = new HashMap<>();
                String temp[] = monsterSkillTemp.getBufferMapId().split("-");
                for (int i = 0; i < temp.length; i++) {
                    Buff buffTemp = ProjectContext.buffMap.get(Integer.parseInt(temp[i]));
                    map.put(buffTemp.getTypeOf(), buffTemp.getBufferId());
                }
                monsterSkillTemp.setBuffMap(map);
            }
            ProjectContext.monsterSkillMap.put(monsterSkillTemp.getSkillId(), monsterSkillTemp);
        }
//      初始化怪物技能end


//        初始化人物技能表start
        FileInputStream userSkillfis = new FileInputStream(new File("src/main/resources/UserSkill.xls"));
        LinkedHashMap<String, String> userSkillalias = new LinkedHashMap<>();
        userSkillalias.put("技能id", "skillId");
        userSkillalias.put("技能名称", "skillName");
        userSkillalias.put("技能攻击时间", "attackCd");
        userSkillalias.put("技能伤害", "damage");
        userSkillalias.put("技能消耗Mp", "skillMp");
        userSkillalias.put("技能附带buffId", "bufferMapId");
        userSkillalias.put("技能所属种族", "roleSkill");
        List<UserSkill> userSkillList = ExcelUtil.excel2Pojo(userSkillfis, UserSkill.class, userSkillalias);
        for (UserSkill userSkill : userSkillList) {
            Map<String, Integer> map = new HashMap<>();
            userSkill.setBuffMap(map);
            ProjectContext.skillMap.put(userSkill.getSkillId(), userSkill);
            if (!userSkill.getBufferMapId().equals(GrobalConfig.NULL)) {
                String temp[] = userSkill.getBufferMapId().split("-");
                for (int i = 0; i < temp.length; i++) {
                    Buff buffTemp = ProjectContext.buffMap.get(Integer.parseInt(temp[i]));
                    userSkill.getBuffMap().put(buffTemp.getTypeOf(), buffTemp.getBufferId());
                }
            }
        }
//        初始化技能表end

//      初始化npc start
        FileInputStream npcfis = new FileInputStream(new File("src/main/resources/NPC.xls"));
        LinkedHashMap<String, String> npcalias = new LinkedHashMap<>();
        npcalias.put("NPC的id", "id");
        npcalias.put("NPC的状态", "status");
        npcalias.put("NPC的名字", "name");
        npcalias.put("NPC的话", "talk");
        npcalias.put("NPC所在的地点", "areaId");
        npcalias.put("换取的条件", "getTarget");
        npcalias.put("换取的物品", "getGoods");
        List<NPC> npcList = ExcelUtil.excel2Pojo(npcfis, NPC.class, npcalias);
        for (NPC npc : npcList) {
            ProjectContext.npcMap.put(npc.getId(), npc);
        }
//      初始化npc end

//      初始化收集类物品start
        FileInputStream collectGoodfis = new FileInputStream(new File("src/main/resources/CollectGood.xls"));
        LinkedHashMap<String, String> collectGoodAlias = new LinkedHashMap<>();
        collectGoodAlias.put("物品的名字", "name");
        collectGoodAlias.put("物品的id", "id");
        collectGoodAlias.put("物品的描述", "desc");
        collectGoodAlias.put("物品的价格", "buyMoney");
        collectGoodAlias.put("物品的种类", "type");
        List<CollectGood> collectGoodList = ExcelUtil.excel2Pojo(collectGoodfis, CollectGood.class, collectGoodAlias);
        for (CollectGood collectGood : collectGoodList) {
            ProjectContext.collectGoodMap.put(collectGood.getId(), collectGood);
        }
//      初始化收集类物品end

//      初始化怪物start
        FileInputStream monsterfis = new FileInputStream(new File("src/main/resources/monster.xls"));
        LinkedHashMap<String, String> monsteralias = new LinkedHashMap<>();
        monsteralias.put("怪物id", "id");
        monsteralias.put("怪物名称", "name");
        monsteralias.put("怪物类别", "type");
        monsteralias.put("怪物生命值", "valueOfLife");
        monsteralias.put("怪物状态", "status");
        monsteralias.put("怪物技能", "skillIds");
        monsteralias.put("出生地点", "pos");
        monsteralias.put("怪物经验值", "experience");
        monsteralias.put("击杀获得的奖励", "reward");
        List<Monster> monsterList = ExcelUtil.excel2Pojo(monsterfis, Monster.class, monsteralias);
        for (Monster monster : monsterList) {
            ProjectContext.monsterMap.put(monster.getId(), monster);
        }
//      初始化怪物end

//      初始化副本配置类start
        FileInputStream bossSceneConfigfis = new FileInputStream(new File("src/main/resources/BossSceneConfig.xls"));
        LinkedHashMap<String, String> bossSceneConfigfisAlias = new LinkedHashMap<>();
        bossSceneConfigfisAlias.put("副本的id", "bossSceneId");
        bossSceneConfigfisAlias.put("副本场景的顺序", "sequences");
        bossSceneConfigfisAlias.put("副本的持续时间", "keeptime");
        bossSceneConfigfisAlias.put("副本的名字", "bossSceneName");
        bossSceneConfigfisAlias.put("最后一击奖励场景", "finalReward");
        bossSceneConfigfisAlias.put("需要组队的场景", "needMoreMen");
        List<BossSceneConfig> bossSceneConfigList = ExcelUtil.excel2Pojo(bossSceneConfigfis, BossSceneConfig.class, bossSceneConfigfisAlias);
        for (BossSceneConfig bossSceneConfig : bossSceneConfigList) {
            ProjectContext.bossSceneConfigMap.put(bossSceneConfig.getBossSceneId(), bossSceneConfig);
        }
//      初始化副本配置类end

//      初始化场景start
        FileInputStream scenefis = new FileInputStream(new File("src/main/resources/Scene.xls"));
        LinkedHashMap<String, String> sceneAlias = new LinkedHashMap<>();
        sceneAlias.put("场景的名字", "name");
        sceneAlias.put("场景的id", "id");
        sceneAlias.put("关联的场景id", "sceneIds");
        sceneAlias.put("npcId", "npcS");
        sceneAlias.put("怪物id", "monsterS");
        sceneAlias.put("需要的等级", "needLevel");
        List<Scene> sceneList = ExcelUtil.excel2Pojo(scenefis, Scene.class, sceneAlias);
        for (Scene scene : sceneList) {
//          初始化npc
            List<NPC> npcs = new ArrayList<>();
//          这里后面可以改成多个npc
            npcs.add(ProjectContext.npcMap.get(Integer.parseInt(scene.getNpcS())));
            scene.setNpcs(npcs);

//          初始化怪物
            List<Monster> monsters = new ArrayList<>();
            monsters.add(monsterFactory.getMonster(Integer.parseInt(scene.getMonsterS())));
            scene.setMonsters(monsters);

//          初始化关联地图
            Set<String> areaSet = new HashSet<String>();
            String sceneConnect = scene.getSceneIds();
            for (String sceneT : sceneConnect.split("-")) {
                areaSet.add(sceneT);
            }
            scene.setSceneSet(areaSet);
            ProjectContext.sceneMap.put(scene.getId(), scene);
            ProjectContext.sceneSet.add(scene.getName());
            ProjectContext.sceneThreadPool.execute(scene);
        }
//      初始化场景end
    }
}