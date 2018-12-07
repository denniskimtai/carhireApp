package app.carhire.com.models;

public class ClientModel {
    protected String username;
    protected String email;
    protected String userId;
    protected String userType;

    //for a usertype owner this field refers to cars they're renting out
    public ClientModel(){

    }

    public ClientModel(String userId, String userName, String email, String userType){
        this.setEmail(email);
        this.setUsername(userName);
        this.setUserId(userId);
        this.setUserType(userType);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
