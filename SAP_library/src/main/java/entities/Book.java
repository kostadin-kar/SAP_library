package entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "Book")
@Table(name = "books")
public class Book {
    private Integer id;
    private String title;
    private String author;
    private LocalDate releaseDate;
    private boolean isAvailable;
    private LocalDate returnDeadline;

    public Book() {
    }

    public Book(String title, String author, LocalDate releaseDate) {
        this.title = title;
        this.author = author;
        this.releaseDate = releaseDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "author", nullable = false)
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name = "release_date", nullable = false)
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Column(name = "is_available")
    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
        isAvailable = available;
    }

    @Column(name = "return_deadline")
    public LocalDate getReturnDeadline() {
        return returnDeadline;
    }

    public void setReturnDeadline(LocalDate returnDeadline) {
        this.returnDeadline = returnDeadline;
    }

    @Transient
    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((Book) obj).getId()) && this.title.equals(((Book) obj).title) && this.author.equals(((Book) obj).author);
    }

    @Transient
    @Override
    public int hashCode() {
        return 31 + this.id + this.title.hashCode() + this.author.hashCode();
    }
}
