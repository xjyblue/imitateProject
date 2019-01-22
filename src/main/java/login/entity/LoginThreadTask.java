package login.entity;

import config.impl.excel.*;
import core.ServiceDistributor;
import core.channel.ChannelStatus;
import core.component.role.Role;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.packet.PacketType;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import org.springframework.beans.BeanUtils;
import pojo.User;
import pojo.Userskillrelation;
import pojo.UserskillrelationExample;
import service.achievementservice.util.AchievementUtil;
import service.buffservice.service.UserBuffService;
import service.petservice.service.entity.Pet;
import service.petservice.service.entity.PetSkillConfig;
import service.sceneservice.entity.Scene;
import service.skillservice.entity.UserSkill;
import service.userservice.service.UserService;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @ClassName LoginThreadTask
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/16 10:38
 * @Version 1.0
 **/
public class LoginThreadTask implements Runnable {

    /**
     * 登陆任务
     */
    private ConcurrentLinkedQueue<LoginUserTask> loginUserTaskQueue = new ConcurrentLinkedQueue<>();

    private Integer keyNum;

    private UserMapper userMapper;

    private UserskillrelationMapper userskillrelationMapper;

    private ServiceDistributor serviceDistributor;

    private UserService userService;

    private UserBuffService userBuffService;

    public LoginThreadTask(Integer keyNum, UserMapper userMapper, UserskillrelationMapper userskillrelationMapper, ServiceDistributor serviceDistributor, UserService userService, UserBuffService userBuffService) {
        this.keyNum = keyNum;
        this.userMapper = userMapper;
        this.userskillrelationMapper = userskillrelationMapper;
        this.serviceDistributor = serviceDistributor;
        this.userService = userService;
        this.userBuffService = userBuffService;
    }

    public Integer getKeyNum() {
        return keyNum;
    }

    public void setKeyNum(Integer keyNum) {
        this.keyNum = keyNum;
    }

    public ConcurrentLinkedQueue<LoginUserTask> getLoginUserTaskQueue() {
        return loginUserTaskQueue;
    }

    public void setLoginUserTaskQueue(ConcurrentLinkedQueue<LoginUserTask> loginUserTaskQueue) {
        this.loginUserTaskQueue = loginUserTaskQueue;
    }

    @Override
    public void run() {
        for (; ; ) {
            if (loginUserTaskQueue.peek() != null) {
                LoginUserTask loginUserTask = loginUserTaskQueue.poll();
                doLogin(loginUserTask);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doLogin(LoginUserTask loginUserTask) {
        Channel channel = loginUserTask.getChannel();
        User user = userMapper.getUser(loginUserTask.getUsername(), loginUserTask.getPassword());
        if (user == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORPASSWORD);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//          解决玩家顶号问题
        if (replaceUserChannel(user.getUsername(), channel)) {
            return;
        }
        Role role = RoleResourceLoad.roleMap.get(user.getRoleid());
//          初始化玩家的技能
        initUserSkill(user, channel, role);
//          初始化玩家宠物
        if (user.getRoleid().equals(GrobalConfig.FOUR)) {
            initUserPet(user);
        }
//          初始化玩家buff
        userService.initUserBuff(user);
//      这里注入事件处理器是为了让玩家自己心跳去消费命令，执行任务
        user.setServiceDistributor(serviceDistributor);
//      注入buff处理器，让用户去刷新自己的buff
        user.setUserBuffService(userBuffService);
        user.setBuffRefreshTime(0L);
        user.setIfOccupy(true);
        user.setIfOccupy(false);
//          将玩家放入场景队列中
        Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
        scene.getUserMap().put(user.getUsername(), user);

        ChannelUtil.channelToUserMap.put(channel, user);
        ChannelUtil.userToChannelMap.put(user, channel);
//          展示成就信息
        AchievementUtil.refreshAchievementInfo(user);
        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("登陆成功");
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 初始化宠物职业的宠物
     *
     * @param user
     */
    private void initUserPet(User user) {
        Pet pet = new Pet();
        BeanUtils.copyProperties(PetResourceLoad.petConfigMap.get("1"), pet);
        for (Map.Entry<String, PetSkillConfig> entry : PetSkillResourceLoad.petSkillConfigMap.entrySet()) {
            PetSkillConfig petSkillConfig = entry.getValue();
            pet.getSkillList().add(petSkillConfig);
        }
        user.setPet(pet);
    }

    /**
     * 初始化人物技能
     *
     * @param user
     * @param channel
     * @param role
     */
    private void initUserSkill(User user, Channel channel, Role role) {
        UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
        UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
        Map<String, Userskillrelation> userskillrelationMap = user.getUserskillrelationMap();
        String skillLook = "";
        for (Userskillrelation userskillrelation : userskillrelations) {
            UserSkill userSkill = UserSkillResourceLoad.skillMap.get(userskillrelation.getSkillid());
            userskillrelationMap.put(userskillrelation.getKeypos(), userskillrelation);
            skillLook += "[键位-" + userskillrelation.getKeypos() + "-技能名称-" + userSkill.getSkillName() + "-技能伤害-" + userSkill.getDamage() + "技能cd" + userSkill.getAttackCd() + "] ";
        }
        ServerPacket.UserinfoResp.Builder builder = ServerPacket.UserinfoResp.newBuilder();
        builder.setData("   " + user.getUsername() + "    职业为:" + role.getName() + "] " + skillLook);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 顶号处理
     *
     * @param username
     * @param channel
     * @return
     */
    private boolean replaceUserChannel(String username, Channel channel) {
        Channel channelTarget = null;
//          找到之前的渠道，把之前渠道的信息全部转移到新的渠道
        for (Map.Entry<Channel, User> entry : ChannelUtil.channelToUserMap.entrySet()) {
            if (entry.getValue().getUsername().equals(username)) {
                channelTarget = entry.getKey();
            }
        }

//          进行顶号处理,挂掉旧的渠道
        if (channelTarget != null) {
//              切换渠道处理渠道消息
            User user = ChannelUtil.channelToUserMap.get(channelTarget);
            user.setIfOccupy(true);
            ChannelUtil.channelToUserMap.put(channel, user);
            ChannelUtil.userToChannelMap.put(user, channel);
            ChannelUtil.channelStatus.put(channel, ChannelUtil.channelStatus.get(channelTarget));

            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("登录成功");
            MessageUtil.sendMessage(channel,builder.build());

            ServerPacket.ChangeChannelResp.Builder builder1 = ServerPacket.ChangeChannelResp.newBuilder();
            builder1.setData("不好意思,有人在别处登录你的游戏号，请选择重新登录或者修改密码");
            MessageUtil.sendMessage(channel,builder1.build());
            channelTarget.close();
            return true;
        }
        return false;
    }
}
