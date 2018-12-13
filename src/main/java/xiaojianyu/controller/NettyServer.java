package xiaojianyu.controller;

import achievement.Achievement;
import caculation.RecoverHpCaculation;
import level.Level;
import role.Role;
import buff.Buff;
import component.*;
import component.parent.Good;
import email.Mail;
import factory.MonsterFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import memory.NettyMemory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import packet.PacketProto;
import skill.MonsterSkill;
import skill.UserSkill;
import task.BufferTask;
import test.ExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojianyu
 */
@Service("nettyServer")
public class NettyServer {
    private static Logger logger = Logger.getLogger(NettyServer.class);

    private static final int portNumber = 8081;

    @Autowired
    private NettyServerHandler nettyServerHandler;
    @Autowired
    private MonsterFactory monsterFactory;
    @Autowired
    private RecoverHpCaculation recoverHpCaculation;
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
//                  .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 初始化编码器，解码器，处理器
//                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ProtobufDecoder(PacketProto.Packet.getDefaultInstance()));
//                            写空闲,每隔5秒触发心跳包丢失统计
                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                            ch.pipeline().addLast(nettyServerHandler);
                        }
                    });
            initServer();
            logger.info("初始化netty服务器启动参数，开放8080端口");
            ChannelFuture f = b.bind(portNumber).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public void initServer() throws IOException {
//      初始化任务系统
        FileInputStream achievementfis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Achievement.xls"));
        LinkedHashMap<String, String> achievementalias = new LinkedHashMap<>();
        achievementalias.put("任务id","achievementId");
        achievementalias.put("任务名称","name");
        achievementalias.put("任务类别","type");
        achievementalias.put("任务目标","target");
        achievementalias.put("开始阶段","begin");
        List<Achievement> achievementList = ExcelUtil.excel2Pojo(achievementfis, Achievement.class, achievementalias);
        for(Achievement achievement:achievementList) {
            NettyMemory.achievementMap.put(achievement.getAchievementId(), achievement);
        }


//      模拟从数据库初始化所有用户的邮件系统
        NettyMemory.userEmailMap.put("z", new ConcurrentHashMap<String, Mail>());
        NettyMemory.userEmailMap.put("k", new ConcurrentHashMap<String, Mail>());
        NettyMemory.userEmailMap.put("w", new ConcurrentHashMap<String, Mail>());

//        初始化定时任务 start
        NettyMemory.scheduledThreadPool.scheduleAtFixedRate(new BufferTask(recoverHpCaculation), 0, 1, TimeUnit.SECONDS);
//        初始化定时任务 end

//      初始化人物经验表start
        FileInputStream levelfis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Level.xls"));
        LinkedHashMap<String, String> levelalias = new LinkedHashMap<>();
        levelalias.put("等级","level");
        levelalias.put("经验上限","experienceUp");
        levelalias.put("经验下限","experienceDown");
        levelalias.put("血量上限","maxHp");
        levelalias.put("蓝量上限","maxMp");
        List<Level> levelList = ExcelUtil.excel2Pojo(levelfis, Level.class, levelalias);
        for(Level level:levelList){
            NettyMemory.levelMap.put(level.getLevel(),level);
        }
//      初始化人物经验表end

//      初始化武器start
        FileInputStream equipFis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Equipment.xls"));
        LinkedHashMap<String, String> equipAlias = new LinkedHashMap<>();
        equipAlias.put("武器id", "id");
        equipAlias.put("武器名称", "name");
        equipAlias.put("武器耐久度", "durability");
        equipAlias.put("武器增加伤害", "addValue");
        equipAlias.put("购入价值", "buyMoney");
        List<Equipment> equipmentList = ExcelUtil.excel2Pojo(equipFis, Equipment.class, equipAlias);
        if (equipmentList != null && equipmentList.size() > 0) {
            for (Equipment equipment : equipmentList) {
                equipment.setType(Good.EQUIPMENT);
                NettyMemory.equipmentMap.put(equipment.getId(), equipment);
            }
        }
//      初始化武器end

//       初始化即时回复MP start
        FileInputStream mpMedicineFis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Medicine.xls"));
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
                mpMedicine.setType(Good.MPMEDICINE);
                NettyMemory.mpMedicineMap.put(mpMedicine.getId(), mpMedicine);
            }
        }
//       初始化即时回复MP end

//      初始化全图buff
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Buff.xls"));
        LinkedHashMap<String, String> alias = new LinkedHashMap<>();
        alias.put("buff名称", "name");
        alias.put("每秒造成伤害", "addSecondValue");
        alias.put("buff类别", "type");
        alias.put("buffId", "bufferId");
        alias.put("持续时间", "keepTime");
        alias.put("每秒减免伤害", "injurySecondValue");
        alias.put("buff类型", "typeOf");
        alias.put("每秒回复生命值","recoverValue");
        List<Buff> buffList = ExcelUtil.excel2Pojo(fis, Buff.class, alias);
        for (Buff buff : buffList) {
            NettyMemory.buffMap.put(buff.getBufferId(), buff);
        }
//      初始化全图buff

//      初始化角色start
        FileInputStream rolefis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Role.xls"));
        LinkedHashMap<String, String> rolealias = new LinkedHashMap<>();
        rolealias.put("角色id", "roleId");
        rolealias.put("角色名称", "name");
        rolealias.put("角色技能", "skills");
        rolealias.put("角色防御力", "defense");
        List<Role> roleList = ExcelUtil.excel2Pojo(rolefis, Role.class, rolealias);
        for (Role role : roleList) {
            NettyMemory.roleMap.put(role.getRoleId(), role);
        }
