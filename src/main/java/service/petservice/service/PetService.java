package service.petservice.service;

import core.component.monster.Monster;
import core.config.GrobalConfig;
import core.context.ProjectContext;
import core.packet.PacketType;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.caculationservice.service.HpCaculationService;
import service.petservice.service.entity.PetConfig;
import service.petservice.service.entity.PetSkillConfig;
import utils.MessageUtil;

import java.util.Random;

/**
 * @ClassName PetService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/9 10:35
 * @Version 1.0
 **/
@Component
public class PetService {
    @Autowired
    private HpCaculationService hpCaculationService;

    /**
     * 随机宠物的技能
     *
     * @param user
     * @return
     */
    public PetSkillConfig getPetSkill(User user) {
//      在此处可以随机选择技能
        Random random = new Random();
        int index = random.nextInt(user.getPet().getSkillList().size());
        return user.getPet().getSkillList().get(index);
    }

    /**
     * 宠物攻击怪物
     *
     * @param monster
     * @param channel
     */
    public void attackMonster(Monster monster, Channel channel) {
        User user = ProjectContext.channelToUserMap.get(channel);
        PetConfig petConfig = ProjectContext.petConfigMap.get(GrobalConfig.DEFAULT_PET);
        PetSkillConfig petSkillConfig = getPetSkill(user);
//      这里可以附加宠物技能伤害，比如根据宠物等级等
        hpCaculationService.subMonsterHp(monster, petSkillConfig.getDamage().toString());
        channel.writeAndFlush(MessageUtil.turnToPacket("你的宠物:" + petConfig.getName() + "对怪物使用了" + petSkillConfig.getSkillName()
                + "技能，造成伤害:" + petSkillConfig.getDamage(), PacketType.ATTACKMSG));
    }
}
