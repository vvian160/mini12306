package Admin_GUI;

import console.Mini12306Service;
import console.users.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private Mini12306Service service;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementPanel(Mini12306Service service) {
        this.service = service;
        setLayout(new BorderLayout());

        // 初始化表格
        initTable();

        // 刷新用户列表
        refreshUsers();
    }

    private void initTable() {
        String[] columnNames = {"用户ID", "用户名", "用户类型", "身份证号", "手机号", "银行卡号"};
        tableModel = new DefaultTableModel(columnNames, 0);

        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshUsers() {
        tableModel.setRowCount(0);
        List<User> users = service.getUsers();

        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getUserID(),
                    user.getUsername(),
                    user.getUserType(),
                    user.getIdCard(),
                    user.getPhoneNumber(),
                    user.getBankCard()
            });
        }
    }
}