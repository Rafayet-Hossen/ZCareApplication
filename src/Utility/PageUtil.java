package Utility;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class PageUtil {
    public static void populateUserTypeComboBox(ComboBox<String> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList("Admin", "Doctor", "Patient"));
        comboBox.getSelectionModel().selectFirst();
    }

    public static void switchPage(ComboBox<String> comboBox) {
        String selectedUser = comboBox.getSelectionModel().getSelectedItem();
        Users.selectedUser = selectedUser;

        String fxmlFile;
        String title;

        switch (selectedUser) {
            case "Admin" -> {
                fxmlFile = "/FXML/AdminPortal.fxml";
                title = "Admin";
            }
            case "Doctor" -> {
                fxmlFile = "/FXML/DoctorPortal.fxml";
                title = "Doctor";
            }
            case "Patient" -> {
                fxmlFile = "/FXML/PatientPortal.fxml";
                title = "Patient";
            }
            default -> {
                AlertUtil.showError("Unknown user type selected: " + selectedUser);
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(PageUtil.class.getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) comboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Error loading " + title + " Dashboard: " + e.getMessage());
        }
    }


}