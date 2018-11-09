package client;

import utils.DelimiterUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;
/**
 * 客户端主程序入口
 * @author xiaojianyu
 *
 */
public class text extends JFrame implements KeyListener {
    private static text frm;
    private static JTextField txt;
    private static Client bootstrap;
    text() {
        setTitle("TextField Test");
        setLocation(200, 200);
        setSize(220, 100);
    }

    public static void main(String[] args) {
        frm = new text();
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLayout(new FlowLayout());
        txt = new JTextField(12);
        txt.addKeyListener(frm);
        frm.add(txt);
        frm.setVisible(true);

        Scanner input = new Scanner(System.in);
         bootstrap = new Client(8080, "127.0.0.1");
//        while (true) {
//            Scanner sc = new Scanner(System.in);
//            String req = sc.nextLine();
//            bootstrap.sendMessage(DelimiterUtils.addDelimiter(req));
//        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getSource() == txt) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) //判断按下的键是否是回车键
            {
                String req = txt.getText();
                bootstrap.sendMessage(DelimiterUtils.addDelimiter(req));
                txt.setText("");
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }
}
