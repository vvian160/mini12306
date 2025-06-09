package console.entities;

public class Payment {
    private String paymentID;
    private String orderID;
    private double amount;
    private String paymentStatus;

    public Payment(String paymentID, String orderID, double amount, String paymentStatus) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public void processPayment() {
        this.paymentStatus = "Paid";
    }

    public void refund() {
        this.paymentStatus = "Refunded";
    }
}