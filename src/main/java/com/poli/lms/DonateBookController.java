package com.poli.lms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.ResultSet;

public class DonateBookController {

    @FXML private TextField isbnField;
    @FXML private TextField titleField;
    @FXML private TextField donorNameField;
    @FXML private TextField numBooksField;
    @FXML private TableView<Book> donatedBooksTable;
    @FXML private TableColumn<Book, Integer> isbnCol;
    @FXML private TableColumn<Book, String> titleCol;
    @FXML private TableColumn<Book, String> donorCol;
    @FXML private TableColumn<Book, Integer> quantityCol;

    private final ObservableList<Book> donatedBooks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Use lambdas for ISBN because JavaFX can't find getISBN()
        isbnCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getISBN()).asObject());

        // These use standard JavaFX naming
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        donorCol.setCellValueFactory(new PropertyValueFactory<>("donorName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        loadDonatedBooks();
    }

    private void loadDonatedBooks() {
        donatedBooks.clear();
        try {
            ResultSet rs = Database.getDonatedBooks();
            while (rs.next()) {
                donatedBooks.add(new Book(
                        rs.getInt("ISBN"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("PublishedDate"),
                        rs.getInt("Available"),
                        rs.getBoolean("Donated"),
                        rs.getString("DonorName")
                ));
            }
            donatedBooksTable.setItems(donatedBooks);
        } catch (Exception e) {
            showAlert("Load Error", "Unable to load donated books: " + e.getMessage());
        }
    }

    @FXML
    private void submitDonation() {
        try {
            int isbn = Integer.parseInt(isbnField.getText());
            String title = titleField.getText();
            String donor = donorNameField.getText();
            int numBooks = Integer.parseInt(numBooksField.getText());

            Database.addBook(isbn, title, "Unknown", "2025-01-01", numBooks, true, donor);

            showAlert("Donation Successful", "The book has been added successfully!");
            clearForm();
            loadDonatedBooks(); // Refresh the table
        } catch (Exception e) {
            showAlert("Error", "Invalid input or database error: " + e.getMessage());
        }
    }

    private void clearForm() {
        isbnField.clear();
        titleField.clear();
        donorNameField.clear();
        numBooksField.clear();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
