package Controllers;

import Utility.PageUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PatientController extends PageUtil implements Initializable {
    @FXML
    private CheckBox doctorLoginCheckBox;

    @FXML
    private AnchorPane loginForm;

    @FXML
    private TextField loginShowPassword;

    @FXML
    private AnchorPane mainForm;

    @FXML
    private TextField patientContactNo;

    @FXML
    private TextField patientEmail;

    @FXML
    private TextField patientFullName;

    @FXML
    private Hyperlink patientLogin;

    @FXML
    private Button patientLoginBtn;

    @FXML
    private PasswordField patientPassword;

    @FXML
    private CheckBox patientRegisterCheckBox;

    @FXML
    private Hyperlink patientRegisterHere;

    @FXML
    private PasswordField patientRegisterPassword;

    @FXML
    private Button patientSignUp;

    @FXML
    private TextField patientUsername;

    @FXML
    private AnchorPane registerForm;

    @FXML
    private TextField registerShowPassword;

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
    public void registerAccount(ActionEvent actionEvent) {

    }
    public void loginAccount(ActionEvent actionEvent) {

    }

    public void switchForms(ActionEvent actionEvent) {
        if (loginForm.isVisible()) {
            loginForm.setVisible(false);
            registerForm.setVisible(true);
        } else {
            registerForm.setVisible(false);
            loginForm.setVisible(true);
        }
    }

    public void showPasswordRegister(ActionEvent actionEvent) {
        if(patientRegisterCheckBox.isSelected()) {
            registerShowPassword.setText(patientRegisterPassword.getText());
            registerShowPassword.setVisible(true);
            patientRegisterPassword.setVisible(false);
        } else {
            patientRegisterPassword.setText(registerShowPassword.getText());
            patientRegisterPassword.setVisible(true);
            registerShowPassword.setVisible(false);
        }
    }

    public void showPasswordLogin(ActionEvent actionEvent) {
        if(doctorLoginCheckBox.isSelected()) {
            loginShowPassword.setText(patientPassword.getText());
            loginShowPassword.setVisible(true);
            patientPassword.setVisible(false);
        } else {
            patientPassword.setText(loginShowPassword.getText());
            patientPassword.setVisible(true);
            loginShowPassword.setVisible(false);
        }
    }
}