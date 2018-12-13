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

    private JTextField txt2;

    private Client bootstrap;

    private JTextArea jTextArea1;

    private JTextArea jTextArea2;

    private JTextArea jTextArea3;

    private JTextArea jTextArea4;

    private JScrollPane jScrollPane1;

    private JScrollPane jScrollPane2;

    private JScrollPane jScrollPane3;

    private JScrollPane jScrollPane4;

    private JTextArea jTextArea5;

    private JScrollPane jScrollPane5;

    private JTextArea jTextArea6;

    private JScrollPane jScrollPane6;

    private JTextArea jTextArea7;

    private JScrollPane jScrollPane7;

    private JTextArea jTextArea8;

    private JScrollPane jScrollPane8;

    private JTextArea jTextArea9;

    private JScrollPane jScrollPane9;

    public JTextArea getjTextArea9() {
        return jTextArea9;
    }

    public void setjTextArea9(JTextArea jTextArea9) {
        this.jTextArea9 = jTextArea9;
    }

    public JScrollPane getjScrollPane9() {
        return jScrollPane9;
    }

    public void setjScrollPane9(JScrollPane jScrollPane9) {
        this.jScrollPane9 = jScrollPane9;
    }

    public JTextArea getjTextArea8() {
        return jTextArea8;
    }

    public void setjTextArea8(JTextArea jTextArea8) {
        this.jTextArea8 = jTextArea8;
    }

    public JScrollPane getjScrollPane8() {
        return jScrollPane8;
    }

    public void setjScrollPane8(JScrollPane jScrollPane8) {
        this.jScrollPane8 = jScrollPane8;
    }

    public JTextArea getjTextArea7() {
        return jTextArea7;
    }

    public void setjTextArea7(JTextArea jTextArea7) {
        this.jTextArea7 = jTextArea7;
    }

    public JScrollPane getjScrollPane7() {
        return jScrollPane7;
    }

    public void setjScrollPane7(JScrollPane jScrollPane7) {
        this.jScrollPane7 = jScrollPane7;
    }

    public JTextArea getjTextArea6() {
        return jTextArea6;
    }

    public void setjTextArea6(JTextArea jTextArea6) {
        this.jTextArea6 = jTextArea6;
    }

    public JScrollPane getjScrollPane6() {
        return jScrollPane6;
    }

    public void setjScrollPane6(JScrollPane jScrollPane6) {
        this.jScrollPane6 = jScrollPane6;
    }

    public volatile boolean flag = true;

    public JTextArea getjTextArea5() {
        return jTextArea5;
    }

    public void setjTextArea5(JTextArea jTextArea5) {
        this.jTextArea5 = jTextArea5;
    }

    public JScrollPane getjScrollPane5() {
        return jScrollPane5;
    }

    public void setjScrollPane5(JScrollPane jScrollPane5) {
        this.jScrollPane5 = jScrollPane5;
    }

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
        frm.setSize(1800, 1000);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(3);
        frm.setResizable(false);



