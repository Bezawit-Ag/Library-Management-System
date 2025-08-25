package com.poli.lms;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    @FXML private BorderPane mainPage;

    @FXML private Button booksBtn;
    @FXML private Button membersBtn;
    @FXML private Button issueBookBtn;
    @FXML private Button returnBookBtn;

    @FXML private Button donateBookBtn;
    @FXML private Button penaltyBtn;
    @FXML private Button studentPropertyBtn;

    @FXML
    private void booksBtnHandler(ActionEvent event){
        loadPage("books");
        addIndicator(booksBtn);
    }

    @FXML
    private void membersBtnHandler(ActionEvent event){
        loadPage("members");
        addIndicator(membersBtn);
    }

    @FXML
    private void issueBookBtnHandler(ActionEvent event){
        loadPage("issueBook");
        addIndicator(issueBookBtn);
    }

    @FXML
    private void returnBookBtnHandler(ActionEvent event){
        loadPage("returnBook");
        addIndicator(returnBookBtn);
    }



    @FXML
    private void donateBookBtnHandler(ActionEvent event){
        loadPage("donatebook");
        addIndicator(donateBookBtn);
    }
    @FXML
    private void penaltyBtnHandler(ActionEvent event){
        loadPage("penalty");
        addIndicator(penaltyBtn);
    }

    @FXML
    private void studentPropertyBtn(ActionEvent event){
        loadPage("studentProperty");
        addIndicator(studentPropertyBtn);
    }

    public void addIndicator(Button btn){
        booksBtn.setId("");
        membersBtn.setId("");
        issueBookBtn.setId("");
        returnBookBtn.setId("");

        donateBookBtn.setId("");
        studentPropertyBtn.setId("");
        btn.setId("btnActive");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPage("books");
        addIndicator(booksBtn);
    }

    public void loadPage(String page){
        Parent root;
        FXMLLoader fxml = new FXMLLoader(getClass().getResource(page + ".fxml"));

        try {
            root = fxml.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mainPage.setCenter(root);
    }
}
