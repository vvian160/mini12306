package Admin_GUI;

import console.Mini12306Service;

import javax.swing.*;

public class AdminFrame extends JFrame {
    public AdminFrame(Mini12306Service service) {
        super("管理员界面");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        TrainManagementPanel trainManagementPanel = new TrainManagementPanel(service);
        OrderAuditPanel orderAuditPanel = new OrderAuditPanel(service);
        UserManagementPanel userManagementPanel = new UserManagementPanel(service);
        SystemLogPanel systemLogPanel = new SystemLogPanel(service);

        tabbedPane.addTab("车次信息管理", trainManagementPanel);
        tabbedPane.addTab("异常订单审核", orderAuditPanel);
        tabbedPane.addTab("用户信息管理", userManagementPanel);
        tabbedPane.addTab("系统日志查看", systemLogPanel);

        add(tabbedPane);
    }
}