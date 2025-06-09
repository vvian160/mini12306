package User_GUI;

import console.Mini12306Service;
import console.entities.Order;
import console.entities.Ticket;
import console.entities.Train;
import console.users.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class OrderPanel extends JPanel {
    private Mini12306Service service;
    private User user;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private List<Order> orderList;
    private JComboBox<String> yearComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> dayComboBox;

    public OrderPanel(Mini12306Service service, User user) {
        this.service = service;
        this.user = user;
        setLayout(new BorderLayout());

        // 初始化日期选择组件
        initDateSelectionComponents();

        // 初始化表格
        initTable();

        // 刷新订单列表
        refreshOrders();

        // 添加表格到滚动面板
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initDateSelectionComponents() {
        // 初始化年、月、日下拉框
        yearComboBox = new JComboBox<>();
        monthComboBox = new JComboBox<>();
        dayComboBox = new JComboBox<>();

        // 设置默认日期为今天
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // 初始化年份
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearComboBox.addItem(String.valueOf(i));
        }
        yearComboBox.setSelectedItem(String.valueOf(currentYear));

        // 初始化月份
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(String.format("%02d", i));
        }
        monthComboBox.setSelectedItem(String.format("%02d", currentMonth));

        // 初始化日期
        updateDayComboBox(currentYear, currentMonth);
        dayComboBox.setSelectedItem(String.format("%02d", currentDay));
    }

    private void updateDayComboBox(int year, int month) {
        dayComboBox.removeAllItems();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            dayComboBox.addItem(String.format("%02d", i));
        }
    }

    private void initTable() {
        String[] columnNames = {"订单号", "车次", "出发站", "到达站", "出发时间", "票价", "状态", "操作"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // 只有操作列可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 7 ? JButton.class : Object.class;
            }
        };

        orderTable = new JTable(tableModel);

        // 设置操作列的渲染器和编辑器
        orderTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void refreshOrders() {
        tableModel.setRowCount(0);
        orderList = service.getOrdersByUser(user.getUserID());

        for (Order order : orderList) {
            Train train = findTrainById(order.getTrainID());
            String trainNumber = train != null ? train.getTrainNumber() : "未知车次";
            String departure = train != null ? getDepartureStation(train) : "未知";
            String destination = train != null ? getDestinationStation(train) : "未知";
            String departureTime = train != null ? formatDate(train.getDepartureTime()) : "未知";

            tableModel.addRow(new Object[]{
                    order.getOrderID(),
                    trainNumber,
                    departure,
                    destination,
                    departureTime,
                    order.getPrice(),
                    order.getStatus(),
                    order.getStatus().equals("CREATED") ? "改签/退票" : "查看"
            });
        }
    }

    private Train findTrainById(String trainID) {
        for (Train train : service.getAllTrains()) {
            if (train.getTrainID().equals(trainID)) {
                return train;
            }
        }
        return null;
    }

    private String getDepartureStation(Train train) {
        String[] stations = train.getRoute().split("-");
        return stations.length > 0 ? stations[0] : "";
    }

    private String getDestinationStation(Train train) {
        String[] stations = train.getRoute().split("-");
        return stations.length > 1 ? stations[stations.length - 1] : "";
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    private void showOperationDialog(Order order) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "订单操作", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel buttonPanel = new JPanel();
        JButton changeButton = new JButton("改签");
        JButton refundButton = new JButton("退票");
        JButton cancelButton = new JButton("取消");

        buttonPanel.add(changeButton);
        buttonPanel.add(refundButton);
        buttonPanel.add(cancelButton);

        dialog.add(new JLabel("请选择操作"), BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.CENTER);

        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showChangeTicketDialog(order);
            }
        });

        refundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        OrderPanel.this,
                        "确定要退票吗？退票后将扣除一定手续费。",
                        "确认退票",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    service.cancelOrder(order.getOrderID());
                    refreshOrders();
                    JOptionPane.showMessageDialog(OrderPanel.this, "退票成功！");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private void showChangeTicketDialog(Order order) {
        Train originalTrain = findTrainById(order.getTrainID());
        if (originalTrain == null) {
            JOptionPane.showMessageDialog(this, "找不到原始车次信息！");
            return;
        }

        String departure = getDepartureStation(originalTrain);
        String destination = getDestinationStation(originalTrain);

        // 创建改签对话框
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "车票改签", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        // 日期选择面板
        JPanel datePanel = createDateSelectionPanel();

        // 车次选择面板
        JScrollPane trainScrollPane = createTrainSelectionPanel(departure, destination);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("确认改签");
        JButton cancelButton = new JButton("取消");

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        dialog.add(datePanel, BorderLayout.NORTH);
        dialog.add(trainScrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户选择的车次
                int selectedRow = trainTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(dialog, "请选择要改签的车次！");
                    return;
                }

                // 获取车次信息并检查空值
                Object trainNumberObj = trainTable.getValueAt(selectedRow, 0);
                Object trainIdObj = trainTable.getValueAt(selectedRow, 4);

                if (trainNumberObj == null || trainIdObj == null) {
                    JOptionPane.showMessageDialog(dialog, "所选车次信息不完整，请重新选择！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String newTrainNumber = trainNumberObj.toString();
                String newTrainId = trainIdObj.toString();

                // 执行改签
                boolean success = service.changeTicket(
                        order.getOrderID(),
                        newTrainNumber,
                        departure,
                        destination
                );

                if (success) {
                    refreshOrders();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(OrderPanel.this, "改签成功！");
                } else {
                    JOptionPane.showMessageDialog(OrderPanel.this, "改签失败，请重试！");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private JTable trainTable;
    private DefaultTableModel trainTableModel;

    private JScrollPane createTrainSelectionPanel(String departure, String destination) {
        // 获取当前选择的日期
        String year = (String) yearComboBox.getSelectedItem();
        String month = (String) monthComboBox.getSelectedItem();
        String day = (String) dayComboBox.getSelectedItem();
        String dateStr = year + "年" + month + "月" + day + "日";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "日期解析错误，请重新选择", "错误", JOptionPane.ERROR_MESSAGE);
            return new JScrollPane(new JTable()); // 返回空表格
        }

        // 准备表格数据
        String[] columnNames = {"车次", "出发站", "到达站", "出发时间", "到达时间", "TrainID"};
        trainTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        trainTable = new JTable(trainTableModel);

        // 隐藏TrainID列
        TableColumn hiddenTrainIdColumn = trainTable.getColumnModel().getColumn(4);
        trainTable.getColumnModel().removeColumn(hiddenTrainIdColumn);

        // 获取符合条件的车次（修正：传递三个参数）
        List<Train> availableTrains = service.searchTrainsByRoute(departure, destination, date);

        // 添加到表格
        for (Train train : availableTrains) {
            trainTableModel.addRow(new Object[]{
                    train.getTrainNumber(),
                    getDepartureStation(train),
                    getDestinationStation(train),
                    formatDate(train.getDepartureTime()),
                    train.getTrainID()
            });
        }

        return new JScrollPane(trainTable);
    }

    // 新增：创建日期选择面板
    private JPanel createDateSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel dateLabel = new JLabel("选择日期:");
        dateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 创建年、月、日选择下拉框
        JComboBox<String> yearComboBox = new JComboBox<>();
        JComboBox<String> monthComboBox = new JComboBox<>();
        JComboBox<String> dayComboBox = new JComboBox<>();

        // 初始化年份（当前年前后5年）
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearComboBox.addItem(String.valueOf(i));
        }
        yearComboBox.setSelectedItem(String.valueOf(currentYear));

        // 初始化月份
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(String.format("%02d", i));
        }
        monthComboBox.setSelectedItem(String.format("%02d", calendar.get(Calendar.MONTH) + 1));

        // 初始化日期
        updateDayComboBox(dayComboBox, currentYear, calendar.get(Calendar.MONTH) + 1);

        // 添加监听器，当月或年变化时更新日期
        yearComboBox.addActionListener(e -> {
            int year = Integer.parseInt((String) yearComboBox.getSelectedItem());
            int month = Integer.parseInt((String) monthComboBox.getSelectedItem());
            updateDayComboBox(dayComboBox, year, month);
        });

        monthComboBox.addActionListener(e -> {
            int year = Integer.parseInt((String) yearComboBox.getSelectedItem());
            int month = Integer.parseInt((String) monthComboBox.getSelectedItem());
            updateDayComboBox(dayComboBox, year, month);
        });

        panel.add(dateLabel);
        panel.add(yearComboBox);
        panel.add(new JLabel("年"));
        panel.add(monthComboBox);
        panel.add(new JLabel("月"));
        panel.add(dayComboBox);
        panel.add(new JLabel("日"));

        return panel;
    }

    // 新增：更新日期下拉框
    private void updateDayComboBox(JComboBox<String> dayComboBox, int year, int month) {
        dayComboBox.removeAllItems();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            dayComboBox.addItem(String.format("%02d", i));
        }
    }

    // 新增：显示订单详情
    private void showOrderDetails(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("订单号: ").append(order.getOrderID()).append("\n");

        Train train = findTrainById(order.getTrainID());
        if (train != null) {
            details.append("车次: ").append(train.getTrainNumber()).append("\n");
            details.append("出发站: ").append(getDepartureStation(train)).append("\n");
            details.append("到达站: ").append(getDestinationStation(train)).append("\n");
            details.append("出发时间: ").append(formatDate(train.getDepartureTime())).append("\n");
        } else {
            details.append("车次信息: 未找到\n");
        }

        details.append("票价: ").append(order.getPrice()).append("元\n");
        details.append("订单状态: ").append(order.getStatus()).append("\n");

        // 获取乘客信息
        List<Ticket> tickets = service.getTicketsByOrderId(order.getOrderID());
        if (tickets != null && !tickets.isEmpty()) {
            details.append("\n乘客信息:\n");
            for (Ticket ticket : tickets) {
                details.append("- ").append(ticket.getPassengerName())
                        .append(" (座位号: ").append(ticket.getSeatNumber()).append(")\n");
            }
        }

        JOptionPane.showMessageDialog(
                this,
                details.toString(),
                "订单详情",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // 新增：自定义按钮编辑器
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }

            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // 这里可以添加按钮点击后的操作
                if (label.equals("改签/退票")) {
                    showOperationDialog(orderList.get(row));
                } else if (label.equals("查看")) {
                    showOrderDetails(orderList.get(row));
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}