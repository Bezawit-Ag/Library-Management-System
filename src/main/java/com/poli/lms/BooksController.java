package com.poli.lms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BooksController implements Initializable {

    @FXML private AnchorPane booksContainer;
    @FXML private TextField searchField;

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> ISBNCol;
    @FXML private TableColumn<Book, String> titleCol;
    @FXML private TableColumn<Book, String> authorCol;
    @FXML private TableColumn<Book, String> publishedDateCol;
    @FXML private TableColumn<Book, Integer> availableCol;
    @FXML private TableColumn<Book, Boolean> donatedCol;
    @FXML private TableColumn<Book, String> donorNameCol;

    @FXML private TableView<BorrowedBook> borrowedBooksTable;
    @FXML private TableColumn<BorrowedBook, Integer> borrowedBookISBNCol;
    @FXML private TableColumn<BorrowedBook, String> borrowedBookTitleCol;
    @FXML private TableColumn<BorrowedBook, Integer> borrowerMIDCol;
    @FXML private TableColumn<BorrowedBook, String> borrowerIssueDateCol;
    @FXML private TableColumn<BorrowedBook, String> borrowerNameCol;

    private final ObservableList<Book> masterBookList = FXCollections.observableArrayList();
    private final ObservableList<BorrowedBook> masterBorrowedBookList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        connectAndLoadData();
        setupSearch();
    }

    private void setupTableColumns() {
        ISBNCol.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publishedDateCol.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        availableCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        donatedCol.setCellValueFactory(new PropertyValueFactory<>("donated"));
        donorNameCol.setCellValueFactory(new PropertyValueFactory<>("donorName"));

        borrowedBookISBNCol.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        borrowedBookTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        borrowerMIDCol.setCellValueFactory(new PropertyValueFactory<>("mid"));
        borrowerIssueDateCol.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        borrowerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void connectAndLoadData() {
        try {
            Database.connect();
            loadBooks();
            loadBorrowedBooks();
        } catch (ClassNotFoundException | SQLException e) {
            showError("Database Connection Error", "Failed to connect to database", e.getMessage());
        }
    }

    private void loadBooks() {
        masterBookList.clear();
        try {
            ResultSet resultSet = Database.getBooks();
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("ISBN"),
                        resultSet.getString("Title"),
                        resultSet.getString("Author"),
                        resultSet.getString("PublishedDate"),
                        resultSet.getInt("Available"),
                        resultSet.getBoolean("Donated"),
                        resultSet.getString("DonorName")
                );
                masterBookList.add(book);
            }
            booksTable.setItems(masterBookList);
        } catch (SQLException | ClassNotFoundException e) {
            showError("Database Error", "Failed to load books", e.getMessage());
        }
    }

    private void loadBorrowedBooks() {
        masterBorrowedBookList.clear();
        try {
            ResultSet resultSet = Database.getBorrowedBooks();
            while (resultSet.next()) {
                BorrowedBook borrowedBook = new BorrowedBook(
                        resultSet.getInt("ISBN"),
                        resultSet.getString("IssueDate"),
                        resultSet.getInt("MID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Title")
                );
                masterBorrowedBookList.add(borrowedBook);
            }
            borrowedBooksTable.setItems(masterBorrowedBookList);
        } catch (SQLException | ClassNotFoundException e) {
            showError("Database Error", "Failed to load borrowed books", e.getMessage());
        }
    }

    private void setupSearch() {
        FilteredList<Book> filteredBooks = new FilteredList<>(masterBookList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = newValue.toLowerCase();

            filteredBooks.setPredicate(book -> {
                if (lowerCaseFilter.isEmpty()) return true;

                return String.valueOf(book.getISBN()).contains(lowerCaseFilter) ||
                        book.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseFilter);
            });
        });

        booksTable.setItems(filteredBooks);
    }

    @FXML
    private void deleteBookHandler() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showSelectTableRowError();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Book");
        confirm.setContentText("Are you sure you want to delete: \"" + selectedBook.getTitle() + "\"?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Database.removeBook(selectedBook.getISBN());

                    // FULL reload from database after delete
                    loadBooks();

                    showInfo("Book Deleted", "Successfully deleted the book.");
                } catch (SQLException | ClassNotFoundException e) {
                    showError("Deletion Error", "Failed to delete book", e.getMessage());
                }
            }
        });
    }


    @FXML
    private void addBookBtnHandler(ActionEvent event) {
        loadPage("addBook");
    }

    @FXML
    private void editBtnHandler(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showSelectTableRowError();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poli/lms/editBook.fxml"));
            Parent root = loader.load();
            EditBookController controller = loader.getController();
            controller.initializeField(selectedBook);
            ((BorderPane) booksContainer.getParent()).setCenter(root);
        } catch (IOException e) {
            showError("Page Load Error", "Failed to load Edit Book page", e.getMessage());
        }
    }

    private void loadPage(String pageName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poli/lms/" + pageName + ".fxml"));
            Parent root = loader.load();
            ((BorderPane) booksContainer.getParent()).setCenter(root);
        } catch (IOException e) {
            showError("Page Load Error", "Failed to load page: " + pageName, e.getMessage());
        }
    }

    private void showSelectTableRowError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Selection Error");
        alert.setHeaderText("No Selection");
        alert.setContentText("Please select a book from the table first.");
        alert.showAndWait();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
