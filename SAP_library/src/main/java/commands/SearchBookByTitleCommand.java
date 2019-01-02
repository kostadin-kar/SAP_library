package commands;

import entities.Book;
import entities.User;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SearchBookByTitleCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        writer.print("--Enter title to search: ");
        String[] titleTokens = reader.readLine().trim().toLowerCase().split("\\s+");
        String title = String.join(" ", titleTokens);

        Query query = entityManager
                .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.title) LIKE :title AND b.isAvailable = true", Book.class);
        query.setParameter("title", "%" + title.trim() + "%");
        List<Book> books = query.getResultList();

        if (books.isEmpty()) {
            writer.println("No book in library contains such title.");
        } else {
//            books = books.stream().filter(Book::getIsAvailable).collect(Collectors.toList());

            StringBuilder builder = new StringBuilder();
            for (Book book : books) {
                builder.append("Book \'")
                        .append(book.getTitle())
                        .append("\' with id = ")
                        .append(book.getId())
                        .append(" is available at library.")
                        .append(System.lineSeparator());
            }
            writer.print(builder.toString());
        }

//        writer.print("--Check if book is fetched by users? (y/n): ");
//        while (true) {
//            String answer = reader.readLine().toLowerCase();
//            if (answer.equals("y")) {
//                Query userQuery = entityManager
//                        .createQuery("SELECT u FROM User AS u", User.class);
//                List<User> users = userQuery.getResultList();
//
//                StringBuilder builder = new StringBuilder();
//                if (!users.isEmpty()) {
//                    for (User user : users) {
//                        List<Book> userBooks = user.getBooks().stream()
//                                .filter(b -> b.getTitle().toLowerCase().contains(title))
//                                .collect(Collectors.toList());
//
//                        if (!userBooks.isEmpty()) {
//                            for (Book userBook : userBooks) {
//                                builder.append("--User \'")
//                                        .append(user.getUsername())
//                                        .append("\' has \'")
//                                        .append(userBook.getTitle())
//                                        .append("\' in their inventory.")
//                                        .append(System.lineSeparator());
//                            }
//                        }
//                    }
//                    if (builder.length() > 0) {
//                        writer.println("Users who have a book containing \'" + title + "\':");
//                        writer.print(builder.toString());
//                    } else {
//                        writer.println("No users contain book in their inventory.");
//                    }
//                } else {
//                    writer.println("No available users to ask.");
//                }
//                break;
//            } else if (answer.equals("n")) {
//                writer.println("Librarian changed their mind, they will not be asking users.");
//                break;
//            } else {
//                writer.println("Please enter a valid response.");
//            }
//        }

    }
}
