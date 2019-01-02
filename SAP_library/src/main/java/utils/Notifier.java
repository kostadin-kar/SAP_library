package utils;

import entities.Book;
import entities.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public final class Notifier {

    private static Map<String, User> notificationMap = new HashMap<>();

    public static boolean addUserNotification(User user) {
        String username = user.getUsername();
        notificationMap.put(username, user);
        return notificationMap.containsKey(username);
    }

    public static String notify(User userToNotify) {

        User user = notificationMap.get(userToNotify.getUsername());
        if (user == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (Book book : user.getBooks()) {
            if (LocalDate.now().compareTo(book.getReturnDeadline()) > 0) {
                builder.append("Return deadline of the book \'")
                        .append(book.getTitle())
                        .append("\' was on ")
                        .append(book.getReturnDeadline())
                        .append("!")
                        .append(System.lineSeparator());
            }
        }
        if (builder.length() > 0) {
            builder.insert(0, System.lineSeparator() + ">>>>>>>>>>!<<<<<<<<<<" + System.lineSeparator());
            builder.append(">>>>>>>>>>!<<<<<<<<<<").append(System.lineSeparator()).append(System.lineSeparator());
        }

        notificationMap.remove(user.getUsername());

        return builder.toString();
    }
}
