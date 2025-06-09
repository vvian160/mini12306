package console;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tool {

    // 表格按钮渲染器
    public static class ButtonRenderer extends JButton implements TableCellRenderer {
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

    // 表格按钮编辑器
    public static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private ActionListener actionListener;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    if (actionListener != null) {
                        actionListener.actionPerformed(e);
                    }
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

            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
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

        public void setActionListener(ActionListener listener) {
            this.actionListener = listener;
        }
    }

    // 时间工具类
    public static class TimeUtils {
        private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        private static final SimpleDateFormat TIME_ONLY_FORMAT = new SimpleDateFormat("HH:mm");

        public static Date parseTime(String timeStr) {
            try {
                // 尝试解析完整格式
                return TIME_FORMAT.parse(timeStr);
            } catch (ParseException e) {
                // 尝试解析仅时间格式，使用当前日期作为基准
                try {
                    Date timeOnly = TIME_ONLY_FORMAT.parse(timeStr);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(timeOnly);

                    // 获取当前系统时间的年月日
                    Calendar now = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
                    calendar.set(Calendar.MONTH, now.get(Calendar.MONTH));
                    calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

                    return calendar.getTime();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        }

        public static String formatTime(Date date) {
            return date != null ? TIME_FORMAT.format(date) : "";
        }

        // 计算两个时间之间的时间差（分钟）
        public static long getTimeDifference(String startTime, String endTime) {
            try {
                Date start = parseTime(startTime);
                Date end = parseTime(endTime);
                return (end.getTime() - start.getTime()) / (1000 * 60);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}