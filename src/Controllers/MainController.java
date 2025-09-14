package Controllers;

import Database.Database;
import Utility.AlertUtil;
import Utility.PageUtil;
import Utility.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class MainController extends PageUtil implements Initializable {

    @FXML private Button loginBtn;
    @FXML private CheckBox loginCheckbox;
    @FXML private AnchorPane loginForm;
    @FXML private TextField loginPassword;
    @FXML private TextField loginShowPassword;
    @FXML private ComboBox<String> selectUserType;
    @FXML private TextField loginUsername;
    @FXML private AnchorPane mainForm;
    @FXML private Button registerBtn;
    @FXML private CheckBox registerCheckbox;
    @FXML private AnchorPane registerForm;
    @FXML private Hyperlink registerHere;
    @FXML private Hyperlink registerLogin;
    @FXML private TextField registerUsername;
    @FXML private TextField registerShowPassword;
    @FXML private PasswordField registerPassword;
    @FXML private TextField registerEmail;

    private Connection connect;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            if (selectUserType != null) {
                PageUtil.populateUserTypeComboBox(selectUserType);
                if (Users.selectedUser != null) {
                    selectUserType.getSelectionModel().select(Users.selectedUser);
                } else {
                    selectUserType.getSelectionModel().selectFirst(); // default to Admin first time
                }

                selectUserType.setOnAction(event -> switchPage());
            } else {
                System.err.println("⚠️ selectUserType is null! Check fx:id in AdminPortal.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userList() {
        try {
            if (selectUserType == null) {
                System.err.println("⚠️ selectUserType is null! Check fx:id in FXML.");
                return;
            }

            if (Users.user == null) {
                System.err.println("⚠️ Users.user is null! Nothing to display.");
                return;
            }

            ObservableList<String> observableList = FXCollections.observableArrayList(Users.user);
            selectUserType.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Failed to load user list: " + e.getMessage());
        }
    }
    public void registerAccount() {
        if (registerEmail.getText().isEmpty() || registerUsername.getText().isEmpty() || registerPassword.getText().isEmpty()) {
            AlertUtil.showError("Please fill all the fields");
            return;
        }
        if (registerPassword.getText().length() < 8) {
            AlertUtil.showError("Password must be at least 8 characters");
            return;
        }

        String checkUsername = "SELECT * FROM admin WHERE username = ?";
        String checkEmail = "SELECT * FROM admin WHERE email = ?";
        String insertUser = "INSERT INTO admin (email, username, password, created_at) VALUES (?, ?, ?, ?)";

        try {
            connect = Database.connectDB();
            if (connect == null) {
                AlertUtil.showError("Database connection failed!");
                return;
            }
            preparedStatement = connect.prepareStatement(checkUsername);
            preparedStatement.setString(1, registerUsername.getText());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                AlertUtil.showError("Username already exists!");
                return;
            }
            preparedStatement = connect.prepareStatement(checkEmail);
            preparedStatement.setString(1, registerEmail.getText());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                AlertUtil.showError("Email already exists!");
                return;
            }
            preparedStatement = connect.prepareStatement(insertUser);
            preparedStatement.setString(2, registerUsername.getText());
            preparedStatement.setString(3, registerPassword.getText());
            preparedStatement.setString(1, registerEmail.getText());
            preparedStatement.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                AlertUtil.showSuccess("Account created successfully!");
            } else {
                AlertUtil.showError("Failed to create account.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Database error: " + e.getMessage());
        } finally {
            closeConnections();
        }
        loginForm.setVisible(true);
        registerForm.setVisible(false);
        registerUsername.clear();
        registerPassword.clear();
        registerShowPassword.clear();
        registerEmail.clear();
    }

    public void loginAccount() {
        if (loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty()) {
            AlertUtil.showError("Please fill all the fields");
            return;
        }

        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try {
            connect = Database.connectDB();
            if (connect == null) {
                AlertUtil.showError("Database connection failed!");
                return;
            }

            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, loginUsername.getText());
            preparedStatement.setString(2, loginPassword.getText());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                AlertUtil.showSuccess("Login successful!");
                mainForm.setVisible(true);
                loginForm.setVisible(false);
            } else {
                AlertUtil.showError("Invalid username or password.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Database error: " + e.getMessage());
        } finally {
            closeConnections();
        }

        loginUsername.clear();
        loginPassword.clear();
    }

    public void showPasswordLogin() {
        if (loginCheckbox.isSelected()) {
            loginShowPassword.setText(loginPassword.getText());
            loginShowPassword.setVisible(true);
            loginPassword.setVisible(false);
        } else {
            loginPassword.setText(loginShowPassword.getText());
            loginShowPassword.setVisible(false);
            loginPassword.setVisible(true);
        }
    }

    public void showPasswordRegister() {
        if (registerCheckbox.isSelected()) {
            registerShowPassword.setText(registerPassword.getText());
            registerShowPassword.setVisible(true);
            registerPassword.setVisible(false);
        } else {
            registerPassword.setText(registerShowPassword.getText());
            registerShowPassword.setVisible(false);
            registerPassword.setVisible(true);
        }
    }

    public void switchPage() {
        PageUtil.switchPage(selectUserType);
    }

    public void switchForms(ActionEvent event) {
        if (event.getSource() == registerHere) {
            loginForm.setVisible(false);
            registerForm.setVisible(true);
        } else if (event.getSource() == registerLogin) {
            registerForm.setVisible(false);
            loginForm.setVisible(true);
        }
    }

    private void closeConnections() {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connect != null) connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
