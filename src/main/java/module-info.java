module com.poli.lms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;


    opens com.poli.lms to javafx.fxml;
    exports com.poli.lms;
}