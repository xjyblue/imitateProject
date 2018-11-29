package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 客户端主程序入口
 *
 * @author xiaojianyu
 */
public class ClientStart extends JFrame implements KeyListener {
    private JTextField txt;

    private Client bootstrap;

    private JTextArea jTextArea1;

    private JTextArea jTextArea2;

    private JTextArea jTextArea3;

    private JTextArea jTextArea4;

    private JScrollPane jScrollPane1;

    private JScrollPane jScrollPane2;

    private JScrollPane jScrollPane3;

    private JScrollPane jScrollPane4;

    public volatile boolean flag = true;

    public JTextArea getjTextArea4() {
        return jTextArea4;
    }

    public void setjTextArea4(JTextArea jTextArea4) {
        this.jTextArea4 = jTextArea4;
    }

    public JScrollPane getjScrollPane4() {
        return jScrollPane4;
    }

    public void setjScrollPane4(JScrollPane jScrollPane4) {
        this.jScrollPane4 = jScrollPane4;
    }

    public JTextArea getjTextArea3() {
        return jTextArea3;
    }

    public void setjTextArea3(JTextArea jTextArea3) {
        this.jTextArea3 = jTextArea3;
    }

    public JScrollPane getjScrollPane3() {
        return jScrollPane3;
    }

    public void setjScrollPane3(JScrollPane jScrollPane3) {
        this.jScrollPane3 = jScrollPane3;
    }

    public JScrollPane getjScrollPane1() {
        return jScrollPane1;
    }

    public void setjScrollPane1(JScrollPane jScrollPane1) {
        this.jScrollPane1 = jScrollPane1;
    }

    public JScrollPane getjScrollPane2() {
        return jScrollPane2;
    }

    public void setjScrollPane2(JScrollPane jScrollPane2) {
        this.jScrollPane2 = jScrollPane2;
    }

    public JTextField getTxt() {
        return txt;
    }

    public void setTxt(JTextField txt) {
        this.txt = txt;
    }

    public Client getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(Client bootstrap) {
        this.bootstrap = bootstrap;
    }

    public JTextArea getjTextArea1() {
        return jTextArea1;
    }

    public void setjTextArea1(JTextArea jTextArea1) {
        this.jTextArea1 = jTextArea1;
    }

    public JTextArea getjTextArea2() {
        return jTextArea2;
    }

    public void setjTextArea2(JTextArea jTextArea2) {
        this.jTextArea2 = jTextArea2;
    }

    ClientStart() {
        setTitle("客户端");
        setLocation(1400, 200);
        setSize(400, 200);
    }

    public static void main(String[] args) {
        ClientStart frm = new ClientStart();
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(1200,800);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(3);
        frm.setResizable(false);

//      初始化控件
        frm.txt = new JTextField(12);

        frm.jTextArea1 = new JTextArea( 10,100);
        frm.jTextArea1.setEditable(false);
        frm.jScrollPane1 = new JScrollPane();
        frm.jScrollPane1.setBounds(new Rectangle(76, 35, 257, 193));
        frm.jScrollPane1.setViewportView(frm.jTextArea1);

        frm.jTextArea2 = new JTextArea(10,5);
        frm.jTextArea2.setEditable(false);
        frm.jScrollPane2 = new JScrollPane();
        frm.jScrollPane2.setBounds(new Rectangle(76, 35, 257, 193));
        frm.jScrollPane2.setViewportView(frm.jTextArea2);

        frm.jTextArea3 = new JTextArea(10,5);
        frm.jTextArea3.setEditable(false);
        frm.jScrollPane3 = new JScrollPane();
        frm.jScrollPane3.setBounds(new Rectangle(76, 35, 257, 193));
        frm.jScrollPane3.setViewportView(frm.jTextArea3);

        frm.jTextArea4 = new JTextArea(10,5);
        frm.jTextArea4.setEditable(false);
        frm.jScrollPane4 = new JScrollPane();
        frm.jScrollPane4.setBounds(new Rectangle(76, 35, 257, 193));
        frm.jScrollPane4.setViewportView(frm.jTextArea4);

        JLabel jLabel1 = new JLabel("命令提示信息");
        JLabel jLabel2 = new JLabel("人物所受buff提示信息");
        JLabel jLabel3 = new JLabel("怪物所受buff提示信息");
        JLabel jLabel4 = new JLabel("怪物攻击提示信息");

        JButton b = new JButton("清屏");
        b.setBounds(100, 100, 65, 30);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frm.jTextArea2.setText("");
                frm.jTextArea3.setText("");
                frm.jTextArea4.setText("");
            }
        });

        JButton b2 = new JButton("stop");
        b2.setBounds(100, 100, 65, 30);
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frm.flag=false;
            }
        });

        JButton b3 = new JButton("continue");
        b3.setBounds(100, 100, 65, 30);
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frm.flag=true;
            }
        });

//          设置布局
        GridBagLayout gridBagLayout=new GridBagLayout(); //实例化布局对象
        frm.setLayout(gridBagLayout);                     //jf窗体对象设置为GridBagLayout布局
        GridBagConstraints gridBagConstraints=new GridBagConstraints();//实例化这个对象用来对组件进行管理
        gridBagConstraints.fill=GridBagConstraints.BOTH;

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(frm.txt, gridBagConstraints);
        frm.add(frm.txt);

        gridBagConstraints.gridx=10;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(b, gridBagConstraints);
        frm.add(b);

        gridBagConstraints.gridx=15;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(b2, gridBagConstraints);
        frm.add(b2);

        gridBagConstraints.gridx=16;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(b3, gridBagConstraints);
        frm.add(b3);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=5;
        gridBagConstraints.gridwidth=20;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(jLabel1, gridBagConstraints);
        frm.add(jLabel1);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=13;
        gridBagConstraints.gridwidth=20;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(frm.jScrollPane1, gridBagConstraints);
        frm.add(frm.jScrollPane1);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=15;
        gridBagConstraints.gridwidth=20;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(jLabel2, gridBagConstraints);
        frm.add(jLabel2);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=20;
        gridBagConstraints.gridwidth=20;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(frm.jScrollPane2, gridBagConstraints);
        frm.add(frm.jScrollPane2);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=25;
        gridBagConstraints.gridwidth=20;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(jLabel3, gridBagConstraints);
        frm.add(jLabel3);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=30;
        gridBagConstraints.gridwidth=20;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(frm.jScrollPane3, gridBagConstraints);
        frm.add(frm.jScrollPane3);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=35;
        gridBagConstraints.gridwidth=20;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(jLabel4, gridBagConstraints);
        frm.add(jLabel4);

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=40;
        gridBagConstraints.gridwidth=20;
        gridBagConstraints.gridheight=1;
        gridBagLayout.setConstraints(frm.jScrollPane4, gridBagConstraints);
        frm.add(frm.jScrollPane4);

//      设置监听
        frm.txt.addKeyListener(frm);
        frm.setVisible(true);
        frm.bootstrap = new Client(8081, "127.0.0.1", frm);
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
