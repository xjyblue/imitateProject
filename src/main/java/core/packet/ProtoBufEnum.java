package core.packet;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 枚举
 */
public enum ProtoBufEnum {

    /**
     * pong包
     */
    SERVER_PACKET_PONGRESP(0),

    /**
     * 普通的包
     */
    SERVER_PACKET_NORMALRESP(1),

    /**
     * 普通的包
     */
    SERVER_PACKET_USERBUFRESP(2),

    /**
     * 怪物buff的包
     */
    SERVER_PACKET_MONSTERBUFRESP(3),

    /**
     * 攻击的包
     */
    SERVER_PACKET_ATTACKRESP(4),

    /**
     * 交易的包
     */
    SERVER_PACKET_TRADERESP(5),

    /**
     * 用户信息的包
     */
    SERVER_PACKET_USERINFORESP(6),

    /**
     * 成就的包
     */
    SERVER_PACKET_ACHIEVEMENTRESP(7),


    /**
     * 用户背包的包
     */
    SERVER_PACKET_USERBAGRESP(8),

    /**
     * 好友的包
     */
    SERVER_PACKET_FRIENDRESP(9),

    /**
     * 渠道改变的包
     */
    SERVER_PACKET_CHANGECHANNELRESP(10),


    /**
     * 客户端的心跳ping包
     */
    CLIENT_PACKET_PINGREQ(11),

    /**
     * 客户端的统一请求包，后面拆分
     */
    CLIENT_PACKET_NORMALREQ(12);

    private int iValue;

    ProtoBufEnum(int iValue) {
        this.iValue = iValue;
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

        private final Parser[] parsersArray = new Parser[values.length];

        private final Map<Class, ProtoBufEnum> messageLiteToEnumMap = new HashMap<>();
        /**
         * protoBuf文件导出的java包路径
         */
        private final String protoBufPackagePath;

        private ProtoParser() {
//          统一packet
            protoBufPackagePath = "core.packet";
        }

        /**
         * 协议解析器
         *
         * @param protoIndex
         * @return
         */
        private Parser getParser(final int protoIndex) {
            try {
                if (parsersArray[protoIndex] != null) {
                    return parsersArray[protoIndex];
                }
                ProtoBufEnum protoBufEnum = values[protoIndex];

                String innerClassName = getInnerClassName(protoBufEnum);
                String outerClassName = getOuterClassName(innerClassName);

                String className = protoBufPackagePath + "." + outerClassName + "$" + innerClassName;
                Class messageClass = Class.forName(className);

//              proto2 PARSER 字段是pubic，而proto3是private,在获取parser是有一定差异
                Parser parser = (Parser) messageClass.getField("PARSER").get(null);

//PROTO3        Parser parser= (Parser) messageClass.getMethod("parser").invoke(null);
                parsersArray[protoIndex] = parser;
                return parser;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 通过枚举获取内部类名称
         *
         * @param protoBufEnum
         * @return
         */
        private String getInnerClassName(ProtoBufEnum protoBufEnum) {
            return protoBufEnum.name().toLowerCase();
        }

        /**
         * 通过内部类名称获得外部类名称
         *
         * @param innerClassName
         * @return
         */
        private String getOuterClassName(String innerClassName) {
            String[] temp = innerClassName.split("_");
            return temp[0] + "_" + temp[1];
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
            String enumName = messageLite.getClass().getSimpleName().toUpperCase();
            for (ProtoBufEnum protoBufEnum : values) {
                if (enumName.equals(protoBufEnum.name())) {
                    messageLiteToEnumMap.put(messageLite.getClass(), protoBufEnum);
                    return protoBufEnum.getiValue();
                }
            }
            return -1;
        }
    }

    public static <T extends Enum<T>> T indexOf(Class<T> clazz, int index) {
        return (T) clazz.getEnumConstants()[index];
    }


    public static Object buildPacket(String data, Integer type) {
        ProtoBufEnum protoBufEnum = indexOf(ProtoBufEnum.class, type);
        Object o = null;
        switch (protoBufEnum) {
            case SERVER_PACKET_PONGRESP:
                server_packet.server_packet_pongresp.Builder server_packet_pongresp = server_packet.server_packet_pongresp.newBuilder();
                server_packet_pongresp.setData(data);
                o = server_packet_pongresp.build();
                break;
            case SERVER_PACKET_NORMALRESP:
                server_packet.server_packet_normalresp.Builder server_packet_normalresp = server_packet.server_packet_normalresp.newBuilder();
                server_packet_normalresp.setData(data);
                o = server_packet_normalresp.build();
                break;
            case SERVER_PACKET_MONSTERBUFRESP:
                server_packet.server_packet_monsterbufresp.Builder server_packet_monsterbufresp = server_packet.server_packet_monsterbufresp.newBuilder();
                server_packet_monsterbufresp.setData(data);
                o = server_packet_monsterbufresp;
                break;
            case SERVER_PACKET_ATTACKRESP:
                server_packet.server_packet_attackresp.Builder server_packet_attackresp = server_packet.server_packet_attackresp.newBuilder();
                server_packet_attackresp.setData(data);
                o = server_packet_attackresp;
                break;
            case SERVER_PACKET_TRADERESP:

                break;
            case SERVER_PACKET_USERINFORESP:
                break;
            case SERVER_PACKET_ACHIEVEMENTRESP:
                break;
            case SERVER_PACKET_USERBAGRESP:
                break;
            case SERVER_PACKET_FRIENDRESP:
                break;
            case SERVER_PACKET_CHANGECHANNELRESP:
                break;
            case CLIENT_PACKET_PINGREQ:
                break;
            case CLIENT_PACKET_NORMALREQ:
                break;
            default:
        }
        return o;
    }


}
