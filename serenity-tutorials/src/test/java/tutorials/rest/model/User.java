package tutorials.rest.model;

/**
 * Model class for JSONPlaceholder users.
 * https://jsonplaceholder.typicode.com/users
 */
public class User {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;

    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
