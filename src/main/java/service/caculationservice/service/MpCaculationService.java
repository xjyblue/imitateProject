package service.caculationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/29 17:32
 */
@Component
public class MpCaculationService {

    private void subUserMp(User user, String subMp){
        Integer userMp = Integer.parseInt(user.getMp());
        Integer subMpI =Integer.parseInt(subMp);
        userMp -= subMpI;
        if(userMp<0){
            userMp = 0;
        }
        user.setMp(userMp.toString());
    }

}
