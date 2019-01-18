package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import service.buffservice.entity.Buff;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BuffResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:15
 * @Version 1.0
 **/
@Component
@Slf4j
public class BuffResourceLoad implements IResourceLoad {

    /**
     * 初始化全局武器特殊buf效果
     */
    public final static Map<Integer, Buff> buffMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        //      初始化全图buff
        try {
            FileInputStream fis = new FileInputStream(new File("src/main/resources/Buff.xls"));
            LinkedHashMap<String, String> alias = new LinkedHashMap<>();
            alias.put("buff名称", "name");
            alias.put("每秒造成伤害", "addSecondValue");
            alias.put("buff类别", "type");
            alias.put("buffId", "bufferId");
            alias.put("持续时间", "keepTime");
            alias.put("每秒减免伤害", "injurySecondValue");
            alias.put("buff类型", "typeOf");
            alias.put("每秒回复生命值", "recoverValue");
            alias.put("buff的刷新时间", "endTime");
            List<Buff> buffList = ExcelUtil.excel2Pojo(fis, Buff.class, alias);
            for (Buff buff : buffList) {
                buffMap.put(buff.getBufferId(), buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化全图buff
    }
}
