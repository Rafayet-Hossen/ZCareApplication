package Controllers;

import Utility.PageUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PatientController extends PageUtil implements Initializable {
    @FXML
    private ComboBox<String> selectUserType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PageUtil.populateUserTypeComboBox(selectUserType);
        selectUserType.getSelectionModel().select("Patient"); // Set default to Patient
        selectUserType.setOnAction(event -> {
            String selected = selectUserType.getSelectionModel().getSelectedItem();
            if (!"Patient".equals(selected)) {
                PageUtil.switchPage(selectUserType);
            }
        });
    }

    public void switchPage() {
        PageUtil.switchPage(selectUserType);
    }

    public void loginAccount(ActionEvent actionEvent) { }

    public void switchForms(ActionEvent actionEvent) {

    }

    public void showPasswordRegister(ActionEvent actionEvent) { }

    public void registerAccount(ActionEvent actionEvent) { }

    public void showPasswordLogin(ActionEvent actionEvent) { }
}