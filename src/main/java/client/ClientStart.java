package client;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;

import utils.DelimiterUtils;

import javax.swing.*;

/**
 * 客户端主程序入口
 * @author xiaojianyu
 *
 */
public class ClientStart extends JFrame implements KeyListener {

    private static text frm;
    private static JTextField txt;

    ClientStart(){
        setTitle("TextField Test");
        setLocation(200, 200);
        setSize(220, 100);
    }

	public static void main(String[] args){
        frm= new text();
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLayout(new FlowLayout());
        txt = new JTextField(12);
        txt.addKeyListener(frm);
        frm.add(txt);
        frm.setVisible(true);

//		Scanner input = new Scanner(System.in);
//        Client bootstrap = new Client(8080, "127.0.0.1");
//        while (true){
//        	Scanner sc = new Scanner(System.in);
//        	String req = sc.nextLine();
//            bootstrap.sendMessage(DelimiterUtils.addDelimiter(req));
//        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent  e)
    {
        if(e.getSource()==txt)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) //判断按下的键是否是回车键
            {
                txt.setText("");
                txt.setText("Hello World!");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}