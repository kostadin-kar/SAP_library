package entities;

import javax.persistence.*;
import java.util.*;

@Entity(name = "User")
@Table(name = "users")
public class User {
    private Integer id;
    private String username;
    private String password;
    private List<Book> books;
    private Boolean isRoleAdmin;

    public User() {

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.books = new ArrayList<>();
        this.isRoleAdmin = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "username", unique = true, nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(targetEntity = Book.class)
    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Column(name = "is_role_admin")
    public Boolean getIsRoleAdmin() {
        return isRoleAdmin;
    }

    public void setIsRoleAdmin(Boolean roleAdmin) {
        isRoleAdmin = roleAdmin;
    }
}
