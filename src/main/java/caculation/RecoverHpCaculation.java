package caculation;

import component.Monster;
import utils.LevelUtil;
import org.springframework.stereotype.Component;
import pojo.User;

import java.math.BigInteger;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/5 16:14
 */

@Component("recoverHpCaculation")
public class RecoverHpCaculation {

//  计算血量全部在这里，顺便可以加减血

    public void addUserHp(User user, String recoverValue) {
        String maxHp = LevelUtil.getMaxHp(user);
        BigInteger userHp = new BigInteger(user.getHp());
        BigInteger maxHpB = new BigInteger(maxHp);
        BigInteger recoverHp = new BigInteger(recoverValue);
        userHp = userHp.add(recoverHp);
        if(userHp.compareTo(maxHpB)>0){
            user.setHp(maxHp);
        }else {
            user.setHp(userHp.toString());
        }
    }

    public void subMonsterHp(Monster monster, String subValue) {
        BigInteger subHp = new BigInteger(subValue);
        BigInteger monsterHp = new BigInteger(monster.getValueOfLife());
        monsterHp = monsterHp.subtract(subHp);
        if(monsterHp.compareTo(new BigInteger("0"))<0){
            monster.setValueOfLife("0");
        }else {
            monster.setValueOfLife(monsterHp.toString());
        }
    }
}
