package console.entities;

import java.util.Date;

public class Order {
    private String orderID;
    private String userID;
    private String trainID;
    private String status;
    private double price;
    private Date createTime;

    public Order(String orderID, String userID, String trainID, String status, double price) {
        this.orderID = orderID;
        this.userID = userID;
        this.trainID = trainID;
        this.status = status;
        this.price = price;
        this.createTime = new Date();
    }

    // 添加getStatus()方法
    public String getStatus() {
        return status;
    }

    // 添加其他必要的getter和setter方法
    public String getOrderID() {
        return orderID;
    }

    public String getUserID() {
        return userID;
    }

    public String getTrainID() {
        return trainID;
    }

    public double getPrice() {
        return price;
    }

    public Date getCreateTime() {
        return createTime;
    }

    // 取消订单方法
    public void cancelOrder() {
        this.status = "CANCELLED";
    }

    public void createOrder() {
    }
}