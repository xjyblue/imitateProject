package client;

import java.util.Scanner;

import utils.DelimiterUtils;
/**
 * 客户端主程序入口
 * @author xiaojianyu
 *
 */
public class ClientStart {
	
	public static void main(String[] args){  
		Scanner input = new Scanner(System.in);
        Client bootstrap = new Client(8080, "127.0.0.1");  
        while (true){
        	Scanner sc = new Scanner(System.in);
        	String req = sc.nextLine();
            bootstrap.sendMessage(DelimiterUtils.addDelimiter(req));  
        }  
    }  
}