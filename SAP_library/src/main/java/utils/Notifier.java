package utils;

import db.NotificationRepo;
import entities.Book;
import entities.Notification;
import entities.User;

import java.time.LocalDate;
import java.util.List;

public final class Notifier {

    private static final String EXCLAMATION_BOUNDARY = ">>>>>>>>>>!<<<<<<<<<<";
    private static final String NOTIFICATION_EXPIRED_MESSAGE = "* notification expired *";
    private static final String RETURN_DEADLINE_ALERT_MESSAGE
            = "~ Return deadline of book \'%s\' by %s was due on %s";

    private static NotificationRepo notifyRepo;

    public static void setRepository(NotificationRepo notificationRepo) {
        notifyRepo = notificationRepo;
    }

    public static void addUserNotification(User user) {

        StringBuilder builder = new StringBuilder();
        for (Book book : user.getBooks()) {
            if (LocalDate.now().compareTo(book.getReturnDeadline()) > 0) {
                builder.append(String.format(
                        RETURN_DEADLINE_ALERT_MESSAGE,
                        book.getTitle(), book.getAuthor(), book.getReturnDeadline()
                ))
                        .append(System.lineSeparator());
            }
        }
        builder.insert(0, System.lineSeparator() + EXCLAMATION_BOUNDARY + System.lineSeparator());
        builder.append(EXCLAMATION_BOUNDARY).append(System.lineSeparator()).append(System.lineSeparator());

        String message = builder.toString();

        List<Notification> notifications
                = notifyRepo.selectNotificationByUserId(user.getId());
        if (notifications != null && !notifications.isEmpty()) {
            Notification notification = notifications.get(0);
            notification.setMessage(message);
            notifyRepo.persist(notification);
        } else {
            if (message.length() > 1) {
                notifyRepo.persist(new Notification(user.getId(), message));
            }
        }

    }

    public static String notify(User userToNotify) {

        List<Notification> notifications = notifyRepo.selectNotificationByUserId(userToNotify.getId());
        if (notifications == null || notifications.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (Book book : userToNotify.getBooks()) {
            if (LocalDate.now().compareTo(book.getReturnDeadline()) > 0) {
                builder.append(String.format(
                        RETURN_DEADLINE_ALERT_MESSAGE,
                        book.getTitle(), book.getAuthor(), book.getReturnDeadline()
                ))
                        .append(System.lineSeparator());
            }
        }
        String message = builder.toString();

        Notification notification = notifications.get(0);

        if (!message.isEmpty()) {
            builder.setLength(0);
            builder.append(System.lineSeparator()).append(EXCLAMATION_BOUNDARY).append(System.lineSeparator());
            builder.append(message);
            builder.append(EXCLAMATION_BOUNDARY).append(System.lineSeparator()).append(System.lineSeparator());
            message = builder.toString();

            notification.setMessage(message);
            notifyRepo.persist(notification);
        }

        if (message.isEmpty()) {
            notifyRepo.remove(notification);
            builder.append(System.lineSeparator()).append(NOTIFICATION_EXPIRED_MESSAGE).append(System.lineSeparator());
            message = builder.toString();
        }

        return message;
    }
}
