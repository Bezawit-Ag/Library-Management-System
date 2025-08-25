package com.poli.lms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PenaltyController {

    @FXML private TableView<Penalty> penaltyTable;
    @FXML private TableColumn<Penalty, String> userIdCol;
    @FXML private TableColumn<Penalty, String> bookTitleCol;
    @FXML private TableColumn<Penalty, String> reasonCol;
    @FXML private TableColumn<Penalty, Double> amountCol;

    private static final int ALLOWED_DAYS = 1;
    private static final double PENALTY_PER_DAY = 10.0;

    @FXML
    public void initialize() {
        userIdCol.setCellValueFactory(data -> data.getValue().userIdProperty());
        bookTitleCol.setCellValueFactory(data -> data.getValue().bookTitleProperty());
        reasonCol.setCellValueFactory(data -> data.getValue().reasonProperty());
        amountCol.setCellValueFactory(data -> data.getValue().amountProperty().asObject());

        penaltyTable.setItems(fetchPenalties());
    }

    private ObservableList<Penalty> fetchPenalties() {
        ObservableList<Penalty> penalties = FXCollections.observableArrayList();

        try {
            ResultSet result = Database.getConnection().createStatement().executeQuery(
                    "SELECT b.MID, bk.Title, b.IssueDate " +
                            "FROM BorrowedBooks b " +
                            "JOIN Book bk ON b.ISBN = bk.ISBN"
            );

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();

            while (result.next()) {
                String userId = result.getString("MID");
                String bookTitle = result.getString("Title");
                String issueDateStr = result.getString("IssueDate");

                Date issueDate = sdf.parse(issueDateStr);
                long diffInMillies = today.getTime() - issueDate.getTime();
                long daysLate = (diffInMillies / (1000 * 60 * 60 * 24)) - ALLOWED_DAYS;

                if (daysLate > 0) {
                    double penaltyAmount = daysLate * PENALTY_PER_DAY;
                    penalties.add(new Penalty(userId, bookTitle, "Late Return", penaltyAmount));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return penalties;
    }


    @FXML
    private void handleClose() {
        Stage stage = (Stage) penaltyTable.getScene().getWindow();
        stage.close();
    }
}
