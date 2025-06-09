package console;

import console.entities.*;
import console.users.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Mini12306Service {
    private User currentUser;

    // 注册用户
    public void register(User user) {
        String sql = "INSERT INTO users (user_id, username, password, user_type, id_card, phone_number, bank_card) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserID());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUserType());
            pstmt.setString(5, user.getIdCard());
            pstmt.setString(6, user.getPhoneNumber());
            pstmt.setString(7, user.getBankCard());
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 用户登录
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username =? AND password =?";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String userID = rs.getString("user_id");
                String userType = rs.getString("user_type");
                String idCard = rs.getString("id_card");
                String phoneNumber = rs.getString("phone_number");
                String bankCard = rs.getString("bank_card");
                User user = new User(userID, username, password, userType, idCard, phoneNumber, bankCard);
                this.currentUser = user;
                return user;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 添加列车信息
    public void addTrain(Train train) {
        String sql = "INSERT INTO trains (train_id, train_number, departure_time, arrival_time, route, business_seat_price, first_class_price, second_class_price) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, train.getTrainID());
            pstmt.setString(2, train.getTrainNumber());
            java.sql.Timestamp departureTime = new java.sql.Timestamp(train.getDepartureTime().getTime());
            pstmt.setTimestamp(3, departureTime);
            java.sql.Timestamp arrivalTime = new java.sql.Timestamp(train.getArrivalTime().getTime());
            pstmt.setTimestamp(4, arrivalTime);
            pstmt.setString(5, train.getRoute());
            pstmt.setDouble(6, train.getBusinessSeatPrice());
            pstmt.setDouble(7, train.getFirstClassPrice());
            pstmt.setDouble(8, train.getSecondClassPrice());
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询列车信息
    public List<Train> searchTrainsByRoute(String departure, String destination, java.util.Date date) {
        List<Train> result = new ArrayList<>();
        String sql = "SELECT * FROM trains WHERE route LIKE ? AND route LIKE ?";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + departure + "%");
            pstmt.setString(2, "%" + destination + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String trainID = rs.getString("train_id");
                String trainNumber = rs.getString("train_number");
                java.sql.Timestamp departureTime = rs.getTimestamp("departure_time");
                java.sql.Timestamp arrivalTime = rs.getTimestamp("arrival_time");
                String route = rs.getString("route");
                double businessSeatPrice = rs.getDouble("business_seat_price");
                double firstClassPrice = rs.getDouble("first_class_price");
                double secondClassPrice = rs.getDouble("second_class_price");
                Train train = new Train(trainID, trainNumber, new java.util.Date(departureTime.getTime()), new java.util.Date(arrivalTime.getTime()), route, businessSeatPrice, firstClassPrice, secondClassPrice);
                result.add(train);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取用户订单
    public List<Order> getOrdersByUser(String userID) {
        List<Order> result = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String orderID = rs.getString("order_id");
                String trainID = rs.getString("train_id");
                String status = rs.getString("status");
                double price = rs.getDouble("price");
                Order order = new Order(orderID, userID, trainID, status, price);
                result.add(order);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 创建订单
    public Order createOrder(String userID, String trainID, String ticketID, double price) {
        String orderID = "ORD" + UUID.randomUUID().toString().substring(0, 8);
        String sql = "INSERT INTO orders (order_id, user_id, train_id, status, price) VALUES (?,?,?,?,?)";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderID);
            pstmt.setString(2, userID);
            pstmt.setString(3, trainID);
            pstmt.setString(4, "CREATED");
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return new Order(orderID, userID, trainID, "CREATED", price);
    }

    // 取消订单
    public void cancelOrder(String orderID) {
        String sql = "UPDATE orders SET status = 'CANCELLED' WHERE order_id = ?";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderID);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据订单ID获取车票信息
    public List<Ticket> getTicketsByOrderId(String orderId) {
        List<Ticket> result = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE order_id = ?";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String ticketID = rs.getString("ticket_id");
                String trainID = rs.getString("train_id");
                String seatNumber = rs.getString("seat_number");
                String status = rs.getString("status");
                // 假设 Ticket 类有对应的构造函数
                Ticket ticket = new Ticket(ticketID, trainID, seatNumber, status);
                result.add(ticket);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 通过手机号登录
    public User loginByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM users WHERE phone_number = ?";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String userID = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String userType = rs.getString("user_type");
                String idCard = rs.getString("id_card");
                String bankCard = rs.getString("bank_card");
                User user = new User(userID, username, password, userType, idCard, phoneNumber, bankCard);
                this.currentUser = user;
                return user;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 更新列车信息
    public boolean updateTrain(Train train) {
        String sql = "UPDATE trains SET train_number = ?, departure_time = ?, arrival_time = ?, route = ?, business_seat_price = ?, first_class_price = ?, second_class_price = ? WHERE train_id = ?";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, train.getTrainNumber());
            pstmt.setTimestamp(2, new java.sql.Timestamp(train.getDepartureTime().getTime()));
            pstmt.setTimestamp(3, new java.sql.Timestamp(train.getArrivalTime().getTime()));
            pstmt.setString(4, train.getRoute());
            pstmt.setDouble(5, train.getBusinessSeatPrice());
            pstmt.setDouble(6, train.getFirstClassPrice());
            pstmt.setDouble(7, train.getSecondClassPrice());
            pstmt.setString(8, train.getTrainID());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 删除列车信息
    public boolean deleteTrain(String trainID) {
        String sql = "DELETE FROM trains WHERE train_id = ?";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trainID);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 获取当前用户
    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getUsers() {
        List<User> result = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String userID = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String userType = rs.getString("user_type");
                String idCard = rs.getString("id_card");
                String phoneNumber = rs.getString("phone_number");
                String bankCard = rs.getString("bank_card");
                User user = new User(userID, username, password, userType, idCard, phoneNumber, bankCard);
                result.add(user);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 改签车票
    public boolean changeTicket(String oldOrderID, String newTrainId, String departure, String destination) {
        if (currentUser == null) {
            return false;
        }

        Connection conn = null;
        try {
            conn = SQLconnection.getConnection();
            conn.setAutoCommit(false); // 开始事务

            // 1. 查找原订单和车票
            Order oldOrder = null;
            List<Ticket> oldTickets = new ArrayList<>();

            // 获取原订单
            String sql = "SELECT * FROM orders WHERE order_id = ? AND user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, oldOrderID);
                pstmt.setString(2, currentUser.getUserID());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    oldOrder = new Order(
                            rs.getString("order_id"),
                            rs.getString("user_id"),
                            rs.getString("train_id"),
                            rs.getString("status"),
                            rs.getDouble("price")
                    );
                }
            }

            if (oldOrder == null || !oldOrder.getStatus().equals("CREATED")) {
                return false;
            }

            // 获取原车票
            sql = "SELECT * FROM tickets WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, oldOrderID);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    oldTickets.add(new Ticket(
                            rs.getString("ticket_id"),
                            rs.getString("train_id"),
                            rs.getString("seat_number"),
                            rs.getString("status")
                    ));
                }
            }

            // 2. 查找新车次
            Train newTrain = null;
            sql = "SELECT * FROM trains WHERE train_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newTrainId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    newTrain = new Train(
                            rs.getString("train_id"),
                            rs.getString("train_number"),
                            new Date(rs.getTimestamp("departure_time").getTime()),
                            new Date(rs.getTimestamp("arrival_time").getTime()),
                            rs.getString("route"),
                            rs.getDouble("business_seat_price"),
                            rs.getDouble("first_class_price"),
                            rs.getDouble("second_class_price")
                    );
                }
            }

            if (newTrain == null) {
                return false;
            }

            // 3. 创建新订单
            String newOrderID = "ORD" + UUID.randomUUID().toString().substring(0, 8);
            double price = oldOrder.getPrice(); // 保持原价格或重新计算

            sql = "INSERT INTO orders (order_id, user_id, train_id, status, price) VALUES (?,?,?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newOrderID);
                pstmt.setString(2, currentUser.getUserID());
                pstmt.setString(3, newTrainId);
                pstmt.setString(4, "CREATED");
                pstmt.setDouble(5, price);
                pstmt.executeUpdate();
            }

            // 4. 创建新车票
            for (Ticket oldTicket : oldTickets) {
                String newTicketID = "TKT" + UUID.randomUUID().toString().substring(0, 8);
                // 这里简化处理，实际应根据座位类型分配新座位
                String newSeatNumber = "新座位";

                sql = "INSERT INTO tickets (ticket_id, order_id, train_id, seat_number, status) VALUES (?,?,?,?,?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newTicketID);
                    pstmt.setString(2, newOrderID);
                    pstmt.setString(3, newTrainId);
                    pstmt.setString(4, newSeatNumber);
                    pstmt.setString(5, "CREATED");
                    pstmt.executeUpdate();
                }
            }

            // 5. 取消原订单
            sql = "UPDATE orders SET status = 'CANCELLED' WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, oldOrderID);
                pstmt.executeUpdate();
            }

            conn.commit(); // 提交事务
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 预订车票
    public boolean bookTicket(String trainNumber, String departure, String destination) {
        if (currentUser == null) {
            return false;
        }

        // 查找对应的列车
        Train train = null;
        for (Train t : getAllTrains()) {
            if (t.getTrainNumber().equals(trainNumber) &&
                    t.getRoute().contains(departure) &&
                    t.getRoute().contains(destination)) {
                train = t;
                break;
            }
        }

        if (train == null) {
            return false;
        }

        // 创建订单
        String orderID = "ORD" + UUID.randomUUID().toString().substring(0, 8);
        double price = 100.0; // 简单示例，实际应根据列车和座位类型计算

        String sql = "INSERT INTO orders (order_id, user_id, train_id, status, price) VALUES (?,?,?,?,?)";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderID);
            pstmt.setString(2, currentUser.getUserID());
            pstmt.setString(3, train.getTrainID());
            pstmt.setString(4, "CREATED");
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // 获取所有列车
    public List<Train> getAllTrains() {
        List<Train> result = new ArrayList<>();
        String sql = "SELECT * FROM trains";
        try (Connection conn = SQLconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String trainID = rs.getString("train_id");
                String trainNumber = rs.getString("train_number");
                java.sql.Timestamp departureTime = rs.getTimestamp("departure_time");
                java.sql.Timestamp arrivalTime = rs.getTimestamp("arrival_time");
                String route = rs.getString("route");
                double businessSeatPrice = rs.getDouble("business_seat_price");
                double firstClassPrice = rs.getDouble("first_class_price");
                double secondClassPrice = rs.getDouble("second_class_price");
                Train train = new Train(trainID, trainNumber, new java.util.Date(departureTime.getTime()), new java.util.Date(arrivalTime.getTime()), route, businessSeatPrice, firstClassPrice, secondClassPrice);
                result.add(train);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 设置当前用户
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}