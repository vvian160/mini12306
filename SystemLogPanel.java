package Admin_GUI;

import console.Mini12306Service;

import javax.swing.*;
import java.awt.*;

public class SystemLogPanel extends JPanel {
    private Mini12306Service service;
    private JTextArea logTextArea;

    public SystemLogPanel(Mini12306Service service) {
        this.service = service;
        setLayout(new BorderLayout());

        logTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);

        // 这里假设Mini12306Service有获取系统日志的方法，目前先简单显示提示信息
        logTextArea.setText("系统日志查看功能待完善");
    }
}