//      初始化角色end


//      初始化怪物技能start
//      初始化怪物所有技能以及技能所带的buff
        FileInputStream monsterBuffis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\MonsterSkill.xls"));
        LinkedHashMap<String, String> monsterBufalias = new LinkedHashMap<>();
        monsterBufalias.put("技能id", "skillId");
        monsterBufalias.put("技能名称", "skillName");
        monsterBufalias.put("技能攻击时间", "attackCd");
        monsterBufalias.put("技能伤害", "damage");
        monsterBufalias.put("技能附带buff", "bufferMapId");
        List<MonsterSkill> skillList = ExcelUtil.excel2Pojo(monsterBuffis, MonsterSkill.class, monsterBufalias);
        for (MonsterSkill monsterSkillTemp : skillList) {
            if (!monsterSkillTemp.getBufferMapId().equals("0")) {
                Map<String, Integer> map = new HashMap<>();
                String temp[] = monsterSkillTemp.getBufferMapId().split("-");
                for (int i = 0; i < temp.length; i++) {
                    Buff buffTemp = NettyMemory.buffMap.get(Integer.parseInt(temp[i]));
                    map.put(buffTemp.getTypeOf(), buffTemp.getBufferId());
                }
                monsterSkillTemp.setBuffMap(map);
            }
            NettyMemory.monsterSkillMap.put(monsterSkillTemp.getSkillId(), monsterSkillTemp);
        }
//      初始化怪物技能end


//        初始化技能表start
        FileInputStream userSkillfis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\UserSkill.xls"));
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
            NettyMemory.SkillMap.put(userSkill.getSkillId(), userSkill);
            if (!userSkill.getBufferMapId().equals("0")) {
                String temp[] = userSkill.getBufferMapId().split("-");
                for (int i = 0; i < temp.length; i++) {
                    Buff buffTemp = NettyMemory.buffMap.get(Integer.parseInt(temp[i]));
                    userSkill.getBuffMap().put(buffTemp.getTypeOf(), buffTemp.getBufferId());
                }
            }
        }
//        初始化技能表end


//      初始化npc start
        FileInputStream npcfis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\NPC.xls"));
        LinkedHashMap<String, String> npcalias = new LinkedHashMap<>();
        npcalias.put("NPC的id","id");
        npcalias.put("NPC的状态","status");
        npcalias.put("NPC的名字","name");
        npcalias.put("NPC的话","talk");
        npcalias.put("NPC所在的地点","areaId");
        List<NPC> npcList = ExcelUtil.excel2Pojo(npcfis,NPC.class, npcalias);
        for(NPC npc:npcList){
            NettyMemory.npcMap.put(npc.getId(),npc);
        }
//      初始化npc end

//		起始之地
        Area area = new Area();
        area.setName("起始之地");
        area.setId(0);
        Set<String> areaSet = new HashSet<String>();
        areaSet.add("村子");
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        NPC npc = NettyMemory.npcMap.get(1);
        List<NPC> npcs = new ArrayList<NPC>();
        npcs.add(npc);
        area.setNpcs(npcs);
        Monster monster = monsterFactory.getMonster(3);
        List<Monster> monsters = new ArrayList<>();
        monsters.add(monster);
        area.setMonsters(monsters);


        NettyMemory.areaMap.put("0", area);
        NettyMemory.areaToNum.put("起始之地", "0");

//		村子
        area = new Area();
        area.setName("村子");
        area.setId(1);
        areaSet = new HashSet<String>();
        areaSet.add("起始之地");
        areaSet.add("城堡");
        areaSet.add("森林");
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        npc = NettyMemory.npcMap.get(2);
        npcs = new ArrayList<NPC>();
        npcs.add(npc);
        area.setNpcs(npcs);
        monsters = new ArrayList<>();
        monster = monsterFactory.getMonster(4);
        monsters.add(monster);
        area.setMonsters(monsters);


        NettyMemory.areaMap.put("1", area);
        NettyMemory.areaToNum.put("村子", "1");

//      森林
        area = new Area();
        area.setName("森林");
        area.setId(2);
        areaSet = new HashSet<String>();
        areaSet.add("村子");
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        npc = NettyMemory.npcMap.get(3);
        npcs = new ArrayList<NPC>();
        npcs.add(npc);
        area.setNpcs(npcs);

//      初始化怪物
        monster = monsterFactory.getMonster(5);
        monsters = new ArrayList<>();
        monsters.add(monster);
        area.setMonsters(monsters);

        NettyMemory.areaMap.put("2", area);
        NettyMemory.areaToNum.put("森林", "2");

//      城堡
        area = new Area();
        area.setName("城堡");
        areaSet = new HashSet<String>();
        areaSet.add("村子");
        area.setId(3);
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        npc = NettyMemory.npcMap.get(4);
        npcs = new ArrayList<NPC>();
        npcs.add(npc);
        area.setNpcs(npcs);

//      初始化怪物
        monster = monsterFactory.getMonster(6);
        monsters = new ArrayList<>();
        monsters.add(monster);
        area.setMonsters(monsters);
        NettyMemory.areaToNum.put("城堡", "3");
        NettyMemory.areaMap.put("3", area);
        NettyMemory.areaSet.add("村子");
        NettyMemory.areaSet.add("森林");
        NettyMemory.areaSet.add("起始之地");
        NettyMemory.areaSet.add("城堡");
    }
}