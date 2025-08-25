package com.poli.lms;

public class Book {
    private int ISBN;
    private String title;
    private String author;
    private String publishedDate;
    private int available;
    private boolean donated;  // Field for donation status
    private String donorName; // Field for donor name

    // Constructor with parameters for all fields
    public Book(int ISBN, String title, String author, String publishedDate, int available, boolean donated, String donorName) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.available = available;
        this.donated = donated;
        this.donorName = donorName;
    }

    // Getters and setters
    public int getISBN() {
        return ISBN;
    }

    public void setISBN(int ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public boolean isDonated() {
        return donated;
    }

    public void setDonated(boolean donated) {
        this.donated = donated;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }
}
