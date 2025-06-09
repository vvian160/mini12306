package User_GUI;

import console.Mini12306Service;
import console.users.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePanel extends JPanel {
    private Mini12306Service service;
    private User currentUser;

    public HomePanel(Mini12306Service service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 顶部欢迎信息
        JLabel welcomeLabel = new JLabel("欢迎使用 Mini12306 火车票预订系统", JLabel.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLUE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));

        // 中间按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 120, 30, 120));

        // 创建三个按钮
        JButton loginRegisterButton = createButton("登录/注册", new Color(0, 0, 128));
        JButton queryButton = createButton("车票查询", new Color(0, 0, 128));
        JButton orderButton = createButton("我的订单", new Color(0, 0, 128));

        // 添加按钮到面板
        buttonPanel.add(loginRegisterButton);
        buttonPanel.add(queryButton);
        buttonPanel.add(orderButton);

        // 添加组件到主面板
        add(welcomeLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // 设置按钮事件
        loginRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginWindow();
            }
        });

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showQueryWindow();
            }
        });

        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOrderWindow();
            }
        });
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        Dimension size = new Dimension(180, 50);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    private void showLoginWindow() {
        JFrame loginFrame = new JFrame("用户登录");
        LoginPanel loginPanel = new LoginPanel(service, user -> {
            this.currentUser = user;
            loginFrame.dispose();
            JOptionPane.showMessageDialog(this, "登录成功！");
        });
        loginFrame.setContentPane(loginPanel);
        loginFrame.setSize(400, 350); // 调整窗口大小以适应新内容
        loginFrame.setLocationRelativeTo(this);
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setVisible(true);
    }

    private void showQueryWindow() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录！");
            return;
        }

        JFrame queryFrame = new JFrame("车票查询");
        QueryPanel queryPanel = new QueryPanel(service);
        queryFrame.setContentPane(queryPanel);
        queryFrame.setSize(800, 600);
        queryFrame.setLocationRelativeTo(this);
        queryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        queryFrame.setVisible(true);
    }

    private void showOrderWindow() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录！");
            return;
        }

        JFrame orderFrame = new JFrame("我的订单");
        OrderPanel orderPanel = new OrderPanel(service, currentUser);
        orderFrame.setContentPane(orderPanel);
        orderFrame.setSize(800, 600);
        orderFrame.setLocationRelativeTo(this);
        orderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        orderFrame.setVisible(true);
    }
}