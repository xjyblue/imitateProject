package core.packet;


import com.google.common.collect.Maps;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import java.util.Map;

/**
 * 枚举
 */
public enum ProtoBufEnum {
    /**
     * pong响应包
     */
    SERVER_PACKET_PONGRESP(1000, "ServerPacket", "PongResp"),

    /**
     * 普通响应的包
     */
    SERVER_PACKET_NORMALRESP(1001, "ServerPacket", "NormalResp"),

    /**
     * 用户buff响应的包
     */
    SERVER_PACKET_USERBUFRESP(1002, "ServerPacket", "UserbufResp"),

    /**
     * 怪物buff响应的包
     */
    SERVER_PACKET_MONSTERBUFRESP(1003, "ServerPacket", "MonsterbufResp"),

    /**
     * 攻击响应的包
     */
    SERVER_PACKET_ATTACKRESP(1004, "ServerPacket", "AttackResp"),

    /**
     * 交易响应的包
     */
    SERVER_PACKET_TRADERESP(1005, "ServerPacket", "TradeResp"),

    /**
     * 用户信息响应的包
     */
    SERVER_PACKET_USERINFORESP(1006, "ServerPacket", "UserinfoResp"),

    /**
     * 成就响应的包
     */
    SERVER_PACKET_ACHIEVEMENTRESP(1007, "ServerPacket", "AchievementResp"),


    /**
     * 用户背包响应的包
     */
    SERVER_PACKET_USERBAGRESP(1008, "ServerPacket", "UserbagResp"),

    /**
     * 好友信息响应的包
     */
    SERVER_PACKET_FRIENDRESP(1009, "ServerPacket", "FriendResp"),

    /**
     * 渠道改变响应的包
     */
    SERVER_PACKET_CHANGECHANNELRESP(1010, "ServerPacket", "ChangeChannelResp"),

    /**
     * 公户响应包
     */
    SERVER_PACKET_UNIONRESP(1110, "ServerPacket", "UnionResp"),


    /**
     * 客户端的心跳ping请求包
     */
    CLIENT_PACKET_PINGREQ(1011, "ClientPacket", "PingReq"),

    /**
     * 客户端的统一请求包，后面拆分
     */
    CLIENT_PACKET_NORMALREQ(1012, "ClientPacket", "NormalReq");


    private int iValue;

    private String innerClass;

    private String outClass;

    public String getInnerClass() {
        return innerClass;
    }

    public void setInnerClass(String innerClass) {
        this.innerClass = innerClass;
    }

    public String getOutClass() {
        return outClass;
    }

    public void setOutClass(String outClass) {
        this.outClass = outClass;
    }

    ProtoBufEnum(int iValue, String outClass, String innerClass) {
        this.iValue = iValue;
        this.outClass = outClass;
        this.innerClass = innerClass;
    }

    public int getiValue() {
        return iValue;
    }

    public void setiValue(int iValue) {
        this.iValue = iValue;
    }

    private static ProtoBufEnum[] values = ProtoBufEnum.values();


    /**
     * 这里采用的懒汉式管理，所以使用threadLocal，
     * 采用饿汉式的话,可以在类初始化的时候，直接索引好所有的映射，成为线程安全的不可变对象。
     */
    private static final ThreadLocal<ProtoParser> threadLocalParser = new ThreadLocal<ProtoParser>() {
        @Override
        protected ProtoParser initialValue() {
            return new ProtoParser();
        }
    };

    /**
     * 通过protoIndex获得它的消息解析器
     *
     * @param protoIndex
     * @return 若protoIndex无对应的parser，则返回null
     */
    public static Parser parserOfProtoIndex(final int protoIndex) {
        return threadLocalParser.get().getParser(protoIndex);
    }

    /**
     * 通过消息获取它的索引
     *
     * @param messageLite
     * @return 若不存在对应的索引，返回 -1，存在对应的索引，则返回 [0,?)
     */
    public static int protoIndexOfMessage(final MessageLite messageLite) throws UnsupportedOperationException {
        return threadLocalParser.get().getProtoIndex(messageLite);
    }

    private static class ProtoParser {

        private final Map<Integer, Parser> parserMap = Maps.newHashMap();

        private final Map<Class, ProtoBufEnum> messageLiteToEnumMap = Maps.newHashMap();

        private final Map<Integer, ProtoBufEnum> protoBufEnumMap = Maps.newHashMap();
        /**
         * protoBuf文件导出的java包路径
         */
        private final String protoBufPackagePath;

        private ProtoParser() {
//          统一packet
            protoBufPackagePath = "core.packet";
        }

        /**
         * 根据编号拿到枚举类型
         */
        private ProtoBufEnum getProtoBufEnumByIvalue(final int iValue) {
            if (protoBufEnumMap.containsKey(iValue)) {
                return protoBufEnumMap.get(iValue);
            }
            for (ProtoBufEnum protoBufEnum : values) {
                if (protoBufEnum.getiValue() == iValue) {
                    protoBufEnumMap.put(protoBufEnum.getiValue(), protoBufEnum);
                    return protoBufEnum;
                }
            }
            return null;
        }


        /**
         * 协议解析器
         *
         * @param protoIndex
         * @return
         */
        private Parser getParser(final int protoIndex) {
            try {
                if (parserMap.containsKey(protoIndex)) {
                    return parserMap.get(protoIndex);
                }
                ProtoBufEnum protoBufEnum = getProtoBufEnumByIvalue(protoIndex);
//              内外部类名拿到解析器
                String innerClassName = protoBufEnum.getInnerClass();
                String outerClassName = protoBufEnum.getOutClass();
//              拼接解析器路径
                String className = protoBufPackagePath + "." + outerClassName + "$" + innerClassName;
                Class messageClass = Class.forName(className);

                //proto2 PARSER 字段是pubic，而proto3是private,在获取parser是有一定差异
                //PROTO2
                Parser parser = (Parser) messageClass.getField("PARSER").get(null);
                //PROTO3 :Parser parser= (Parser) messageClass.getMethod("parser").invoke(null);
                parserMap.put(protoIndex, parser);
                return parser;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 通过消息 获得它的索引
         *
         * @param messageLite
         * @return 若不存在对应的索引，返回 -1，存在对应的索引，则返回 [0,?)
         */
        private int getProtoIndex(final MessageLite messageLite) throws UnsupportedOperationException {
            if (messageLiteToEnumMap.containsKey(messageLite.getClass())) {
                return messageLiteToEnumMap.get(messageLite.getClass()).getiValue();
            }
//          错误点
//          实际的类名，拿到枚举名
            String enumName = messageLite.getClass().getSimpleName();
            for (ProtoBufEnum protoBufEnum : values) {
                if (enumName.equals(protoBufEnum.getInnerClass())) {
                    messageLiteToEnumMap.put(messageLite.getClass(), protoBufEnum);
                    return protoBufEnum.getiValue();
                }
            }
            return -1;
        }

    }
}
