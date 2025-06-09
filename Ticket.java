package console.entities;

public class Ticket {
    private String ticketID;
    private String trainID;
    private String seatNumber;
    private String status;
    private String passengerName;

    // 构造函数
    public Ticket(String ticketID, String trainID, String seatNumber, String status) {
        this.ticketID = ticketID;
        this.trainID = trainID;
        this.seatNumber = seatNumber;
        this.status = status;

    }

    // 添加或修正 getTrainID() 方法
    public String getTrainID() {
        return trainID;
    }

    // 其他 getter 和 setter 方法
    public String getTicketID() {
        return ticketID;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassengerName() {
        return passengerName;
    }


    // 添加 reserve() 方法
    public void reserve() {
        this.status = "Reserved";
    }

    // 添加 cancel() 方法
    public void cancel() {
        this.status = "Cancelled";
    }
}