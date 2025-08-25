package com.poli.lms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MembersController implements Initializable {
    @FXML private AnchorPane membersContainer;
    @FXML private TableColumn<Member, Integer> MIDCol;
    @FXML private TableColumn<Member, String> departmentCol;
    @FXML private TableColumn<Member, String> genderCol;
    @FXML private TableView<Member> membersTable;
    @FXML private TableColumn<Member, String> nameCol;
    @FXML private TableColumn<Member, Integer> numberBorrowedCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MIDCol.setCellValueFactory(new PropertyValueFactory<>("mid"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        numberBorrowedCol.setCellValueFactory(new PropertyValueFactory<>("numberBorrowed"));

        try {
            Database.connect();
            displayMembers(Database.getConnection());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void deleteMemberBtnHandler(ActionEvent event) {
        Member selected = membersTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showSelectTableRowError();
            return;
        }

        // Only ONE confirmation dialog with default OK/Cancel buttons
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Member");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        // Use default ButtonType.OK and ButtonType.CANCEL
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                Database.removeMember(selected.getMid());
                displayMembers(Database.getConnection());

                // Show deletion success
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Deleted");
                success.setHeaderText(null);
                success.setContentText("Member deleted successfully.");
                success.showAndWait();
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Delete Error");
                error.setHeaderText("Could not delete member");
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        }
    }


    @FXML
    void editMemberBtnHandler(ActionEvent event) {
        Member memberSelected = membersTable.getSelectionModel().getSelectedItem();

        if (memberSelected == null) {
            showSelectTableRowError();
            return;
        }

        Parent root;
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("editMember.fxml"));
        try {
            root = fxml.load();
            EditMemberController editMemberController = fxml.getController();
            editMemberController.initializeFields(memberSelected);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ((BorderPane) (membersContainer.getParent())).setCenter(root);
    }

    public void displayMembers(Connection conn) throws SQLException {
        membersTable.getItems().clear();

        String query = "SELECT * FROM Member ORDER BY MID";
        try (Statement stat = conn.createStatement();
             ResultSet result = stat.executeQuery(query)) {

            while (result.next()) {
                int mid = result.getInt("MID");
                String name = result.getString("Name");
                String gender = result.getString("Gender");
                String department = result.getString("Department");
                int numberBorrowed = result.getInt("NumberBorrowed");

                Member member = new Member(mid, name, gender, department, numberBorrowed);
                membersTable.getItems().add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving members", e);
        }
    }

    @FXML
    private void addMemberBtnHandler(ActionEvent event) {
        loadPage("addMember");
    }

    public void loadPage(String page) {
        Parent root;

        FXMLLoader fxml = new FXMLLoader(getClass().getResource(page + ".fxml"));
        try {
            root = fxml.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ((BorderPane) (membersContainer.getParent())).setCenter(root);
    }

    public void showSelectTableRowError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Member Error Dialog");
        alert.setHeaderText("First Select The Table Row");
        alert.setContentText("You need to select a member you need to edit or delete.");
        alert.showAndWait();
    }
}
