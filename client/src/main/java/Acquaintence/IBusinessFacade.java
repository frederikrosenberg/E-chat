package Acquaintence;

import Business.Connection.PathEnum;
import Business.Connection.RequestResponse;

import java.util.HashMap;
import java.util.List;

public interface IBusinessFacade {

    ConnectionState login(String username, String password);
    void injectMessageReceiver(IMessageReceiver messageReceiver);
    RequestResponse<List<? extends IChat>> getChats();

    void sendMessage(String message);


    <T> RequestResponse<T> getMessages();

    <T> RequestResponse<T> getUsersInChat();
    RequestResponse<List<? extends IDepartment>> getDepartments();

}
