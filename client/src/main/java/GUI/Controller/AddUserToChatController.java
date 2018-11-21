package GUI.Controller;

import Acquaintence.IChat;
import Acquaintence.IUser;
import Business.Connection.PermissionType;
import GUI.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class AddUserToChatController {
    @FXML
    public ComboBox<IChat> chatsCB;
    @FXML
    public ComboBox<IUser> usersCB;

    public void initialize() {
        // Get chats that the selected user is in, do a eventhandler on user combobox switch, and update the chatsCB items
        //chatsCB.getItems().addAll(GUI.getInstance().getBusiness().getAllChats().getResponse());
        usersCB.getItems().addAll(GUI.getInstance().getBusiness().getUsers().getResponse());
        usersCB.valueProperty().addListener(new ChangeListener<IUser>() {
            @Override
            public void changed(ObservableValue<? extends IUser> observable, IUser oldValue, IUser newValue) {
                chatsCB.getItems().setAll(GUI.getInstance().getBusiness().getAvailableChats(newValue.getId()));
            }
        });
    }

    public void addUser() {
        GUI.getInstance().getBusiness().addUserToChat(chatsCB.getSelectionModel().getSelectedItem().getId(), usersCB.getSelectionModel().getSelectedItem().getId());
    }
}