package com.poli.lms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditMemberController implements Initializable {

    @FXML
    private Button addMemberBtn;

    @FXML
    private AnchorPane editMemberContainer;

    @FXML
    private TextField departmentField;

    @FXML
    private ChoiceBox<String> genderChoiceBox;

    @FXML
    private TextField memberIdField;

    @FXML
    private TextField nameField;

    private final String[] gender = {"Male", "Female"};
    private int mid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderChoiceBox.getItems().addAll(gender);
    }

    public void initializeFields(Member member) {
        mid = member.getMid();
        nameField.setText(member.getName());
        departmentField.setText(member.getDepartment());
        genderChoiceBox.setValue(member.getGender());
    }

    @FXML
    void backToMembersHandler(ActionEvent event) {
        loadPage("members");
    }

    public void loadPage(String page) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(page + ".fxml"));
            ((BorderPane) editMemberContainer.getParent()).setCenter(root);
        } catch (IOException e) {
            showError("Page Load Error", "Could not load page: " + page, e.getMessage());
        }
    }

    @FXML
    void editMemberBtnHandler(ActionEvent event) {
        try {
            Database.connect();
            if (nameField.getText().isEmpty() || departmentField.getText().isEmpty() || genderChoiceBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required.");
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Missing Information");
                alert.showAndWait();
                return;
            }

            Database.editMember(mid, nameField.getText(), genderChoiceBox.getValue(), departmentField.getText());
            loadPage("members");
        } catch (ClassNotFoundException | SQLException e) {
            showError("Database Error", "Error editing member.", e.getMessage());
        } finally {
            Database.closeConnection();
        }
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
