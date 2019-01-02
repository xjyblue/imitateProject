package component.boss;

/**
 * Description ：nettySpringServer 副本场景的配置类
 * Created by xiaojianyu on 2018/12/29 15:07
 */
public class BossSceneConfig {

//  副本的id
    private String bossSceneId;
//  副本的场景
    private String sequences;
//  副本的截止时间
    private Long keeptime;
//  副本的名字
    private String bossSceneName;
//  最后一击奖励场景
    private String finalReward;
//  需要组队的场景
    private String needMoreMen;

    public String getNeedMoreMen() {
        return needMoreMen;
    }

    public void setNeedMoreMen(String needMoreMen) {
        this.needMoreMen = needMoreMen;
    }

    public BossSceneConfig() {
    }

    public String getFinalReward() {
        return finalReward;
    }

    public void setFinalReward(String finalReward) {
        this.finalReward = finalReward;
    }

    public String getBossSceneId() {
        return bossSceneId;
    }

    public void setBossSceneId(String bossSceneId) {
        this.bossSceneId = bossSceneId;
    }

    public String getSequences() {
        return sequences;
    }

    public void setSequences(String sequences) {
        this.sequences = sequences;
    }

    public Long getKeeptime() {
        return keeptime;
    }

    public void setKeeptime(Long keeptime) {
        this.keeptime = keeptime;
    }

    public String getBossSceneName() {
        return bossSceneName;
    }

    public void setBossSceneName(String bossSceneName) {
        this.bossSceneName = bossSceneName;
    }
}
