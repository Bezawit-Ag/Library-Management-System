package com.poli.lms;

import javafx.beans.property.*;

public class Penalty {
    private final StringProperty userId;
    private final StringProperty bookTitle;
    private final StringProperty reason;
    private final DoubleProperty amount;

    public Penalty(String userId, String bookTitle, String reason, double amount) {
        this.userId = new SimpleStringProperty(userId);
        this.bookTitle = new SimpleStringProperty(bookTitle);
        this.reason = new SimpleStringProperty(reason);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public StringProperty userIdProperty() { return userId; }
    public StringProperty bookTitleProperty() { return bookTitle; }
    public StringProperty reasonProperty() { return reason; }
    public DoubleProperty amountProperty() { return amount; }
}
