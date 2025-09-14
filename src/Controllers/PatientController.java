package Controllers;

import Database.Database;
import Utility.AlertUtil;
import Utility.PageUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PatientController extends PageUtil implements Initializable {
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
    private CheckBox patientLoginCheckbox;

    @FXML
    private PasswordField patientLoginPassword;

    @FXML
    private TextField patientLoginUsername;

    @FXML
    private CheckBox patientRegisterCheckBox;

    @FXML
    private Hyperlink patientRegisterHere;

    @FXML
    private PasswordField patientRegisterPassword;

    @FXML
    private TextField patientRegisterShowPassword;

    @FXML
    private Button patientSignUp;

    @FXML
    private AnchorPane registerForm;

    @FXML
    private ComboBox<String> selectUserType;
    private Connection connect;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PageUtil.populateUserTypeComboBox(selectUserType);
        selectUserType.getSelectionModel().select("Patient");
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

    public void registerAccount(ActionEvent actionEvent) throws SQLException {
        if (patientFullName.getText().isEmpty() || patientEmail.getText().isEmpty() ||
                patientContactNo.getText().isEmpty() || patientRegisterPassword.getText().isEmpty()) {
            AlertUtil.showError("Please fill all the fields");
            return;
        }
        if (patientRegisterPassword.getText().length() < 8) {
            AlertUtil.showError("Password must be at least 8 characters long");
            return;
        }
        String checkEmail = "SELECT * FROM patients WHERE email = ?";
        String insertPatient = "INSERT INTO patients (full_name, email, contact_no, password) VALUES (?, ?, ?, ?)";
        try {
            connect = Database.connectDB();
            if (connect == null) {
                AlertUtil.showError("Database connection failed!");
                return;
            }
            preparedStatement = connect.prepareStatement(checkEmail);
            preparedStatement.setString(1, patientEmail.getText());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                AlertUtil.showError("Email already exists!");
                return;
            }
            preparedStatement = connect.prepareStatement(insertPatient);
            preparedStatement.setString(1, patientFullName.getText());
            preparedStatement.setString(2, patientEmail.getText());
            preparedStatement.setString(3, patientContactNo.getText());
            preparedStatement.setString(4, patientRegisterPassword.getText());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                AlertUtil.showSuccess("Account created successfully!");
                switchForms();
            } else {
                AlertUtil.showError("Account creation failed!");
            }
        } catch (SQLException e) {
            AlertUtil.showError("Database error: " + e.getMessage());
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException ignored) {}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException ignored) {}
            if (connect != null) try { connect.close(); } catch (SQLException ignored) {}
        }
    }

    public void loginAccount(ActionEvent actionEvent) {
        String email = patientLoginUsername.getText();
        String password = patientLoginPassword.isVisible() ? patientLoginPassword.getText() : loginShowPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertUtil.showError("Please enter both email and password");
            return;
        }

        String query = "SELECT * FROM patients WHERE email = ? AND password = ?";
        try {
            connect = Database.connectDB();
            if (connect == null) {
                AlertUtil.showError("Database connection failed!");
                return;
            }
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                AlertUtil.showSuccess("Login successful!");
                mainForm.setVisible(true);
                loginForm.setVisible(false);
            } else {
                AlertUtil.showError("Invalid email or password!");
            }
        } catch (SQLException e) {
            AlertUtil.showError("Database error: " + e.getMessage());
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException ignored) {}
            if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException ignored) {}
            if (connect != null) try { connect.close(); } catch (SQLException ignored) {}
        }
    }

    public void switchForms() {
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
            patientRegisterShowPassword.setText(patientRegisterPassword.getText());
            patientRegisterShowPassword.setVisible(true);
            patientRegisterPassword.setVisible(false);
        } else {
            patientRegisterPassword.setText(patientRegisterShowPassword.getText());
            patientRegisterPassword.setVisible(true);
            patientRegisterShowPassword.setVisible(false);
        }
    }

    public void showPasswordLogin(ActionEvent actionEvent) {
        if(patientLoginCheckbox.isSelected()) {
            loginShowPassword.setText(patientLoginPassword.getText());
            loginShowPassword.setVisible(true);
            patientLoginPassword.setVisible(false);
        } else {
            patientLoginPassword.setText(loginShowPassword.getText());
            patientLoginPassword.setVisible(true);
            loginShowPassword.setVisible(false);
        }
    }
}