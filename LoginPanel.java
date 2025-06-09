package User_GUI;

import console.Mini12306Service;
import console.users.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginPanel extends JPanel {
    private Mini12306Service service;
    private JTabbedPane tabbedPane;
    private LoginListener loginListener;
    private Map<String, String> verificationCodes = new HashMap<>();

    public LoginPanel(Mini12306Service service, LoginListener listener) {
        this.service = service;
        this.loginListener = listener;
        setLayout(new BorderLayout());

        // 创建标签页面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 添加账号密码登录面板
        tabbedPane.addTab("账号密码登录", createPasswordLoginPanel());

        // 添加手机号验证码登录面板
        tabbedPane.addTab("手机号验证码登录", createPhoneLoginPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // 添加注册链接
        JLabel registerLabel = new JLabel("<html><a href=\"#\">没有账号？立即注册</a></html>");
        registerLabel.setHorizontalAlignment(JLabel.CENTER);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showRegistrationWindow();
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(registerLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPasswordLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名:"), gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码:"), gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "请输入用户名和密码");
                    return;
                }

                User user = service.login(username, password);
                if (user != null) {
                    if (loginListener != null) {
                        loginListener.onLoginSuccess(user);
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this, "用户名或密码错误");
                }
            }
        });

        return panel;
    }

    private JPanel createPhoneLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 手机号
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("手机号:"), gbc);

        JTextField phoneField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        // 验证码
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("验证码:"), gbc);

        JTextField codeField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        // 获取验证码按钮
        JButton getCodeButton = new JButton("获取验证码");
        getCodeButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(getCodeButton, gbc);

        getCodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phoneNumber = phoneField.getText();
                if (phoneNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "请输入手机号");
                    return;
                }

                // 验证手机号格式（简化版）
                if (!phoneNumber.matches("\\d{11}")) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "请输入有效的11位手机号");
                    return;
                }

                String code = generateVerificationCode();
                verificationCodes.put(phoneNumber, code);
                JOptionPane.showMessageDialog(LoginPanel.this, "验证码已发送到 " +
                        phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7) +
                        "\n验证码: " + code);

                // 启动倒计时
                startCountdown(getCodeButton);
            }
        });

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phoneNumber = phoneField.getText();
                String code = codeField.getText();

                if (phoneNumber.isEmpty() || code.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "请输入手机号和验证码");
                    return;
                }

                String storedCode = verificationCodes.get(phoneNumber);
                if (storedCode != null && storedCode.equals(code)) {
                    User user = service.loginByPhoneNumber(phoneNumber);
                    if (user != null) {
                        if (loginListener != null) {
                            loginListener.onLoginSuccess(user);
                        }
                    } else {
                        // 如果手机号未注册，询问是否注册
                        int choice = JOptionPane.showConfirmDialog(
                                LoginPanel.this,
                                "该手机号未注册，是否立即注册？",
                                "注册提示",
                                JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            showRegistrationWindowWithPhone(phoneNumber);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this, "验证码错误");
                }
            }
        });

        return panel;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private void startCountdown(JButton button) {
        button.setEnabled(false);
        Timer timer = new Timer(1000, new ActionListener() {
            int seconds = 60;

            @Override
            public void actionPerformed(ActionEvent e) {
                button.setText("重新发送(" + seconds + ")");
                seconds--;

                if (seconds < 0) {
                    button.setText("获取验证码");
                    button.setEnabled(true);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    private void showRegistrationWindow() {
        JFrame registrationFrame = new JFrame("用户注册");
        RegistrationPanel registrationPanel = new RegistrationPanel(service);
        registrationFrame.setContentPane(registrationPanel);
        registrationFrame.setSize(400, 400);
        registrationFrame.setLocationRelativeTo(this);
        registrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registrationFrame.setVisible(true);
    }

    private void showRegistrationWindowWithPhone(String phoneNumber) {
        JFrame registrationFrame = new JFrame("用户注册");
        RegistrationPanel registrationPanel = new RegistrationPanel(service, phoneNumber);
        registrationFrame.setContentPane(registrationPanel);
        registrationFrame.setSize(400, 400);
        registrationFrame.setLocationRelativeTo(this);
        registrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registrationFrame.setVisible(true);
    }

    public interface LoginListener {
        void onLoginSuccess(User user);
    }
}