package com.poli.lms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class IssueBookController implements Initializable {

    @FXML
    private TextField ISBNField;

    @FXML
    private TextField MIDField;

    @FXML
    private Text memberBorrowed;

    @FXML
    private Text bookAuthor;

    @FXML
    private Text bookAvailable;

    @FXML
    private VBox bookContainer;

    @FXML
    private Text bookTitle;

    @FXML
    private VBox memberContainer;

    @FXML
    private Text memberDepartment;

    @FXML
    private Text memberName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Database.connect();
        } catch (SQLException | ClassNotFoundException e) {
            showError("Database Connection Error", e.getMessage());
        }
    }

    @FXML
    void issueBookBtnHandler(ActionEvent event) {
        if (ISBNField.getText().isEmpty() || MIDField.getText().isEmpty()) {
            showAlert("Input Error", "Some fields are empty", "Please enter all required fields.");
            return;
        }

        int mid = Integer.parseInt(MIDField.getText());
        int isbn = Integer.parseInt(ISBNField.getText());

        try {
            Database.connect();
            Database.issueBook(mid, isbn);
            resetFields();
        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert("Issue Book Error", "Already borrowed", "The member already borrowed this book.");
        } catch (ClassNotFoundException | SQLException e) {
            showError("Database Error", e.getMessage());
        }
    }

    @FXML
    void bookISBNKeyPressed(KeyEvent event) {
        if (ISBNField.getText().isEmpty()) {
            bookContainer.setVisible(false);
            return;
        }
        try {
            Database.connect();
            int ISBN = Integer.parseInt(ISBNField.getText());
            ResultSet result = Database.getBookByISBN(ISBN);

            if (result.next()) {
                bookTitle.setText(result.getString("Title"));
                bookAuthor.setText(result.getString("Author"));
                bookAvailable.setText(String.valueOf(result.getInt("Available")));
                bookContainer.setVisible(true);
            } else {
                bookTitle.setText("Not found");
                bookAuthor.setText("Not found");
                bookAvailable.setText("Not found");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showError("Database Error", e.getMessage());
        }
    }

    @FXML
    void memberIdFieldKeyPressed(KeyEvent event) {
        if (MIDField.getText().isEmpty()) {
            memberContainer.setVisible(false);
            return;
        }
        try {
            Database.connect();
            int MID = Integer.parseInt(MIDField.getText());
            ResultSet result = Database.getMemberByMID(MID);

            if (result.next()) {
                memberName.setText(result.getString("Name"));
                memberDepartment.setText(result.getString("Department"));
                memberBorrowed.setText(String.valueOf(result.getInt("NumberBorrowed"))); // Fixed here
                memberContainer.setVisible(true);
            } else {
                memberName.setText("Not found");
                memberDepartment.setText("Not found");
                memberBorrowed.setText("Not found");
            }
        } catch (SQLException | ClassNotFoundException e) {
            showError("Database Error", e.getMessage());
        }
    }


    public void resetFields() {
        MIDField.setText("");
        ISBNField.setText("");
        memberContainer.setVisible(false);
        bookContainer.setVisible(false);
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("An error occurred");
        alert.setContentText(content);
        alert.showAndWait();
    }
}
