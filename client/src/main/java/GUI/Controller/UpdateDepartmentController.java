package GUI.Controller;

import Acquaintence.ConnectionState;
import Acquaintence.IDepartment;
import GUI.NotificationUpdater;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateDepartmentController {

    public ChoiceBox<IDepartment> choiceBox;
    public TextField txTField;
    public Button renameBtn;

    public void initialize(){

        choiceBox.getItems().addAll(GUI.GUI.getInstance().getBusiness().getAllDepartments().getResponse());
        if(!choiceBox.getItems().isEmpty()){
            choiceBox.getSelectionModel().select(0);
        }

    }


    public void rename(ActionEvent actionEvent){
        ConnectionState connectionState = GUI.GUI.getInstance().getBusiness().updateDepartment(choiceBox.getValue().getId(), txTField.getText());
        Stage stage = (Stage) renameBtn.getScene().getWindow();
        stage.setScene(GUI.GUI.getInstance().getPrimaryScene());
        String input = "Succesfully renamed the department " + choiceBox.getValue().getName() + " to the name " + txTField.getText();
        NotificationUpdater.getInstance().showNotification(input, connectionState);
    }

}
