package console.users;

import console.entities.Order;
import console.entities.Ticket;

public class Passenger extends User {
    public Passenger(String userID, String username, String password, String userType) {
        super(userID, username, password, userType);
    }

    public void bookTicket(Ticket ticket, Order order) {
        ticket.reserve();
        order.createOrder();
    }

    public void cancelTicket(Ticket ticket, Order order) {
        ticket.cancel();
        order.cancelOrder();
    }

    public void viewOrders() {
        // 实现查看订单逻辑
    }
}