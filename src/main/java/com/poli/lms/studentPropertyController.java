package com.poli.lms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.sql.*;

public class studentPropertyController {

    @FXML private TextField midField;
    @FXML private TextField deviceTypeField;
    @FXML private TextField deviceModelField;
    @FXML private TextField serialNumberField;
    @FXML private DatePicker entryDateField;

    @FXML private TableView<studentProperty> propertyTable;
    @FXML private TableColumn<studentProperty, Integer> midColumn;
    @FXML private TableColumn<studentProperty, String> deviceTypeColumn;
    @FXML private TableColumn<studentProperty, String> deviceModelColumn;
    @FXML private TableColumn<studentProperty, String> serialNumberColumn;
    @FXML private TableColumn<studentProperty, LocalDate> entryDateColumn;

    private final ObservableList<studentProperty> propertyList = FXCollections.observableArrayList();

    private studentProperty selectedProperty = null;

    @FXML
    public void initialize() {
        midColumn.setCellValueFactory(new PropertyValueFactory<>("mid"));
        deviceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        deviceModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serial"));
        entryDateColumn.setCellValueFactory(new PropertyValueFactory<>("entryDate"));

        propertyTable.setItems(propertyList);
        loadExistingProperties();

        propertyTable.setOnMouseClicked(e -> {
            selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
            if (selectedProperty != null) {
                midField.setText(String.valueOf(selectedProperty.getMid()));
                deviceTypeField.setText(selectedProperty.getDeviceType());
                deviceModelField.setText(selectedProperty.getModel());
                serialNumberField.setText(selectedProperty.getSerial());
                entryDateField.setValue(selectedProperty.getEntryDate());
            }
        });
    }

    private void loadExistingProperties() {
        propertyList.clear();
        try {
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT MID, DeviceType, Model, SerialNumber, EntryDate FROM studentproperty";
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                int mid = result.getInt("MID");
                String deviceType = result.getString("DeviceType");
                String deviceModel = result.getString("Model");
                String serial = result.getString("SerialNumber");
                LocalDate entryDate = result.getDate("EntryDate").toLocalDate();

                propertyList.add(new studentProperty(mid, deviceType, deviceModel, serial, entryDate));
            }

            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterEntry() {
        try {
            int mid = Integer.parseInt(midField.getText());
            String deviceType = deviceTypeField.getText();
            String model = deviceModelField.getText();
            String serial = serialNumberField.getText();
            LocalDate date = entryDateField.getValue();

            if (deviceType.isEmpty() || model.isEmpty() || serial.isEmpty() || date == null) {
                showAlert("Please fill all fields!");
                return;
            }

            Database.registerStudentProperty(mid, deviceType, model, serial, date.toString());
            propertyList.add(new studentProperty(mid, deviceType, model, serial, date));
            showAlert("Device registered successfully.");
            clearForm();
        } catch (Exception e) {
            showAlert("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateEntry() {
        if (selectedProperty == null) {
            showAlert("Select a record to update.");
            return;
        }

        try {
            int mid = Integer.parseInt(midField.getText());
            String deviceType = deviceTypeField.getText();
            String model = deviceModelField.getText();
            String serial = serialNumberField.getText();
            LocalDate date = entryDateField.getValue();

            if (deviceType.isEmpty() || model.isEmpty() || serial.isEmpty() || date == null) {
                showAlert("Please fill all fields!");
                return;
            }

            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE studentproperty SET DeviceType=?, Model=?, SerialNumber=?, EntryDate=? WHERE MID=?");
            stmt.setString(1, deviceType);
            stmt.setString(2, model);
            stmt.setString(3, serial);
            stmt.setDate(4, Date.valueOf(date));
            stmt.setInt(5, mid);
            stmt.executeUpdate();
            conn.close();

            loadExistingProperties();
            showAlert("Entry updated.");
            clearForm();

        } catch (Exception e) {
            showAlert("Update error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteEntry() {
        if (selectedProperty == null) {
            showAlert("Select a record to delete.");
            return;
        }

        try {
            int mid = selectedProperty.getMid();
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM studentproperty WHERE MID=?");
            stmt.setInt(1, mid);
            stmt.executeUpdate();
            conn.close();

            propertyList.remove(selectedProperty);
            showAlert("Entry deleted.");
            clearForm();

        } catch (Exception e) {
            showAlert("Delete error: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Property");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearForm() {
        midField.clear();
        deviceTypeField.clear();
        deviceModelField.clear();
        serialNumberField.clear();
        entryDateField.setValue(null);
        selectedProperty = null;
        propertyTable.getSelectionModel().clearSelection();
    }
}
