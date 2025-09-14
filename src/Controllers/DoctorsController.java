package Controllers;

import Database.Database;
import Utility.AlertUtil;
import Utility.PageUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ComboBox;


import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DoctorsController extends PageUtil implements Initializable {

    @FXML
    private TextField doctorEmail;

    @FXML
    private TextField doctorFullName;

    @FXML
    private TextField doctorID;

    @FXML
    private Button doctorLoginBtn;

    @FXML
    private CheckBox doctorLoginCheckBox;

    @FXML
    private PasswordField doctorLoginPassword;

    @FXML
    private TextField doctorRegisterID;

    @FXML
    private PasswordField doctorRegisterPassword;

    @FXML
    private AnchorPane loginForm;

    @FXML
    private TextField loginShowPassword;

    @FXML
    private AnchorPane mainForm;

    @FXML
    private Button registerBtn;

    @FXML
    private CheckBox registerCheckbox;

    @FXML
    private AnchorPane registerForm;

    @FXML
    private Hyperlink registerHere;

    @FXML
    private Hyperlink registerLogin;

    @FXML
    private TextField registerShowPassword;

    @FXML
    private ComboBox<String> selectUserType;



    private Connection connect;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public void registerAccount() {
        if (doctorEmail.getText().isEmpty() || doctorFullName.getText().isEmpty() || doctorRegisterPassword.getText().isEmpty()) {
            AlertUtil.showError("Please fill all the fields");
            return;
        }
        if (doctorRegisterPassword.getText().length() < 8) {
            AlertUtil.showError("Password must be at least 8 characters");
            return;
        }

        String checkEmail = "SELECT * FROM doctors WHERE email = ?";
        String insertUser = "INSERT INTO doctors (full_name, email,doctor_id,password,created_at) VALUES (?, ?, ?, ?,?)";

        try {
            connect = Database.connectDB();
            if (connect == null) {
                AlertUtil.showError("Database connection failed!");
                return;
            }
            preparedStatement = connect.prepareStatement(checkEmail);
            preparedStatement.setString(1, doctorEmail.getText());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                AlertUtil.showError("Email already exists!");
                return;
            }
            preparedStatement = connect.prepareStatement(insertUser);
            preparedStatement.setString(1, doctorFullName.getText());
            preparedStatement.setString(2, doctorEmail.getText());
            preparedStatement.setString(3, doctorRegisterID.getText());
            preparedStatement.setString(4, doctorRegisterPassword.getText());
            preparedStatement.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                AlertUtil.showSuccess("Account created successfully!");
                switchForms();
            } else {
                AlertUtil.showError("Failed to create account.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Database error: " + e.getMessage());
        } finally {
            closeConnections();
        }
        doctorFullName.clear();
        doctorRegisterPassword.clear();
        registerShowPassword.clear();
        doctorEmail.clear();
    }

    public void loginAccount() {
        if (doctorID.getText().isEmpty() || doctorLoginPassword.getText().isEmpty()) {
            AlertUtil.showError("Please enter Doctor ID and Password");
            return;
        }

        String query = "SELECT * FROM doctors WHERE doctor_id = ? AND password = ?";

        try {
            connect = Database.connectDB();
            if (connect == null) {
                AlertUtil.showError("Database connection failed!");
                return;
            }

            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, doctorID.getText());
            preparedStatement.setString(2, doctorLoginPassword.getText());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                AlertUtil.showSuccess("Login successful!");
                mainForm.setVisible(true);
                loginForm.setVisible(false);
            } else {
                AlertUtil.showError("Invalid Doctor ID or Password.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Database error: " + e.getMessage());
        } finally {
            closeConnections();
        }

        doctorID.clear();
        doctorLoginPassword.clear();
        loginShowPassword.clear();
    }

    private void closeConnections() {
    }


    @FXML
    private void toggleLoginShowPassword() {
        if (doctorLoginCheckBox.isSelected()) {
            loginShowPassword.setText(doctorLoginPassword.getText());
            loginShowPassword.setVisible(true);
            doctorLoginPassword.setVisible(false);
        } else {
            doctorLoginPassword.setText(loginShowPassword.getText());
            doctorLoginPassword.setVisible(true);
            loginShowPassword.setVisible(false);
        }
    }

    @FXML
    private void toggleRegisterShowPassword() {
        if (registerCheckbox.isSelected()) {
            registerShowPassword.setText(doctorRegisterPassword.getText());
            registerShowPassword.setVisible(true);
            doctorRegisterPassword.setVisible(false);
        } else {
            doctorRegisterPassword.setText(registerShowPassword.getText());
            doctorRegisterPassword.setVisible(true);
            registerShowPassword.setVisible(false);
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PageUtil.populateUserTypeComboBox(selectUserType);
        selectUserType.getSelectionModel().select("Doctor"); // Set default to Doctor
        selectUserType.setOnAction(event -> {
            String selected = selectUserType.getSelectionModel().getSelectedItem();
            if (!"Doctor".equals(selected)) {
                PageUtil.switchPage(selectUserType);
            }
        });
    }

    public void switchPage() {
        PageUtil.switchPage(selectUserType);
    }
    public void switchForms(){
        if (loginForm.isVisible()) {
            loginForm.setVisible(false);
            registerForm.setVisible(true);
        } else {
            registerForm.setVisible(false);
            loginForm.setVisible(true);
        }
    }
}