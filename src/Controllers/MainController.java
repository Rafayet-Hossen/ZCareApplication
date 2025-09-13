package Controllers;

import Database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button loginBtn;

    @FXML
    private CheckBox loginCheckbox;

    @FXML
    private AnchorPane loginForm;

    @FXML
    private TextField loginPassword;

    @FXML
    private TextField loginShowPassword;

    @FXML
    private ComboBox<String> selectUserType;

    @FXML
    private TextField loginUsername;

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
    private TextField registerUsername;
    @FXML
    private TextField registerShowPassword;
    @FXML
    private PasswordField registerPassword;
    @FXML
    private TextField registerEmail;

    private Connection connect;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public void registerAccount() {
        if (registerEmail.getText().isEmpty() || registerUsername.getText().isEmpty() || registerPassword.getText().isEmpty()) {
            AlertUtil.showError("Please fill all the fields");
            return;
        }
        if (registerPassword.getText().length() < 8) {
            AlertUtil.showError("Password must be 8 characters");
            return;
        }

        String checkUsername = "SELECT * FROM admin WHERE username = ?";
        String checkEmail = "SELECT * FROM admin WHERE email = ?";
        String insertUser = "INSERT INTO admin (username, password, email, date) VALUES (?, ?, ?, ?)";

        try {
            connect = Database.connectDB();
            assert connect != null;

            preparedStatement = connect.prepareStatement(checkUsername);
            preparedStatement.setString(1, registerUsername.getText());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                AlertUtil.showError("Username already exists!");
                return;
            }
            resultSet.close();
            preparedStatement.close();

            preparedStatement = connect.prepareStatement(checkEmail);
            preparedStatement.setString(1, registerEmail.getText());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                AlertUtil.showError("Email already exists!");
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
                AlertUtil.showSuccess("Account created successfully!");
            } else {
                AlertUtil.showError("Failed to create account.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Database error: " + e.getMessage());
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

    public void loginAccount() {
        if (loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty()) {
            AlertUtil.showError("Please fill all the fields");
            return;
        }

        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try {
            connect = Database.connectDB();
            assert connect != null;

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
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connect != null) connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loginUsername.clear();
        loginPassword.clear();
    }

    public void showPasswordLogin(){
        if(loginCheckbox.isSelected()){
            loginShowPassword.setText(loginPassword.getText());
            loginShowPassword.setVisible(true);
            loginPassword.setVisible(false);
        }else{
            loginPassword.setText(loginShowPassword.getText());
            loginShowPassword.setVisible(false);
            loginPassword.setVisible(true);
        }
    }

    public void showPasswordRegister(){
        if(registerCheckbox.isSelected()){
            registerShowPassword.setText(registerPassword.getText());
            registerShowPassword.setVisible(true);
            registerPassword.setVisible(false);
        }else{
            registerPassword.setText(registerShowPassword.getText());
            registerShowPassword.setVisible(false);
            registerPassword.setVisible(true);
        }
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
    public void userList(){
        List<String> listUser = new ArrayList<>();
        for(String data: Users.user){
            listUser.add(data);
        }
        ObservableList<String> observableList = FXCollections.observableList(listUser);
        selectUserType.setItems(observableList);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList();
    }
}