//      初始化控件
        frm.txt = new JTextField(85);
        frm.txt2 = new JTextField(58);
        frm.txt2.setEditable(false);

        frm.jTextArea1 = new JTextArea(8,1);
        frm.jTextArea1.setEditable(false);
        frm.jTextArea1.setFont(new Font("宋体",Font.BOLD,15));
        frm.jScrollPane1 = new JScrollPane();
        frm.jScrollPane1.setViewportView(frm.jTextArea1);

        frm.jTextArea2 = new JTextArea(8, 1);
        frm.jTextArea2.setEditable(false);
        frm.jTextArea2.setFont(new Font("宋体",Font.BOLD,14));
        frm.jScrollPane2 = new JScrollPane();
        frm.jScrollPane2.setViewportView(frm.jTextArea2);

        frm.jTextArea3 = new JTextArea(8, 1);
        frm.jTextArea3.setEditable(false);
        frm.jTextArea3.setFont(new Font("宋体",Font.BOLD,14));
        frm.jScrollPane3 = new JScrollPane();
        frm.jScrollPane3.setViewportView(frm.jTextArea3);

        frm.jTextArea4 = new JTextArea(8,1);
        frm.jTextArea4.setEditable(false);
        frm.jScrollPane4 = new JScrollPane();
        frm.jTextArea4.setFont(new Font("宋体",Font.BOLD,14));
        frm.jScrollPane4.setViewportView(frm.jTextArea4);

        frm.jTextArea5 = new JTextArea(8, 5);
        frm.jTextArea5.setEditable(false);
        frm.jScrollPane5 = new JScrollPane();
        frm.jScrollPane5.setBounds(new Rectangle(76, 35, 257, 193));
        frm.jTextArea5.setFont(new Font("宋体",Font.BOLD,14));
        frm.jScrollPane5.setViewportView(frm.jTextArea5);

        frm.jTextArea6 = new JTextArea(8, 5);
        frm.jTextArea6.setEditable(false);
        frm.jScrollPane6 = new JScrollPane();
        frm.jTextArea6.setFont(new Font("宋体",Font.BOLD,14));
        frm.jScrollPane6.setViewportView(frm.jTextArea6);

        frm.jTextArea7 = new JTextArea(8, 5);
        frm.jTextArea7.setEditable(false);
        frm.jScrollPane7 = new JScrollPane();
        frm.jTextArea7.setFont(new Font("宋体",Font.BOLD,14));
        frm.jScrollPane7.setViewportView(frm.jTextArea7);

        frm.jTextArea8 = new JTextArea(8, 5);
        frm.jTextArea8.setEditable(false);
        frm.jScrollPane8 = new JScrollPane();
        frm.jTextArea8.setFont(new Font("宋体",Font.BOLD,14));
        frm.jScrollPane8.setViewportView(frm.jTextArea8);

        frm.jTextArea9 = new JTextArea(8, 5);
        frm.jTextArea9.setEditable(false);
        frm.jScrollPane9 = new JScrollPane();
        frm.jTextArea9.setFont(new Font("宋体",Font.BOLD,14));
        frm.jScrollPane9.setViewportView(frm.jTextArea9);

        JLabel jLabel1 = new JLabel("命令提示信息");
        JLabel jLabel2 = new JLabel("人物所受buff提示信息");
        JLabel jLabel3 = new JLabel("怪物所受buff提示信息");
        JLabel jLabel4 = new JLabel("怪物攻击提示信息");
        JLabel jLabel5 = new JLabel("交易提示信息");
        JLabel jLabel6 = new JLabel("工会提示信息");
        JLabel jLabel7 = new JLabel("任务成就提示信息");
        JLabel jLabel8 = new JLabel("人物背包信息");
        JLabel jLabel9 = new JLabel("好友系统信息");

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
                frm.flag = false;
            }
        });

        JButton b3 = new JButton("continue");
//        b3.setBounds(100, 100, 65, 30);
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frm.flag = true;
            }
        });

//      设置布局
        GridBagLayout gridBagLayout = new GridBagLayout(); //实例化布局对象
        frm.setLayout(gridBagLayout);                     //jf窗体对象设置为GridBagLayout布局
        GridBagConstraints gridBagConstraints = new GridBagConstraints();//实例化这个对象用来对组件进行管理
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.txt, gridBagConstraints);
        frm.add(frm.txt);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.txt2, gridBagConstraints);
        frm.add(frm.txt2);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(b, gridBagConstraints);
        frm.add(b);

        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(b2, gridBagConstraints);
        frm.add(b2);

        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(b3, gridBagConstraints);
        frm.add(b3);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel1, gridBagConstraints);
        frm.add(jLabel1);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane1, gridBagConstraints);
        frm.add(frm.jScrollPane1);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel2, gridBagConstraints);
        frm.add(jLabel2);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel9, gridBagConstraints);
        frm.add(jLabel9);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane2, gridBagConstraints);
        frm.add(frm.jScrollPane2);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane9, gridBagConstraints);
        frm.add(frm.jScrollPane9);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel3, gridBagConstraints);
        frm.add(jLabel3);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane3, gridBagConstraints);
        frm.add(frm.jScrollPane3);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel4, gridBagConstraints);
        frm.add(jLabel4);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane4, gridBagConstraints);
        frm.add(frm.jScrollPane4);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel5, gridBagConstraints);
        frm.add(jLabel5);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel8, gridBagConstraints);
        frm.add(jLabel8);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane5, gridBagConstraints);
        frm.add(frm.jScrollPane5);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane8, gridBagConstraints);
        frm.add(frm.jScrollPane8);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel6, gridBagConstraints);
        frm.add(jLabel6);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(jLabel7, gridBagConstraints);
        frm.add(jLabel7);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane6, gridBagConstraints);
        frm.add(frm.jScrollPane6);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(frm.jScrollPane7, gridBagConstraints);
        frm.add(frm.jScrollPane7);

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
