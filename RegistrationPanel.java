package User_GUI;

import console.Mini12306Service;
import console.users.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationPanel extends JPanel {
    private Mini12306Service service;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneField;
    private JTextField idCardField;
    private JTextField bankCardField;

    public RegistrationPanel(Mini12306Service service) {
        this(service, "");
    }

    public RegistrationPanel(Mini12306Service service, String prefilledPhone) {
        this.service = service;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("用户名:"), gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("密码:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("确认密码:"), gbc);

        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        // 手机号
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("手机号:"), gbc);

        phoneField = new JTextField(20);
        phoneField.setText(prefilledPhone);
        gbc.gridx = 1;
        add(phoneField, gbc);

        // 身份证号
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("身份证号:"), gbc);

        idCardField = new JTextField(20);
        gbc.gridx = 1;
        add(idCardField, gbc);

        // 银行卡号
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("银行卡号:"), gbc);

        bankCardField = new JTextField(20);
        gbc.gridx = 1;
        add(bankCardField, gbc);

        // 注册按钮
        JButton registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(registerButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String phoneNumber = phoneField.getText();
                String idCard = idCardField.getText();
                String bankCard = bankCardField.getText();

                // 基本验证
                if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this, "用户名、密码和手机号不能为空");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this, "两次输入的密码不一致");
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this, "密码长度至少为6位");
                    return;
                }

                if (!phoneNumber.matches("\\d{11}")) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this, "请输入有效的11位手机号");
                    return;
                }

                if (!idCard.isEmpty() && !idCard.matches("\\d{17}[0-9Xx]")) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this, "请输入有效的18位身份证号");
                    return;
                }

                // 创建用户并注册
                User user = new User(
                        "USER" + System.currentTimeMillis(),
                        username,
                        password,
                        "Passenger",
                        idCard,
                        phoneNumber,
                        bankCard
                );

                try {
                    service.register(user);
                    JOptionPane.showMessageDialog(RegistrationPanel.this, "注册成功！请登录");

                    // 关闭注册窗口
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(RegistrationPanel.this);
                    frame.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RegistrationPanel.this,
                            "注册失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}