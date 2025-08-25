package com.poli.lms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.util.ResourceBundle;

public class AddMemberController implements Initializable {
    @FXML private AnchorPane addMemberContainer;
    @FXML private ChoiceBox<String> genderChoiceBox;

    @FXML
    private TextField departmentField;

    @FXML
    private TextField memberIdField;

    @FXML
    private TextField nameField;

    private final String[] gender = {"Male", "Female"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderChoiceBox.getItems().addAll(gender);
    }

    @FXML
    private void backToMembersHandler(ActionEvent event){
        loadPage("members");
    }

    @FXML
    private void addMemberBtnHandler(ActionEvent event){
        int memberId = Integer.parseInt(memberIdField.getText());
        String department = departmentField.getText();
        String name = nameField.getText();
        String gender = genderChoiceBox.getValue();

        if(memberIdField.getText().isEmpty() || department.isEmpty() || name.isEmpty() || gender.isEmpty()) return;

        try {
            Database.connect();
            Database.addMember(memberId, name, gender, department, 0);

            loadPage("members");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }


    public void loadPage(String page){
        System.out.println("Page found");
        Parent root;

        FXMLLoader fxml = new FXMLLoader(getClass().getResource(page+".fxml"));
        try {
            root = fxml.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ((BorderPane) (addMemberContainer.getParent())).setCenter(root);
    }
}
