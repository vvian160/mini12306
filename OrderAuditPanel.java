package Admin_GUI;

import console.Mini12306Service;
import console.Tool;
import console.entities.Train;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderAuditPanel extends JPanel {
    private Mini12306Service service;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public OrderAuditPanel(Mini12306Service service) {
        this.service = service;
        setLayout(new BorderLayout());

        // 创建表格面板
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[] columnNames = {"订单号", "用户ID", "车次ID", "状态", "操作"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshTrainTable();

        return panel;
    }

    private void refreshTrainTable() {
        tableModel.setRowCount(0);
        List<Train> trains = service.getAllTrains();
        for (Train train : trains) {
            tableModel.addRow(new Object[]{
                    train.getTrainID(),  // 显示 TrainID 而不是车次号（根据需要调整）
                    train.getTrainNumber(),
                    train.getRoute().split("-")[0],
                    train.getRoute().split("-")[1],
                    Tool.TimeUtils.formatTime(train.getDepartureTime()),
                    Tool.TimeUtils.formatTime(train.getArrivalTime()),
                    train.getPrice()  // 确保 Train 类有 getPrice() 方法
            });
        }
    }
}