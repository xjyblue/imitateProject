package xiaojianyu.controller;

import Component.Monster;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import mapper.UserskillrelationMapper;
import memory.NettyMemory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import Component.Area;
import Component.NPC;
import skill.MonsterSkill;
import skill.UserSkill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiaojianyu
 */
@Service("nettyServer")
public class NettyServer {
    private static Logger logger = Logger.getLogger(NettyServer.class);

    private static final int portNumber = 8080;

    @Autowired
    private NettyServerHandler nettyServerHandler;

    // 程序初始方法入口注解，提示spring这个程序先执行这里
    public void serverStart() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 初始化编码器，解码器，处理器
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048, delimiter));
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


    public void initServer() {
//        初始化技能表start
        UserSkill userSkill = new UserSkill();
        userSkill.setSkillId(1);
        userSkill.setSkillName("烈火攻击");
        userSkill.setAttackCd(10000l);
        userSkill.setDamage("10000");
        userSkill.setSkillMp("600");
        NettyMemory.SkillMap.put(userSkill.getSkillId(), userSkill);
        userSkill = new UserSkill();
        userSkill.setSkillId(2);
        userSkill.setSkillName("喷水攻击");
        userSkill.setAttackCd(2000l);
        userSkill.setDamage("120");
        userSkill.setSkillMp("50");
        NettyMemory.SkillMap.put(userSkill.getSkillId(), userSkill);
//        初始化技能表end


//		起始之地
        Area area = new Area();
        area.setName("起始之地");
        Set<String> areaSet = new HashSet<String>();
        areaSet.add("村子");
        area.setAreaSet(areaSet);
//        初始化NPC和会话
        NPC npc = new NPC("1", "起始之地-塞里亚");
        List<String> talkList = new ArrayList<String>();
        talkList.add("我是塞里亚，欢迎来到起始之地");
        npc.setTalks(talkList);
        List<NPC> npcs = new ArrayList<NPC>();
        npcs.add(npc);
        area.setNpcs(npcs);
//      初始化怪物
        MonsterSkill monsterSkill = new MonsterSkill();
        monsterSkill.setAttackCd("5");
        monsterSkill.setDamage("10");
        monsterSkill.setSkillName("闪电");
        monsterSkill.setSkillId(1);
        List<MonsterSkill> skills = new ArrayList<>();
        skills.add(monsterSkill);
        Monster monster = new Monster("起始之地哥伦布", "0", "10000000", skills, "1");
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
        npc = new NPC("1", "村子-村民");
        talkList = new ArrayList<String>();
        talkList.add("我是村民，欢迎来到村子");
        npc.setTalks(talkList);
        npcs = new ArrayList<NPC>();
        npcs.add(npc);
        area.setNpcs(npcs);

//      初始化怪物
        monsterSkill = new MonsterSkill();
        monsterSkill.setAttackCd("20");
        monsterSkill.setDamage("100");
        monsterSkill.setSkillName("锄头攻击");
        monsterSkill.setSkillId(2);
        skills = new ArrayList<>();
        skills.add(monsterSkill);
        monster = new Monster("村子村霸", "0", "15000", skills, "1");
        monsters = new ArrayList<>();
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
        npc = new NPC("1", "森林-植物精灵");
        talkList = new ArrayList<String>();
        talkList.add("我是植物精灵，欢迎来到森林");
        npc.setTalks(talkList);
        npcs = new ArrayList<NPC>();
        npcs.add(npc);
        area.setNpcs(npcs);

//      初始化怪物
        monsterSkill = new MonsterSkill();
        monsterSkill.setAttackCd("40");
        monsterSkill.setDamage("100000");
        monsterSkill.setSkillName("藤蔓攻击");
        monsterSkill.setSkillId(3);
        skills = new ArrayList<>();
        skills.add(monsterSkill);
        monster = new Monster("野兽", "0", "15000000", skills, "1");
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
        npc = new NPC("1", "城堡-骑士");
        talkList = new ArrayList<String>();
        talkList.add("我是骑士，欢迎来到城堡");
        npc.setTalks(talkList);
        npcs = new ArrayList<NPC>();
        npcs.add(npc);
        area.setNpcs(npcs);

//      初始化怪物
        monsterSkill = new MonsterSkill();
        monsterSkill.setAttackCd("40");
        monsterSkill.setDamage("100000");
        monsterSkill.setSkillName("锄头攻击");
        monsterSkill.setSkillId(4);
        skills = new ArrayList<>();
        skills.add(monsterSkill);
        monster = new Monster("帝国势力", "0", "100000000", skills, "1");
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