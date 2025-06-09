package User_GUI;

import console.Mini12306Service;
import console.users.User;
import javax.swing.*;

public class MainFrame extends JFrame {
    private Mini12306Service service;
    private User currentUser;

    public MainFrame(Mini12306Service service) {
        super("Mini-12306 火车票预订系统");
        this.service = service;
        initComponents();

    }

    private void initComponents() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 设置首页
        HomePanel homePanel = new HomePanel(service, currentUser);
        setContentPane(homePanel);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}