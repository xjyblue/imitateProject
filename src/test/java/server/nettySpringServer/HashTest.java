package server.nettySpringServer;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * @ClassName HashTest
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 11:43
 * @Version 1.0
 **/
public class HashTest {

    public static void main(String args[]) {
//        List<String> nodes = Arrays.asList("ins1","ins2","ins3","ins4");
//        List<String> keys = Arrays.asList("key1","key2","key3","key4");
//        keys.stream().forEach(e -> {
//            int bucket = Hashing.consistentHash(Hashing.md5().hashString(e, Charsets.UTF_8), nodes.size());
//            System.out.println(bucket);
//            String node = nodes.get(bucket);
//            System.out.println(e + " >> " + node);
//        });
        int[] a = new int[51];
        for (int i = 1; i < 200000; i++) {
            int bucket = Hashing.consistentHash(Hashing.sha512().hashString("" + i, Charsets.UTF_8), 50);
            a[bucket]++;
        }
        for(int i=1;i<51;i++){
            System.out.println(a[i]);
        }
//        int bucket = Hashing.consistentHash(Hashing.sha512().hashString("这个1211", Charsets.UTF_8), 50);
//        int bucket2 = Hashing.consistentHash(Hashing.sha512().hashString("这2个1211", Charsets.UTF_8), 50);
//        System.out.println(bucket);
//        System.out.println(bucket2);
    }
}
