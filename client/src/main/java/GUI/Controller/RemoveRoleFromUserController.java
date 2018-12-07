package GUI.Controller;

import Acquaintence.IRole;
import Acquaintence.IUser;
import GUI.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class RemoveRoleFromUserController {

    @FXML
    ComboBox<IUser> selectUser;

    @FXML
    ComboBox<IRole> selectRole;

    @FXML
    ChoiceBox<IUser> userCB;

    @FXML
    ChoiceBox<IRole> roleCB;

    @FXML
    Button removeRoleFromUser;

    private String selectedUser;
    private String selectedRole;

    /*public void initialize() {
        //TODO Fine tune it later
        for (IUser user : GUI.getInstance().getBusiness().getUsers().getResponse()) {
            selectUser.getItems().add(user);
        }
        selectUser.valueProperty().addListener(new ChangeListener<IUser>() {
            @Override
            public void changed(ObservableValue<? extends IUser> observableValue, IUser iUser, IUser t1) {
                selectedUser = t1.getId();
            }
        });

        for (IRole role : GUI.getInstance().getBusiness().getRoles().getResponse()) {
            selectRole.getItems().setAll(GUI.getInstance().getBusiness().getRoles().getResponse());
        }

        selectRole.valueProperty().addListener(new ChangeListener<IRole>() {
            @Override
            public void changed(ObservableValue<? extends IRole> observableValue, IRole iRole, IRole t1) {
                selectedRole = t1.getName();
            }
        });
    }*/

    public void initialize(){
        userCB.getItems().setAll(GUI.getInstance().getBusiness().getUsers().getResponse());

        roleCB.getItems().setAll(GUI.getInstance().getBusiness().getAvailableRoles(userCB.getValue().getId()).getResponse());
    }

    public void removeSelected(ActionEvent actionEvent) {
        GUI.getInstance().getBusiness().removeRoleFromUser(userCB.getValue().getId(), roleCB.getValue().getName());
        Stage stage = (Stage) removeRoleFromUser.getScene().getWindow();
        stage.setScene(GUI.getInstance().getPrimaryScene());
    }


}