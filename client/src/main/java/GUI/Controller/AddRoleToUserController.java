package GUI.Controller;

import Acquaintence.ConnectionState;
import Acquaintence.IRole;
import Acquaintence.IUser;
import GUI.NotificationUpdater;
import GUI.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.util.List;

public class AddRoleToUserController {

    @FXML
    ComboBox<IUser> selectUser;

    @FXML
    ComboBox<IRole> selectRole;

    @FXML
    Button addRoleToUser;

    private IUser selectedUser;
    private IRole selectedRole;

    public void initialize() {
        //TODO Make labels or something for clarity
        for (IUser user : GUI.getInstance().getBusiness().getUsers().getResponse()) {
            selectUser.getItems().add(user);
        }
        selectUser.valueProperty().addListener(new ChangeListener<IUser>() {
            @Override
            public void changed(ObservableValue<? extends IUser> observableValue, IUser iUser, IUser t1) {
                selectedUser = t1;
            }
        });

        for (IRole role : GUI.getInstance().getBusiness().getRoles().getResponse()) {
            selectRole.getItems().setAll(GUI.getInstance().getBusiness().getRoles().getResponse());
        }

        selectRole.valueProperty().addListener(new ChangeListener<IRole>() {
            @Override
            public void changed(ObservableValue<? extends IRole> observableValue, IRole iRole, IRole t1) {
                selectedRole = t1;
            }
        });
    }

    public void addSelected(javafx.event.ActionEvent actionEvent) {
        ConnectionState connectionState = GUI.getInstance().getBusiness().addRoleToUser(selectedUser.getId(), selectedRole.getId());
        Stage stage = (Stage) addRoleToUser.getScene().getWindow();
        stage.setScene(GUI.getInstance().getPrimaryScene());
        String input = "Succesfully added role " + selectedRole.getName() + " to the user " + selectedUser.getName();
        NotificationUpdater.getInstance().showNotification(input, connectionState);
    }
}
