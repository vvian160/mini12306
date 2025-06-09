package console.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Train {
    private String trainID;
    private String trainNumber;
    private Date departureTime;
    private Date arrivalTime;
    private String route;
    private double price;
    private double businessSeatPrice;    // 商务座价格
    private double firstClassPrice;      // 一等座价格
    private double secondClassPrice;     // 二等座价格

    // 座位余量信息
    private int businessSeatCount = 10;
    private int firstClassCount = 50;
    private int secondClassCount = 100;

    // 构造函数
    public Train(String trainID, String trainNumber, Date departureTime, Date arrivalTime,
                 String route, double businessSeatPrice, double firstClassPrice, double secondClassPrice) {
        this.trainID = trainID;
        this.trainNumber = trainNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.route = route;
        this.businessSeatPrice = businessSeatPrice;
        this.firstClassPrice = firstClassPrice;
        this.secondClassPrice = secondClassPrice;
    }

    private Date parseTime(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addTrain() {
        // 实现添加列车的逻辑
    }

    public void updateTrain() {
        // 实现更新列车信息的逻辑
    }

    public void deleteTrain() {
        // 实现删除列车的逻辑
    }

    public String getTrainID() {
        return trainID;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public String getRoute() {
        return route;
    }

    public double getPrice() {
        return price;
    }

    public double getBusinessSeatPrice() {
        return businessSeatPrice;
    }

    public double getFirstClassPrice() {
        return firstClassPrice;
    }

    public double getSecondClassPrice() {
        return secondClassPrice;
    }

    // 添加 setter 方法
    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setBusinessSeatPrice(double businessSeatPrice) {
        this.businessSeatPrice = businessSeatPrice;
    }

    public void setFirstClassPrice(double firstClassPrice) {
        this.firstClassPrice = firstClassPrice;
    }

    public void setSecondClassPrice(double secondClassPrice) {
        this.secondClassPrice = secondClassPrice;
    }
}