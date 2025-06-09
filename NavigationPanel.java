package User_GUI;
import javax.swing.*;
import java.awt.*;

public class NavigationPanel extends JPanel {
    public NavigationPanel(MainFrame mainFrame) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(new Color(70, 130, 180));

        JButton homeBtn = createStyledButton("首页");
        JButton queryBtn = createStyledButton("车票查询");
        JButton orderBtn = createStyledButton("我的订单");
        JButton loginBtn = createStyledButton("登录/注册");

        add(homeBtn);
        add(queryBtn);
        add(orderBtn);
        add(loginBtn);


    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(70, 130, 180));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }
}