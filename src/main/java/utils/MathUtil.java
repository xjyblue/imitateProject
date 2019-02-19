package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName MathUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/16 9:58
 * @Version 1.0
 **/
public class MathUtil {
    /**
     * 范围内的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max + 1) % (max - min + 2) + min;
        return s;

    }

}
