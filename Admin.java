package console.users;

import console.entities.Train;

public class Admin extends User {
    public Admin(String userID, String username, String password, String userType) {
        super(userID, username, password, userType);
    }

    public void addTrain(Train train) {
        train.addTrain();
    }

    public void manageUser() {
        // 实现管理用户逻辑
    }
}