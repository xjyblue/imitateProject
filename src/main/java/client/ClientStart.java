package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 客户端主程序入口
 *
 * @author xiaojianyu
 */
public class ClientStart extends JFrame implements KeyListener {
    private static ClientStart frm;
    private static JTextField txt;
    private static Client bootstrap;

    ClientStart() {
        setTitle("TextField Test");
        setLocation(400, 400);
        setSize(220, 100);
    }

    public static void main(String[] args) {
        frm = new ClientStart();
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLayout(new FlowLayout());
        txt = new JTextField(12);
        txt.addKeyListener(frm);
        frm.add(txt);
        frm.setVisible(true);

        bootstrap = new Client(8081, "127.0.0.1");
    }

    public void keyPressed(KeyEvent e) {
        if (e.getSource() == txt) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) //判断按下的键是否是回车键
            {
                String req = txt.getText();
                txt.setText("");
                bootstrap.sendMessage(req);

            }
        }
    }


    public void keyReleased(KeyEvent e) {
    }


    public void keyTyped(KeyEvent e) {
    }
}
