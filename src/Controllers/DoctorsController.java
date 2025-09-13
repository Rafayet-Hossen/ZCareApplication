package Controllers;

import Database.Database;
import Utility.PageUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DoctorsController extends PageUtil implements Initializable {
    @FXML
    private TextField doctorID;
    @FXML
    private Button doctorLoginBtn;
    @FXML
    private CheckBox doctorLoginCheckBox;
    @FXML
    private PasswordField doctorPassword;
    @FXML
    private AnchorPane loginForm;
    @FXML
    private TextField loginShowPassword;
    @FXML
    private ComboBox<String> selectUserType;
    @FXML
    private AnchorPane mainForm;
    @FXML
    private Button registerBtn;
    @FXML
    private CheckBox registerCheckbox;
    @FXML
    private TextField registerEmail;
    @FXML
    private AnchorPane registerForm;
    @FXML
    private Hyperlink registerHere;
    @FXML
    private Hyperlink registerLogin;
    @FXML
    private PasswordField registerPassword;
    @FXML
    private TextField registerShowPassword;
    @FXML
    private TextField registerUsername;

    private Connection connect;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @FXML
    private void handleRegister() {
        if (registerEmail.getText().isEmpty() || registerUsername.getText().isEmpty() || registerPassword.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all the fields");
            return;
        }
        if (registerPassword.getText().length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Password must be 8 characters");
            return;
        }

        String checkEmail = "SELECT * FROM doctors WHERE email = ?";
        String insertUser = "INSERT INTO doctors (username, password, email, date) VALUES (?, ?, ?, ?)";

        try {
            connect = Database.connectDB();
            assert connect != null;

            preparedStatement = connect.prepareStatement(checkEmail);
            preparedStatement.setString(1, registerEmail.getText());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                showAlert(Alert.AlertType.ERROR, "Email already exists!");
                return;
            }
            resultSet.close();
            preparedStatement.close();

            preparedStatement = connect.prepareStatement(insertUser);
            preparedStatement.setString(1, registerUsername.getText());
            preparedStatement.setString(2, registerPassword.getText());
            preparedStatement.setString(3, registerEmail.getText());
            preparedStatement.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Account created successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to create account.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connect != null) connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loginForm.setVisible(true);
        registerForm.setVisible(false);
        registerUsername.clear();
        registerPassword.clear();
        registerShowPassword.clear();
        registerEmail.clear();
    }

    @FXML
    private void loginAccount() {
        String email = doctorID.getText();
        String password = doctorPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter both email and password.");
            return;
        }

        String query = "SELECT * FROM doctors WHERE email = ? AND password = ?";

        try {
            connect = Database.connectDB();
            assert connect != null;

            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                mainForm.setVisible(true);
                loginForm.setVisible(false);
                doctorID.clear();
                doctorPassword.clear();
                loginShowPassword.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid email or password.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connect != null) connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void toggleLoginShowPassword() {
        if (doctorLoginCheckBox.isSelected()) {
            loginShowPassword.setText(doctorPassword.getText());
            loginShowPassword.setVisible(true);
            doctorPassword.setVisible(false);
        } else {
            doctorPassword.setText(loginShowPassword.getText());
            doctorPassword.setVisible(true);
            loginShowPassword.setVisible(false);
        }
    }

    @FXML
    private void toggleRegisterShowPassword() {
        if (registerCheckbox.isSelected()) {
            registerShowPassword.setText(registerPassword.getText());
            registerShowPassword.setVisible(true);
            registerPassword.setVisible(false);
        } else {
            registerPassword.setText(registerShowPassword.getText());
            registerPassword.setVisible(true);
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

    public void switchForms(ActionEvent event) {
    }

    public void showPasswordLogin(ActionEvent event) {
    }

    public void showPasswordRegister(ActionEvent event) {
    }

    public void registerAccount(ActionEvent actionEvent) {
    }
}