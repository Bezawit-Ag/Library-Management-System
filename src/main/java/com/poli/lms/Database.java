package com.poli.lms;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CancellationException;

public class Database {

    private static Connection connection;
    private static final int PORT = 3306;
    private static final String USER = "root";
    private static final String PASSWORD = "Sealitemihret1621";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:" + PORT + "/library";

    // Connect to Database
    public static void connect() throws ClassNotFoundException, SQLException {
        if (connection != null && !connection.isClosed()) return;
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        System.out.println("Database connected successfully");
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // BOOK METHODS
    public static ResultSet getBookByISBN(int isbn) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM Book WHERE ISBN=?";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        stmt.setInt(1, isbn);
        return stmt.executeQuery();
    }

    public static ResultSet getBooks() throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM Book ORDER BY Available";
        return getConnection().prepareStatement(query).executeQuery();
    }

    public static ResultSet getDonatedBooks() throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM Book WHERE Donated=TRUE";
        return getConnection().prepareStatement(query).executeQuery();
    }

    public static void addBook(int isbn, String title, String author, String pubDate, int available) throws SQLException, ClassNotFoundException {
        addBook(isbn, title, author, pubDate, available, false, null);
    }

    public static void addBook(int isbn, String title, String author, String pubDate, int available, boolean donated, String donorName) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO Book (ISBN, Title, Author, PublishedDate, Available, Donated, DonorName) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, isbn);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setString(4, pubDate);
            stmt.setInt(5, available);
            stmt.setBoolean(6, donated);
            stmt.setString(7, donorName);
            stmt.executeUpdate();
        }
    }

    public static void editBook(int isbn, String title, String author, String pubDate, int available, boolean donated, String donorName) throws SQLException, ClassNotFoundException {
        String query = "UPDATE Book SET Title=?, Author=?, PublishedDate=?, Available=?, Donated=?, DonorName=? WHERE ISBN=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, pubDate);
            stmt.setInt(4, available);
            stmt.setBoolean(5, donated);
            stmt.setString(6, donorName);
            stmt.setInt(7, isbn);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                showAlert(Alert.AlertType.ERROR, "Book Error", "Update Failed", "Failed to update book details.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Book Updated", "Success", "Book details updated successfully.");
            }
        }
    }
    public static void removeBook(int isbn) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM Book WHERE ISBN=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Confirm Deletion
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to permanently delete \"" + rs.getString("Title") + "\"?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText("This action cannot be undone.");
                    confirm.showAndWait();

                    if (confirm.getResult() == ButtonType.YES) {
                        // Check if book is borrowed
                        try (PreparedStatement checkBorrowed = getConnection().prepareStatement(
                                "SELECT * FROM BorrowedBooks WHERE ISBN=?")) {
                            checkBorrowed.setInt(1, isbn);
                            try (ResultSet borrowed = checkBorrowed.executeQuery()) {
                                if (borrowed.next()) {
                                    showAlert(Alert.AlertType.ERROR, "Cannot Delete", "Book is still borrowed", "Please ensure the book is returned before deleting.");
                                    return;
                                }
                            }
                        }

                        // Perform deletion
                        try (PreparedStatement deleteStmt = getConnection().prepareStatement("DELETE FROM Book WHERE ISBN=?")) {
                            deleteStmt.setInt(1, isbn);
                            deleteStmt.executeUpdate();
                        }

                    } else {
                        throw new CancellationException("Deletion cancelled by user.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Book Not Found", null, "No book found with the provided ISBN.");
                }
            }
        }
    }

    // MEMBER METHODS
    public static ResultSet getMemberByMID(int mid) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM Member WHERE MID=?";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        stmt.setInt(1, mid);
        return stmt.executeQuery();
    }

    public static ResultSet getMembers() throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM Member ORDER BY MID";
        return getConnection().prepareStatement(query).executeQuery();
    }

    public static void addMember(int mid, String name, String gender, String department, int numberBorrowed) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO Member (MID, Name, Gender, Department, NumberBorrowed) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setString(2, name);
            stmt.setString(3, gender);
            stmt.setString(4, department);
            stmt.setInt(5, numberBorrowed);
            stmt.executeUpdate();
        }
    }

    public static void editMember(int mid, String name, String gender, String department) throws SQLException, ClassNotFoundException {
        String query = "UPDATE Member SET Name=?, Gender=?, Department=? WHERE MID=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, gender);
            stmt.setString(3, department);
            stmt.setInt(4, mid);
            stmt.executeUpdate();
        }
    }

    public static void removeMember(int mid) throws Exception {
        String query = "SELECT * FROM Member WHERE MID=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, mid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new Exception("Member not found.");
                if (rs.getInt("NumberBorrowed") > 0) {
                    showAlert(Alert.AlertType.ERROR, "Member Error", "Cannot Remove Member", "Return all borrowed books first.");
                    throw new Exception("Canceled");
                }
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete " + rs.getString("Name") + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait();
                if (confirm.getResult() == ButtonType.YES) {
                    try (PreparedStatement deleteStmt = getConnection().prepareStatement("DELETE FROM Member WHERE MID=?")) {
                        deleteStmt.setInt(1, mid);
                        deleteStmt.executeUpdate();
                    }
                } else {
                    throw new Exception("Canceled");
                }
            }
        }
    }

    // BORROWING METHODS
    public static void issueBook(int mid, int isbn) throws SQLException, ClassNotFoundException {
        try (PreparedStatement bookStmt = getConnection().prepareStatement("SELECT * FROM Book WHERE ISBN=?");
             PreparedStatement memberStmt = getConnection().prepareStatement("SELECT * FROM Member WHERE MID=?")) {

            bookStmt.setInt(1, isbn);
            memberStmt.setInt(1, mid);

            try (ResultSet bookRs = bookStmt.executeQuery(); ResultSet memberRs = memberStmt.executeQuery()) {
                if (!bookRs.next()) {
                    showAlert(Alert.AlertType.ERROR, "Issue Book", "Error", "Book not found.");
                    return;
                }
                if (!memberRs.next()) {
                    showAlert(Alert.AlertType.ERROR, "Issue Book", "Error", "Member not found.");
                    return;
                }
                if (bookRs.getInt("Available") <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Issue Book", "Error", "No copies available.");
                    return;
                }
                if (memberRs.getInt("NumberBorrowed") >= 5) {
                    showAlert(Alert.AlertType.ERROR, "Issue Book", "Error", "Member has reached borrowing limit.");
                    return;
                }
            }
        }

        String issueDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (PreparedStatement updateBook = getConnection().prepareStatement("UPDATE Book SET Available=Available-1 WHERE ISBN=?");
             PreparedStatement updateMember = getConnection().prepareStatement("UPDATE Member SET NumberBorrowed=NumberBorrowed+1 WHERE MID=?");
             PreparedStatement insertBorrowed = getConnection().prepareStatement("INSERT INTO BorrowedBooks (MID, ISBN, IssueDate) VALUES (?, ?, ?)")) {

            updateBook.setInt(1, isbn);
            updateBook.executeUpdate();

            updateMember.setInt(1, mid);
            updateMember.executeUpdate();

            insertBorrowed.setInt(1, mid);
            insertBorrowed.setInt(2, isbn);
            insertBorrowed.setString(3, issueDate);
            insertBorrowed.executeUpdate();
        }
    }

    public static void returnBook(int mid, int isbn, String status) throws SQLException, ClassNotFoundException {
        if (!isBookBorrowed(mid, isbn)) {
            throw new SQLException("No borrowed record found.");
        }

        if ("Lost".equalsIgnoreCase(status)) {
            addPenalty(mid, isbn, "Lost Book", 500.0);
        } else if ("Damaged".equalsIgnoreCase(status)) {
            addPenalty(mid, isbn, "Damaged Book", 300.0);
        }

        try (PreparedStatement deleteBorrowed = getConnection().prepareStatement("DELETE FROM BorrowedBooks WHERE MID=? AND ISBN=?")) {
            deleteBorrowed.setInt(1, mid);
            deleteBorrowed.setInt(2, isbn);
            deleteBorrowed.executeUpdate();
        }

        try (PreparedStatement updateMember = getConnection().prepareStatement("UPDATE Member SET NumberBorrowed=NumberBorrowed-1 WHERE MID=?")) {
            updateMember.setInt(1, mid);
            updateMember.executeUpdate();
        }

        if ("Normal".equalsIgnoreCase(status)) {
            try (PreparedStatement updateBook = getConnection().prepareStatement("UPDATE Book SET Available=Available+1 WHERE ISBN=?")) {
                updateBook.setInt(1, isbn);
                updateBook.executeUpdate();
            }
        }
    }

    public static void addPenalty(int memberId, int isbn, String penaltyType, double amount) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO Penalty (member_id, isbn, penalty_type, amount, date) VALUES (?, ?, ?, ?, CURDATE())";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, isbn);
            stmt.setString(3, penaltyType);
            stmt.setDouble(4, amount);
            stmt.executeUpdate();
        }
    }

    public static boolean isBookBorrowed(int mid, int isbn) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM BorrowedBooks WHERE MID=? AND ISBN=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static ResultSet getBorrowedBooks() throws SQLException, ClassNotFoundException {
        String query = "SELECT b.MID, m.Name, b.ISBN, bk.Title, b.IssueDate FROM BorrowedBooks b JOIN Member m ON b.MID = m.MID JOIN Book bk ON b.ISBN = bk.ISBN";
        return getConnection().prepareStatement(query).executeQuery();
    }

    public static void registerStudentProperty(int mid, String deviceType, String model, String serial, String date) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO StudentProperty (MID, DeviceType, Model, SerialNumber, EntryDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setString(2, deviceType);
            stmt.setString(3, model);
            stmt.setString(4, serial);
            stmt.setString(5, date);
            stmt.executeUpdate();
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
