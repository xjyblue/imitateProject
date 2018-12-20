package utils;

import component.Scene;
import context.ProjectContext;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/18 14:34
 */
public class SceneUtil {

    public static Scene getSceneByName(String areaName) {
        for (Map.Entry<String, Scene> entry : ProjectContext.sceneMap.entrySet()) {
            if(areaName.equals(entry.getValue().getName())){
                return entry.getValue();
            }
        }
        return null;
    }
}
