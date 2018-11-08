package Event;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Component.NPC;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mapper.UserMapper;
import memory.NettyMemory;
import pojo.User;
import utils.DelimiterUtils;

/**
 * 具体时间分发器
 * @author xiaojianyu
 *
 */
@Component("eventDistributor")
public class EventDistributor {
	@Autowired
	private UserMapper userMapper;
	
	public void distributeEvent(ChannelHandlerContext ctx, String msg) {
		Channel ch = ctx.channel();
		if(!NettyMemory.eventStatus.containsKey(ch)) {
			if(msg.equals("d")) {
				NettyMemory.eventStatus.put(ch, EventStatus.LOGIN);
				ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入：用户名-密码"));
			}
			if(msg.equals("z")) {
				NettyMemory.eventStatus.put(ch, EventStatus.REGISTER);
				ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入：用户名-密码-确认密码"));
			}
		}else {
			String status = NettyMemory.eventStatus.get(ch);
			String temp[] = null;
			switch(status){
			case EventStatus.LOGIN:
				temp = msg.split("-");
				if(temp.length!=2) {
					ctx.writeAndFlush(DelimiterUtils.addDelimiter("输入错误命令"));
				}else {
					User user2 = userMapper.selectByPrimaryKey(temp[0]);
					if(user2==null||(!user2.getPassword().equals(temp[1]))) {
						ctx.writeAndFlush(DelimiterUtils.addDelimiter("账户密码出错"));
					}else {
						NettyMemory.session2UserIds.put(ch, user2);
						ctx.writeAndFlush(DelimiterUtils.addDelimiter("登录成功，你已进入"+NettyMemory.areaMap.get(user2.getPos()).getName()));
						NettyMemory.eventStatus.put(ch, EventStatus.STOPAREA);
					}
				}
				break;
			case EventStatus.REGISTER:
				temp = msg.split("-");
				if(temp.length!=3) {
					ctx.writeAndFlush(DelimiterUtils.addDelimiter("输入错误命令"));
				}else {
					if(!temp[1].equals(temp[2])) {
						ctx.writeAndFlush(DelimiterUtils.addDelimiter("两次密码不一致"));
					}else {
						User user = new User();
						user.setUsername(temp[0]);
						user.setPassword(temp[1]);
						user.setStatus("1");
						user.setPos("0");
						userMapper.insert(user);
						ctx.writeAndFlush(DelimiterUtils.addDelimiter("注册成功请输入：用户名-密码进行登录"));
						NettyMemory.eventStatus.put(ch, EventStatus.LOGIN);
					}
				}
			    break;
			case EventStatus.STOPAREA:
				if(msg.startsWith("move")) {
					temp = msg.split(" ");
					User user = NettyMemory.session2UserIds.get(ch);
					if(temp[1].equals(NettyMemory.areaMap.get(user.getPos()).getName())) {
						ctx.writeAndFlush(DelimiterUtils.addDelimiter("原地无需移动"));
					}else {
						if(!NettyMemory.areaSet.contains(temp[1])) {
							ctx.writeAndFlush(DelimiterUtils.addDelimiter("移动地点不存在"));
						}else {
							if(NettyMemory.areaMap.get(user.getPos()).getAreaSet().contains(temp[1])){
								user.setPos(NettyMemory.areaToNum.get(temp[1]));
								userMapper.updateByPrimaryKeySelective(user);
								NettyMemory.session2UserIds.put(ch, user);
								ctx.writeAndFlush(DelimiterUtils.addDelimiter("已移动到"+temp[1]));
							}else {
								ctx.writeAndFlush(DelimiterUtils.addDelimiter("请充值才能启用传送门"));
							}
						}
					}
				}else if(msg.startsWith("aoi")) {
					User user = NettyMemory.session2UserIds.get(ch);
					String allStatus = "玩家"+user.getUsername()+"---"+user.getStatus()
							+"处于"+NettyMemory.areaMap.get(user.getPos()).getName()
							+System.getProperty("line.separator");
					for (Entry<Channel, User> entry : NettyMemory.session2UserIds.entrySet()) { 
						if(!user.getUsername().equals(entry.getValue().getUsername())&&user.getPos().equals(entry.getValue().getPos())) {
							allStatus += "其他玩家"+entry.getValue().getUsername()+"---"+entry.getValue().getStatus()+System.getProperty("line.separator");
						}
					}
					for(NPC npc:NettyMemory.areaMap.get(user.getPos()).getNpcs()) {
						allStatus+="NPC:"+npc.getName()+"---"+npc.getStatus()+System.getProperty("line.separator");
					}
					ctx.writeAndFlush(DelimiterUtils.addDelimiter(allStatus));
				}else {
					ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入有效指令"));
				}
			    break;
			}
		}
	}
}
