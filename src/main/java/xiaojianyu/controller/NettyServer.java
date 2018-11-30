package xiaojianyu.controller;

import buff.Buff;
import component.*;
import component.parent.Good;
import config.BuffConfig;
import email.Mail;
import event.TeamEvent;
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

//        模拟从数据库初始化所有用户的邮件系统
        NettyMemory.userEmailMap.put("z", new ConcurrentHashMap<String, Mail>());
        NettyMemory.userEmailMap.put("k", new ConcurrentHashMap<String, Mail>());
        NettyMemory.userEmailMap.put("w", new ConcurrentHashMap<String, Mail>());

//        初始化定时任务 start
//         回蓝定时任务
        NettyMemory.scheduledThreadPool.scheduleAtFixedRate(new BufferTask(), 0, 1, TimeUnit.SECONDS);
//        初始化定时任务 end


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
        List<Buff> buffList = ExcelUtil.excel2Pojo(fis, Buff.class, alias);
        for (Buff buff : buffList) {
            NettyMemory.buffMap.put(buff.getBufferId(), buff);
        }
//      初始化全图buff


//      初始化怪物技能start
//      初始化怪物所有技能以及技能所带的buff
        FileInputStream monsterBuffis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\MonsterSkill.xls"));
        LinkedHashMap<String, String> monsterBufalias = new LinkedHashMap<>();
        monsterBufalias.put("技能id", "skillId");
        monsterBufalias.put("技能名称", "skillName");
        monsterBufalias.put("技能攻击时间", "attackCd");
        monsterBufalias.put("技能伤害", "damage");
        monsterBufalias.put("技能附带buff","bufferMapId");
        List<MonsterSkill> skillList = ExcelUtil.excel2Pojo(monsterBuffis, MonsterSkill.class, monsterBufalias);
        for(MonsterSkill monsterSkillTemp:skillList){
            if(!monsterSkillTemp.getBufferMapId().equals("0")){
                Map<String,Integer> map = new HashMap<>();
                String temp[] = monsterSkillTemp.getBufferMapId().split("-");
                for(int i=0;i<temp.length;i++){
                    Buff buffTemp = NettyMemory.buffMap.get(Integer.parseInt(temp[i]));
                    map.put(buffTemp.getTypeOf(),buffTemp.getBufferId());
                }
                monsterSkillTemp.setBuffMap(map);
            }
            NettyMemory.monsterSkillMap.put(monsterSkillTemp.getSkillId(),monsterSkillTemp);
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


//		起始之地
        Area area = new Area();
        area.setName("起始之地");
        Set<String> areaSet = new HashSet<String>();
        areaSet.add("村子");
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        NPC npc = new NPC("1", "赛利亚");
        List<String> talkList = new ArrayList<String>();
        talkList.add("我是塞里亚，欢迎来到起始之地");
        npc.setTalks(talkList);
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
        areaSet = new HashSet<String>();
        areaSet.add("起始之地");
        areaSet.add("城堡");
        areaSet.add("森林");
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        npc = new NPC("1", "村民");
        talkList = new ArrayList<String>();
        talkList.add("我是村民，欢迎来到村子");
        npc.setTalks(talkList);
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
        areaSet = new HashSet<String>();
        areaSet.add("村子");
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        npc = new NPC("1", "植物精灵");
        talkList = new ArrayList<String>();
        talkList.add("我是植物精灵，欢迎来到森林");
        npc.setTalks(talkList);
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
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        npc = new NPC("1", "骑士");
        talkList = new ArrayList<String>();
        talkList.add("我是骑士，欢迎来到城堡");
        npc.setTalks(talkList);
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