package com.poli.lms;

public class BorrowedBook {
    private int mid;
    private String name;
    private String title;
    private String issueDate;
    private int ISBN;


    public BorrowedBook(int ISBN, String issueDate, int mid, String name, String title) {
        this.ISBN = ISBN;
        this.issueDate = issueDate;
        this.mid = mid;
        this.name = name;
        this.title = title;
    }

    public int getISBN() {
        return ISBN;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public int getMid() {
        return mid;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}
