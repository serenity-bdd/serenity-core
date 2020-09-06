package net.serenitybdd.screenplay.webtests.model;

public class LoginAccount {
    private String username;
    private String password;

    public LoginAccount(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public LoginAccount setUsername(String username) {
        this.username = username;
        return this;
    }

    public LoginAccount setPassword(String password){
        this.password = password;
        return this;
    }
}
