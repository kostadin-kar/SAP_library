package entities;

import javax.persistence.*;

@Entity
@Table(name = "notification")
public class Notification {
    private Integer id;
    private Integer userId;
    private String message;

    public Notification() {
    }

    public Notification(int userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
