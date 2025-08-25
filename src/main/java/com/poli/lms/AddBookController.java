package com.poli.lms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.SQLException;

public class AddBookController {
    @FXML private AnchorPane addBookContainer;
    @FXML private TextField ISBNField;
    @FXML private TextField authorField;
    @FXML private TextField availableField;
    @FXML private DatePicker publishedDateField;
    @FXML private TextField titleField;
    @FXML private Button addBtn;
    @FXML private Button backToBooksBtn;

    @FXML
    private void backToBooksBtnHandler(ActionEvent event) {
        loadPage("books");
    }

    @FXML
    private void addBookBtnHandler(ActionEvent event) {
        try {
            int isbn = Integer.parseInt(ISBNField.getText());
            String author = authorField.getText();
            String title = titleField.getText();
            String publishedDate = publishedDateField.getValue().toString();
            int available = Integer.parseInt(availableField.getText());

            if (author.isEmpty() || title.isEmpty() || publishedDate.isEmpty()) {
                showAlert("Please fill in all fields.");
                return;
            }

            Database.connect();
            Database.addBook(isbn, title, author, publishedDate, available);
            showAlert("Book added successfully!");
            loadPage("books");

        } catch (NumberFormatException e) {
            showAlert("ISBN and Available must be numbers.");
        } catch (Exception e) {
            showAlert("Error: " + e.getMessage());
        }
    }

    private void loadPage(String page) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(page + ".fxml"));
            ((BorderPane) addBookContainer.getParent()).setCenter(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Book");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
