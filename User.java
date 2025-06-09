package console.users;

public class User {
    private String userID;
    private String username;
    private String password;
    private String userType;
    private String idCard;
    private String phoneNumber;
    private String bankCard;

    public User(String userID, String username, String password, String userType, String idCard, String phoneNumber, String bankCard) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.idCard = idCard;
        this.phoneNumber = phoneNumber;
        this.bankCard = bankCard;
    }
    public User(String userID, String username, String password, String userType) {
        this(userID, username, password, userType, "", "", "");
    }

    // Getters and Setters for new fields
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public void login() {
        // 实现登录逻辑
    }

    public void logout() {
        // 实现退出登录逻辑
    }

    public void changePassword() {
        // 实现修改密码逻辑
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }
}