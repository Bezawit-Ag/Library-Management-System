package com.poli.lms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

public class ReturnBookController implements Initializable {

    @FXML private TextField ISBNField;
    @FXML private TextField MIDField;
    @FXML private Text bookAuthor;
    @FXML private Text bookAvailable;
    @FXML private VBox bookContainer;
    @FXML private Text bookTitle;
    @FXML private Text memberBorrowed;
    @FXML private VBox memberContainer;
    @FXML private Text memberDepartment;
    @FXML private Text memberName;
    @FXML private Button returnBookBtn;
    @FXML private ComboBox<String> statusComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Database.connect();
        } catch (SQLException | ClassNotFoundException e) {
            showError("Initialization Error", "Database Connection Failed", e.getMessage());
        }

        statusComboBox.getItems().addAll("Normal", "Lost", "Damaged");

        // Hide containers initially
        bookContainer.setVisible(false);
        memberContainer.setVisible(false);
    }


    @FXML
    void returnBookBtnHandler(ActionEvent event) {
        if (ISBNField.getText().isEmpty() || MIDField.getText().isEmpty() || statusComboBox.getValue() == null) {
            showWarning("Some Fields are empty", "Please enter all fields and select status");
            return;
        }

        try {
            int mid = Integer.parseInt(MIDField.getText().trim());
            int isbn = Integer.parseInt(ISBNField.getText().trim());
            String status = statusComboBox.getValue();

            // Ask for penalty confirmation if needed
            if ("Lost".equalsIgnoreCase(status)) {
                if (!confirmPenalty("Lost Book", 500)) {
                    showWarning("Penalty Required", "Return canceled by user.");
                    return;
                }
            } else if ("Damaged".equalsIgnoreCase(status)) {
                if (!confirmPenalty("Damaged Book", 300)) {
                    showWarning("Penalty Required", "Return canceled by user.");
                    return;
                }
            }

            Database.returnBook(mid, isbn, status);

            showInfo("Book Returned", "Book returned successfully!");
            resetFields();
        } catch (NumberFormatException e) {
            showWarning("Invalid Input", "Please enter valid numeric MID and ISBN.");
        } catch (SQLException | ClassNotFoundException e) {
            showError("Return Book Error", "Failed to Return Book", e.getMessage());
        }
    }





    @FXML
    void bookISBNKeyPressed(KeyEvent event) {
        if (ISBNField.getText().isEmpty()) {
            bookContainer.setVisible(false);
            return;
        }
        try {
            int isbn = Integer.parseInt(ISBNField.getText().trim());
            ResultSet result = Database.getBookByISBN(isbn);

            if (result.next()) {
                bookTitle.setText(result.getString(2));
                bookAuthor.setText(result.getString(3));
                bookAvailable.setText(result.getString(5));
                bookContainer.setVisible(true);
            } else {
                showBookNotFound();
            }
        } catch (NumberFormatException | SQLException | ClassNotFoundException e) {  // <-- Add here
            showBookNotFound();
        }
    }


    @FXML
    void memberIdFieldKeyPressed(KeyEvent event) {
        if (MIDField.getText().isEmpty()) {
            memberContainer.setVisible(false);
            return;
        }
        try {
            int mid = Integer.parseInt(MIDField.getText().trim());
            ResultSet result = Database.getMemberByMID(mid);

            if (result.next()) {
                memberName.setText(result.getString(2));
                memberDepartment.setText(result.getString(4));
                memberBorrowed.setText(result.getString(5));
                memberContainer.setVisible(true);
            } else {
                showMemberNotFound();
            }
        } catch (NumberFormatException | SQLException | ClassNotFoundException e) {
            showMemberNotFound();
        }
    }

    private void resetFields() {
        MIDField.clear();
        ISBNField.clear();
        statusComboBox.setValue(null);
        bookContainer.setVisible(false);
        memberContainer.setVisible(false);
    }

    private void showBookNotFound() {
        bookTitle.setText("Not Found");
        bookAuthor.setText("Not Found");
        bookAvailable.setText("Not Found");
        bookContainer.setVisible(true);
    }

    private void showMemberNotFound() {
        memberName.setText("Not Found");
        memberDepartment.setText("Not Found");
        memberBorrowed.setText("Not Found");
        memberContainer.setVisible(true);
    }

    private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content != null ? content : "");
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private boolean confirmPenalty(String penaltyType, int amount) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Penalty Confirmation");
        alert.setHeaderText("Penalty for " + penaltyType);
        alert.setContentText("A penalty of " + amount + " birr is required. Do you want to continue?");
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.showAndWait();
        return alert.getResult() == yesButton;
    }

}
