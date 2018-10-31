package GUI.Controller;

import GUI.GUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    public TextField usernameTF;

    @FXML
    public PasswordField passwordTF;

    @FXML
    public Label errorL;

    @FXML
    public void login() {
        if(GUI.getInstance().getBusiness().login(usernameTF.getText(), passwordTF.getText())) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
                GUI.getInstance().getStage().setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorL.setOpacity(1);
        }
    }
}