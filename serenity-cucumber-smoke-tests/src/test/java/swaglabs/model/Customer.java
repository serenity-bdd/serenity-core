package swaglabs.model;

public enum Customer {
    Colin("standard_user","secret_sauce"),
    Lorenzo("locked_out_user","secret_sauce");

    final String username;
    final String password;

    Customer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static Customer withName(String name) {
        return valueOf(name);
    }
}
