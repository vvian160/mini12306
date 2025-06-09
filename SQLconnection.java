package console;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLconnection {
    // 加载数据库驱动类
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    // 声明数据库的 URL，需替换为你的数据库名称
    private static final String URL = "jdbc:mysql://localhost:3306/mini12306_db";
    // 数据库用户，需替换为你的用户名和密码
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // 连接数据库函数
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER_NAME);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 断开数据库函数
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}