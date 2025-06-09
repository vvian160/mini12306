package User_GUI;

import console.Mini12306Service;
import console.entities.Train;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

public class QueryPanel extends JPanel {
    private Mini12306Service service;
    private JTextField departureField;
    private JTextField destinationField;
    private JComboBox<Integer> yearComboBox;
    private JComboBox<Integer> monthComboBox;
    private JComboBox<Integer> dayComboBox;
    private JCheckBox studentTicketCheckBox;
    private JCheckBox highSpeedTrainCheckBox;
    private JTable resultTable;
    private JScrollPane scrollPane;

    public QueryPanel(Mini12306Service service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        // Create result table
        resultTable = new JTable();
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("查询结果"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("查询条件"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Departure and destination
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("出发地:"), gbc);

        gbc.gridx = 1;
        departureField = new JTextField(15);
        panel.add(departureField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("→"), gbc);

        gbc.gridx = 3;
        panel.add(new JLabel("目的地:"), gbc);

        gbc.gridx = 4;
        destinationField = new JTextField(15);
        panel.add(destinationField, gbc);

        // Date selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("出发日期:"), gbc);

        gbc.gridx = 1;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        Integer[] years = new Integer[10];
        for (int i = 0; i < 10; i++) {
            years[i] = currentYear + i;
        }
        yearComboBox = new JComboBox<>(years);
        panel.add(yearComboBox, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("年"), gbc);

        gbc.gridx = 3;
        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) {
            months[i] = i + 1;
        }
        monthComboBox = new JComboBox<>(months);
        panel.add(monthComboBox, gbc);

        gbc.gridx = 4;
        panel.add(new JLabel("月"), gbc);

        gbc.gridx = 5;
        updateDayComboBox();
        panel.add(dayComboBox, gbc);

        gbc.gridx = 6;
        panel.add(new JLabel("日"), gbc);

        // Checkboxes
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        studentTicketCheckBox = new JCheckBox("学生票");
        panel.add(studentTicketCheckBox, gbc);

        gbc.gridx = 2;
        highSpeedTrainCheckBox = new JCheckBox("高铁/动车");
        panel.add(highSpeedTrainCheckBox, gbc);

        // Search button
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JButton searchButton = new JButton("查询");
        searchButton.setPreferredSize(new Dimension(100, 30));
        panel.add(searchButton, gbc);

        // Event listeners
        monthComboBox.addActionListener(e -> updateDayComboBox());
        yearComboBox.addActionListener(e -> updateDayComboBox());
        searchButton.addActionListener(e -> searchTrains());

        return panel;
    }

    private void updateDayComboBox() {
        int year = (int) yearComboBox.getSelectedItem();
        int month = (int) monthComboBox.getSelectedItem();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Integer[] days = new Integer[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) {
            days[i] = i + 1;
        }
        dayComboBox = new JComboBox<>(days);
        dayComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 16));
    }

    private void searchTrains() {
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();
        int year = (int) yearComboBox.getSelectedItem();
        int month = (int) monthComboBox.getSelectedItem();
        int day = (int) dayComboBox.getSelectedItem();

        if (departure.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入出发地和目的地", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Construct date string
        String dateStr = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
        java.util.Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "日期格式错误", "提示", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Search trains
        List<Train> trains = service.searchTrainsByRoute(departure, destination, date);

        if (trains.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到符合条件的车次", "提示", JOptionPane.INFORMATION_MESSAGE);
            resultTable.setModel(new DefaultTableModel());
            return;
        }

        // Prepare table data
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"车次", "出发地", "目的地", "出发时间", "到达时间", "历时", "商务座", "一等座", "二等座", "高级软卧", "软卧/动卧", "硬卧", "硬座", "无座", "操作"},
                0
        );

        for (Train train : trains) {
            // Simulate seat availability
            String businessSeat = "候补";
            String firstClassSeat = train.getTrainNumber().equals("D9") ? "有票" : "候补";
            String secondClassSeat = train.getTrainNumber().equals("D9") ? "有票" : "候补";
            String highSoftSleeper = "候补";
            String softSleeper = "候补";
            String hardSleeper = "候补";
            String hardSeat = "候补";
            String noSeat = "候补";

            model.addRow(new Object[]{
                    train.getTrainNumber(),
                    departure,
                    destination,
                    train.getDepartureTime(),
                    train.getArrivalTime(),
                    calculateDuration(train.getDepartureTime(), train.getArrivalTime()),
                    businessSeat,
                    firstClassSeat,
                    secondClassSeat,
                    highSoftSleeper,
                    softSleeper,
                    hardSleeper,
                    hardSeat,
                    noSeat,
                    "预订"
            });
        }

        // Set table model
        resultTable.setModel(model);

        // Set column widths
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(9).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(10).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(11).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(12).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(13).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(14).setPreferredWidth(80);

        // Add button functionality for "预订" column
        TableColumn buttonColumn = resultTable.getColumnModel().getColumn(14);
        buttonColumn.setCellRenderer(new ButtonRenderer());
        buttonColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private String calculateDuration(Date departureTime, Date arrivalTime) {
        long diff = arrivalTime.getTime() - departureTime.getTime();
        long hours = diff / (1000 * 60 * 60);
        long minutes = (diff % (1000 * 60 * 60)) / (1000 * 60);
        return hours + "小时" + minutes + "分钟";
    }

    // Button renderer
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Button editor
    private class ButtonEditor extends DefaultCellEditor {
        private String label;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            currentRow = row;
            JButton button = new JButton(label);
            button.addActionListener(e -> {
                String trainNumber = table.getValueAt(currentRow, 0).toString();
                String departure = table.getValueAt(currentRow, 1).toString();
                String destination = table.getValueAt(currentRow, 2).toString();
                bookTicket(trainNumber, departure, destination);
                fireEditingStopped();
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    private void bookTicket(String trainNumber, String departure, String destination) {
        if (service.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "请先登录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确认预订 " + trainNumber + " 次列车\n从 " + departure + " 到 " + destination + " 吗？",
                "确认预订",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = service.bookTicket(trainNumber, departure, destination);
            if (success) {
                JOptionPane.showMessageDialog(this, "预订成功！");
            } else {
                JOptionPane.showMessageDialog(this, "预订失败，请重试！");
            }
        }
    }
}