package test;

import pojo.User;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/3 16:00
 */
public class TestThird implements Runnable{

    private User user;

    TestThird(){

    }

    TestThird(User user){
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        System.out.println(user);
        this.user = user;
    }

    public static void main(String args[]) throws InterruptedException {
        User user = new User();
        User user1 = new User();
        TestThird testThird = new TestThird();
        testThird.setUser(user);
//        TestThird testThird2 = new TestThird();
//        testThird2.setUser(user1);
        Thread t1 = new Thread(testThird);

        Thread t2 = new Thread(testThird);
        t1.start();
        t2.start();
        Thread.sleep(10000);
        testThird.setUser(user1);
    }


    @Override
    public void run() {
        synchronized (User.class){
            while(true){
                System.out.println(Thread.currentThread().getName());
            }

//            try {
//               Thread.sleep(50000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
