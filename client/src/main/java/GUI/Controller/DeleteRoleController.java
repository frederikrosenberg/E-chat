package GUI.Controller;

import Acquaintence.ConnectionState;
import Acquaintence.IRole;
import GUI.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import GUI.NotificationUpdater;

public class DeleteRoleController {

    @FXML
    ComboBox<IRole> selectRole;

    @FXML
    Button deleteRole;

    private IRole selectedRole;

    public void initialize() {

        for (IRole role : GUI.getInstance().getBusiness().getRoles().getResponse()) {
            selectRole.getItems().add(role);
        }
        if(!selectRole.getItems().isEmpty()) {
            selectRole.getSelectionModel().select(0);
            selectedRole = selectRole.getItems().get(0);
        }
        selectRole.valueProperty().addListener(new ChangeListener<IRole>() {
            @Override
            public void changed(ObservableValue<? extends IRole> observableValue, IRole iRole, IRole t1) {
                selectedRole = t1;

            }

        });

    }

    public void deleteRole(ActionEvent actionEvent) {
        ConnectionState connectionState = GUI.getInstance().getBusiness().deleteRole(selectedRole.getId());

        GUI.getInstance().loadMainScene();
        String input = "Succesfully deleted the role " + selectedRole.getName();
        NotificationUpdater.getInstance().showNotification(input, connectionState);
    }
}
