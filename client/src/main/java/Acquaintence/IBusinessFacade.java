package Acquaintence;

import Business.Connection.PathEnum;
import Business.Connection.RequestResponse;

import java.util.HashMap;
import java.util.List;

public interface IBusinessFacade {
    ConnectionState login(String username, String password);
    void injectMessageReceiver(IMessageReceiver messageReceiver);
    RequestResponse<List<? extends IChat>> getChats(HashMap<String, String> param);
    void sendMessage(String message);
}
