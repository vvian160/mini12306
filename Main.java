package console;

import console.entities.Train;
import console.users.Admin;
import console.users.Passenger;
import User_GUI.MainFrame;
import Admin_GUI.AdminFrame;
import console.users.User;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 测试数据库连接
                Connection conn = SQLconnection.getConnection();
                if (conn != null) {
                    System.out.println("数据库连接成功");
                    SQLconnection.closeConnection(conn);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "数据库连接失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return;
            }

            // 创建 Mini12306Service 实例
            Mini12306Service service = new Mini12306Service();

            // 示例代码
            Passenger passenger = new Passenger("P001", "passenger1", "123456", "Passenger");
            Admin admin = new Admin("A001", "admin1", "123456", "Admin");

            service.addTrain(new Train("T001", "G101", Tool.TimeUtils.parseTime("08:00"), Tool.TimeUtils.parseTime("12:00"), "北京南-上海虹桥", 1234.5, 862.5, 553.5));
            service.addTrain(new Train("T002", "G103", Tool.TimeUtils.parseTime("09:00"), Tool.TimeUtils.parseTime("13:00"), "北京南-上海虹桥", 1234.5, 862.5, 553.5));

            // 注册用户
            service.register(passenger);
            service.register(admin);

            // 显示选择对话框
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "欢迎来到mini12306系统\n您是",
                    "选择身份",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"用户", "管理员"},
                    "用户"
            );

            if (choice == JOptionPane.YES_OPTION) {
                User user = service.login("passenger1", "123456");
                MainFrame mainFrame = new MainFrame(service);
                mainFrame.setVisible(true);
            } else if (choice == JOptionPane.NO_OPTION) {
                AdminFrame adminFrame = new AdminFrame(service);
                adminFrame.setVisible(true);
            }
        });
    }
}