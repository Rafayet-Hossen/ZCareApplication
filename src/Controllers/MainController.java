package Controllers;

import Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainController {

    @FXML
    private Button loginBtn;

    @FXML
    private CheckBox loginCheckbox;

    @FXML
    private AnchorPane loginForm;

    @FXML
    private TextField loginPassword;

    @FXML
    private ComboBox<?> loginUser;

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
        if (registerEmail.getText().isEmpty() || registerUsername.getText().isEmpty() || registerShowPassword.getText().isEmpty()) {
            AlertUtil.showError("Please fill all the fields");
            return;
        }
        String checkUsername = "SELECT * FROM admin WHERE username = ?";
        String insertUser = "INSERT INTO admin (username, password, email,date) VALUES (?, ?, ?,?)";

        try {
            connect = Database.connectDB();
            assert connect != null;
            preparedStatement = connect.prepareStatement(checkUsername);
            preparedStatement.setString(1, registerUsername.getText());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                AlertUtil.showError("Username already exists!");
            } else {
                preparedStatement = connect.prepareStatement(insertUser);
                preparedStatement.setString(1, registerUsername.getText());
                preparedStatement.setString(2, registerShowPassword.getText());
                preparedStatement.setString(3, registerEmail.getText());
                preparedStatement.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                int rows = preparedStatement.executeUpdate();
                if (rows > 0) {
                    AlertUtil.showSuccess("Account created successfully!");
                } else {
                    AlertUtil.showError("Failed to create account.");
                }
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

}
