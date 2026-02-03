package tutorials.rest.model;

/**
 * Model class for JSONPlaceholder posts.
 * https://jsonplaceholder.typicode.com/posts
 */
public class Post {
    private Integer id;
    private String title;
    private String body;
    private Integer userId;

    // Default constructor for JSON deserialization
    public Post() {}

    public Post(String title, String body, Integer userId) {
        this.title = title;
        this.body = body;
        this.userId = userId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Post{id=" + id + ", title='" + title + "', userId=" + userId + "}";
    }
}
