package Admin_GUI;

import console.Mini12306Service;
import console.entities.Train;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class TrainManagementPanel extends JPanel {
    private Mini12306Service service;
    private JTable trainTable;
    private DefaultTableModel tableModel;

    public TrainManagementPanel(Mini12306Service service) {
        this.service = service;
        setLayout(new BorderLayout());

        // 初始化表格
        initTable();

        // 刷新列车列表
        refreshTrains();
    }

    private void initTable() {
        String[] columnNames = {"列车ID", "车次", "出发时间", "到达时间", "路线", "商务座价格", "一等座价格", "二等座价格", "操作"};
        tableModel = new DefaultTableModel(columnNames, 0);

        trainTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(trainTable);
        add(scrollPane, BorderLayout.CENTER);

        // 添加操作按钮
        JButton addButton = new JButton("添加列车");
        JButton updateButton = new JButton("更新列车");
        JButton deleteButton = new JButton("删除列车");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddTrainDialog();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = trainTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(TrainManagementPanel.this, "请选择要更新的列车");
                    return;
                }

                String trainID = tableModel.getValueAt(selectedRow, 0).toString();
                Train train = findTrainById(trainID);
                if (train != null) {
                    showUpdateTrainDialog(train);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = trainTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(TrainManagementPanel.this, "请选择要删除的列车");
                    return;
                }

                String trainID = tableModel.getValueAt(selectedRow, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(TrainManagementPanel.this, "确认删除该列车?", "确认", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    service.deleteTrain(trainID);
                    refreshTrains();
                }
            }
        });
    }

    private void refreshTrains() {
        tableModel.setRowCount(0);
        List<Train> trains = service.getAllTrains();
        for (Train train : trains) {
            tableModel.addRow(new Object[]{
                    train.getTrainID(),
                    train.getTrainNumber(),
                    train.getDepartureTime(),
                    train.getArrivalTime(),
                    train.getRoute(),
                    train.getBusinessSeatPrice(),
                    train.getFirstClassPrice(),
                    train.getSecondClassPrice()
            });
        }
    }

    private Train findTrainById(String trainID) {
        List<Train> trains = service.getAllTrains();
        for (Train train : trains) {
            if (train.getTrainID().equals(trainID)) {
                return train;
            }
        }
        return null;
    }

    private void showAddTrainDialog() {
        // 弹出添加列车的对话框
        // 示例代码，具体实现根据需求调整
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "添加列车", true);
        // 添加对话框内容
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showUpdateTrainDialog(Train train) {
        // 弹出更新列车的对话框
        // 示例代码，具体实现根据需求调整
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "更新列车", true);

        // 创建表单组件
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.add(new JLabel("车次:"));
        JTextField trainNumberField = new JTextField(train.getTrainNumber());
        formPanel.add(trainNumberField);

        formPanel.add(new JLabel("出发时间:"));
        JTextField departureTimeField = new JTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(train.getDepartureTime()));
        formPanel.add(departureTimeField);

        formPanel.add(new JLabel("到达时间:"));
        JTextField arrivalTimeField = new JTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(train.getArrivalTime()));
        formPanel.add(arrivalTimeField);

        formPanel.add(new JLabel("路线:"));
        JTextField routeField = new JTextField(train.getRoute());
        formPanel.add(routeField);

        formPanel.add(new JLabel("商务座价格:"));
        JTextField businessSeatPriceField = new JTextField(String.valueOf(train.getBusinessSeatPrice()));
        formPanel.add(businessSeatPriceField);

        formPanel.add(new JLabel("一等座价格:"));
        JTextField firstClassPriceField = new JTextField(String.valueOf(train.getFirstClassPrice()));
        formPanel.add(firstClassPriceField);

        formPanel.add(new JLabel("二等座价格:"));
        JTextField secondClassPriceField = new JTextField(String.valueOf(train.getSecondClassPrice()));
        formPanel.add(secondClassPriceField);

        dialog.add(formPanel);

        // 添加确认按钮
        JButton confirmButton = new JButton("确认更新");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    train.setTrainNumber(trainNumberField.getText());
                    train.setDepartureTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(departureTimeField.getText()));
                    train.setArrivalTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(arrivalTimeField.getText()));
                    train.setRoute(routeField.getText());
                    train.setBusinessSeatPrice(Double.parseDouble(businessSeatPriceField.getText()));
                    train.setFirstClassPrice(Double.parseDouble(firstClassPriceField.getText()));
                    train.setSecondClassPrice(Double.parseDouble(secondClassPriceField.getText()));

                    if (service.updateTrain(train)) {
                        JOptionPane.showMessageDialog(TrainManagementPanel.this, "列车信息更新成功");
                        refreshTrains();
                    } else {
                        JOptionPane.showMessageDialog(TrainManagementPanel.this, "列车信息更新失败");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TrainManagementPanel.this, "输入格式错误: " + ex.getMessage());
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}