package com.poli.lms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditBookController {

    @FXML
    private AnchorPane addBookContainer;

    @FXML
    private Button addBtn;

    @FXML
    private TextField authorField;

    @FXML
    private TextField availableField;

    @FXML
    private Button backToBooksBtn;

    @FXML
    private DatePicker publishedDateField;

    @FXML
    private TextField titleField;

    @FXML
    private CheckBox donatedCheckBox;

    @FXML
    private TextField donorNameField;

    private int isbn;

    public void initialize() {
        // You can also set initial states here if needed
        donatedCheckBox.setOnAction(event -> toggleDonorField());
        toggleDonorField();
    }

    private void toggleDonorField() {
        donorNameField.setDisable(!donatedCheckBox.isSelected());
        if (!donatedCheckBox.isSelected()) {
            donorNameField.clear();
        }
    }

    @FXML
    void editBookBtnHandler(ActionEvent event) {
        try {
            Database.connect();

            // Validate fields
            if (titleField.getText().isEmpty() || authorField.getText().isEmpty() ||
                    publishedDateField.getValue() == null || availableField.getText().isEmpty()) {

                showAlert(Alert.AlertType.ERROR, "Invalid Data", "All fields must be filled properly.");
                return;
            }

            int available;
            try {
                available = Integer.parseInt(availableField.getText());
                if (available < 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Data", "Available copies cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Number", "Available field must be a valid number.");
                return;
            }

            boolean donated = donatedCheckBox.isSelected();
            String donorName = donated ? donorNameField.getText() : "";

            if (donated && donorName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Missing Donor Name", "Please enter the donor's name.");
                return;
            }

            // Update the book
            Database.editBook(isbn, titleField.getText(), authorField.getText(),
                    publishedDateField.getValue().toString(), available, donated, donorName);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");

            loadPage("books");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }
    }

    @FXML
    void backToBooksBtnHandler(ActionEvent event) {
        loadPage("books");
    }

    public void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page + ".fxml"));
            Parent root = loader.load();
            ((BorderPane) addBookContainer.getParent()).setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the page.");
        }
    }

    public void initializeField(Book book) {
        if (book == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No book selected to edit.");
            return;
        }

        isbn = book.getISBN();
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        publishedDateField.setValue(LocalDate.parse(book.getPublishedDate()));
        availableField.setText(String.valueOf(book.getAvailable()));

        donatedCheckBox.setSelected(book.isDonated());
        donorNameField.setText(book.isDonated() ? book.getDonorName() : "");
        toggleDonorField(); // Update donor field status
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
