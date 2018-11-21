package Business;


import Acquaintence.*;
import Acquaintence.Event.*;
import Business.Connection.HubConnect;
import Business.Connection.PathEnum;
import Business.Connection.RequestResponse;
import Business.Connection.RestConnect;
import Business.Models.*;
import GUI.GUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BusinessFacade implements IBusinessFacade {

    private HubConnect hubConnect = new HubConnect();
    private RestConnect restConnect = new RestConnect();
    private List<Department> departments = null;
    private Department currentDepartment = null;
    private List<Chat> chats = null;
    private Chat currentChat = null;
    private List<User> users = null;
    private LoginUser loginUser = null;
    private String token = null;



    public <T extends EventObject> BusinessFacade() {
        EventManager.getInstance().registerListener(MessageEvent.class, this::getMessage);
        EventManager.getInstance().registerListener(NewChatEvent.class, this::getNewChat);
        EventManager.getInstance().registerListener(AddChatEvent.class, this::addChat);
        EventManager.getInstance().registerListener(RemoveUserFromChatEvent.class, this::removeUserFromChat);
        EventManager.getInstance().registerListener(LeaveChatEvent.class, this::leaveChatEvent);
    }



    /* Listener methods */
    private void getMessage(MessageEvent event) {
        for(Chat chat : chats) {
            if(chat.getId() == event.getMessageIn().getChatId()) {
                if(chat.getMessages().isEmpty()) {
                    // TODO make new thread for downloading existing messages
                    getMessages(chat.getId());
                }
                chat.addMessage((MessageIn) event.getMessageIn());
                break;
            }
        }
    }

    private void leaveChatEvent(LeaveChatEvent leaveChatEvent) {
        if(leaveChatEvent.getUser().getId().equals(loginUser.getSub())) {
            chats.removeIf(chat -> chat.getId() == leaveChatEvent.getChatId());
        }
        users.clear();
        users.addAll((List<User>)getUsers().getResponse());
    }

    private void removeUserFromChat(RemoveUserFromChatEvent removeUserFromChatEvent) {
        if(removeUserFromChatEvent.getUser().getId().equals(loginUser.getSub())) {
            chats.removeIf(chat -> chat.getId() == removeUserFromChatEvent.getChatId());
        }
        users.clear();
        users.addAll((List<User>)getUsers().getResponse());
    }

    private void addChat(AddChatEvent addChatEvent) {
        boolean found = false;
        for (Chat chat : chats) {
            if(chat.getId() == addChatEvent.getChatId()) {
                found = true;
                break;
            }
        }
        if(!found) {
            RequestResponse<Chat> chat = restConnect.get(PathEnum.GetChat, addChatEvent.getChatId(), null, token);
            chats.add(chat.getResponse());
            if(users.contains(addChatEvent.getUser())) {
                RequestResponse<User> user = restConnect.get(PathEnum.GetUser, addChatEvent.getUser().getId(), null, token);
                users.add(user.getResponse());
                EventManager.getInstance().fireEvent(new AddUserEvent(this, user.getResponse()));
            }
        }
    }

    private void getNewChat(NewChatEvent newChatEvent) {
        chats.add((Chat)newChatEvent.getChat());
    }

    @Override
    public ConnectionState login(String username, String password) {
        RequestResponse<String> temp = restConnect.login(username, password);
        if(temp.getConnectionState() == ConnectionState.SUCCESS) {
            token = temp.getResponse();
            hubConnect.connect(token);
            RequestResponse<LoginUser> data = restConnect.get(PathEnum.GetUserInfo, null, null, token);
            loginUser = data.getResponse();
            loginUser.initializePermissions();
            getDepartments();
            getUsers();
            getChats();
        }
        return temp.getConnectionState();
    }

    @Override
    public List<String> getRolesPermissions(String roleName) {
        RequestResponse<List<String>> response = restConnect.get(PathEnum.GetRolesPermissions, roleName, null, token);
        List<String> permissions = response.getResponse();
        return permissions;
    }

    @Override
    public RequestResponse<List<? extends IUser>> getUsers() {
        RequestResponse<List<User>> response = restConnect.get(PathEnum.GetUsers, loginUser.getSub(), null, token);
        users = response.getResponse();
        return new RequestResponse<>(response.getResponse(), response.getConnectionState());
    }

    @Override
    public RequestResponse<Chat> createDirectMessage(String name, IUser otherUser) {
        Chat chat = new Chat(name);
        RequestResponse<Chat> response = restConnect.post(PathEnum.CreateDirectMessage, otherUser.getId(), chat, token);
        chats.add(response.getResponse());
        return new RequestResponse<>(response.getResponse(), response.getConnectionState());
    }

    @Override
    public RequestResponse<String> addUserToSpecificChat(String userId, IChat chat) {
        RequestResponse<String> response = restConnect.post(PathEnum.AddUserToChat, chat.getId(), userId, token);
        return new RequestResponse<>(response.getResponse(), response.getConnectionState());
    }

    @Override
    public void setCurrentChat(int chatId) {
        if(currentChat != null) {
            if(currentChat.getId() != chatId) {
                for (Chat tempchat : chats) {
                    if(tempchat.getId() == chatId) {
                        currentChat = tempchat;
                        if(currentChat.getMessages().isEmpty()) {
                            getMessages();
                        }
                        EventManager.getInstance().fireEvent(new ChangeChatEvent(this, currentChat));
                        break;
                    }
                }
            }
        } else if (chats != null) {
            currentChat = chats.get(0);
            if(currentChat.getMessages().isEmpty()) {
                getMessages();
            }
            EventManager.getInstance().fireEvent(new ChangeChatEvent(this, currentChat));
        }

    }

    @Override
    public void createUser(String username, String password, IRole role, ArrayList<Integer> departmentsIds) {
        CreateUser userToSend = new CreateUser(username, password, role.getName(), departmentsIds);
        restConnect.post(PathEnum.CreateUser, null, userToSend, token);
    }

    @Override
    public ConnectionState createChat(String chatName, int departmentId){
        Chat chatToSend = new Chat(chatName);
        RequestResponse<Chat> response = restConnect.post(PathEnum.CreateChatroom, departmentId, chatToSend, token);
        return response.getConnectionState();
    }

    public void createDepartment(String departmentName){
        Department departmentToSend = new Department(departmentName);
        RequestResponse<Department> response = restConnect.post(PathEnum.CreateDepartment, null, departmentToSend, token);
        departments.add(response.getResponse());
    }

    public void updateDepartment(int depId, String name){
        restConnect.put(PathEnum.UpdateDepartment, depId, name, token);
    }

    @Override
    public void addUserToDepartment(int depId, String userId) {
        restConnect.post(PathEnum.AddUserToDeparment, depId, userId, token);
    }

    public void deleteDepartment(int depId){
        restConnect.delete(PathEnum.DeleteDepartment, depId, token);
    }

    @Override
    public void createUserRole(List<String> permissions, String roleName) {
        restConnect.post(PathEnum.CreateUserRole, roleName, permissions, token);
    }

    @Override
    public ILoginUser getLoginUser() {
        return loginUser;
    }

    @Override
    public RequestResponse<List<? extends IRole>> getRoles() {
        return restConnect.get(PathEnum.GetRoles, null, null, token);
    }

    @Override
    public List<? extends IUser> getExistingUsers() {
        return users;
    }

    public void addRoleToUser(String userId, String role) {
        restConnect.put(PathEnum.AddRoleToUser, userId, role, token);
    }

    @Override
    public ConnectionState addUserToChat(int chatId, String userId) {
        return restConnect.post(PathEnum.AddUserToChat, chatId, userId, token).getConnectionState();
    }

    @Override
    public List<? extends IChat> getAvailableChats(String userId) {
        RequestResponse<List<Chat>> response = restConnect.get(PathEnum.GetAvailableChats, userId, null, token);
        return response.getResponse();
    }

    public void removePermissionsFromRole(String role, List<String> permissions) {
        restConnect.post(PathEnum.RemovePermissionsFromRole, role, permissions, token);
    }

    @Override
    public void addPermissionsToRole(String role, List<String> permissions) {
        restConnect.post(PathEnum.AddPermissionsToRole, role, permissions, token);
    }

    @Override
    public void deleteUser(String userId) {
        restConnect.delete(PathEnum.DeleteUser, userId, token);
    }

    @Override
    public List<IChat> getUsersChats(String userId) {
        RequestResponse<List<Department>> departments = restConnect.get(PathEnum.GetDepartments, userId, null, token);
        List<IChat> usersChats = new ArrayList<>();
        for(Department department : departments.getResponse()) {
            RequestResponse<List<Chat>> response = restConnect.get(PathEnum.GetChats, userId, department.toMap(), token);
            if(response.getResponse() != null ) {
                usersChats.addAll(response.getResponse());
            }
        }
        return usersChats;

    }

    @Override
    public void removeUserFromChat(int chatId, String userId) {
        restConnect.post(PathEnum.RemoveUserFromChat, chatId, userId, token);
    }

    public RequestResponse<List<? extends IChat>> getChats() {
        RequestResponse<List<Chat>> chats = restConnect.get(PathEnum.GetChats, loginUser.getSub(), null, token);
        if(!chats.getResponse().isEmpty()) {
            this.chats = chats.getResponse();
            currentChat = this.chats.get(0);
        }
        return new RequestResponse<>(chats.getResponse(), chats.getConnectionState());
    }

    @Override
    public Chat getCurrentChat() {
        return currentChat;
    }

    @Override
    public RequestResponse<List<String>> getAllPermissions() {
        RequestResponse<List<String>> response = restConnect.get(PathEnum.GetAllPermissions, null, null, token);
        return new RequestResponse<>(response.getResponse(), response.getConnectionState());
    }

    @Override
    public List<? extends IChat> getExistingChats() {
        return chats;
    }

    @Override
    public void deleteUserRole(String roleName) {
        restConnect.delete(PathEnum.DeleteUserRole, roleName, token);
    }

    @Override
    public RequestResponse<List<? extends IUser>> getUsersInChat() {
        return restConnect.get(PathEnum.GetUsersInChat, currentChat.getId(), null, token );
    }

    public RequestResponse<List<? extends IDepartment>> getDepartments() {
        RequestResponse<List<Department>> response = restConnect.get(PathEnum.GetDepartments, loginUser.getSub(),null,token);
        if(response.getResponse() != null && !response.getResponse().isEmpty()) {
            currentDepartment = response.getResponse().get(0);
            departments = response.getResponse();
        }
        return new RequestResponse<>(response.getResponse(), response.getConnectionState());
    }

    @Override
    public RequestResponse<List<? extends IDepartment>> getAllDepartments() {
        return restConnect.get(PathEnum.GetAllDepartments, null, null, token );
    }

    @Override
    public RequestResponse<List<? extends IMessageIn>> getMessages(int chatId) {
        RequestResponse<List<MessageIn>> response = restConnect.get(PathEnum.GetMessages, chatId, new Page(0,20).toMap(), token);
        for(Chat chat : chats) {
            if(chat.getId() == chatId) {
                if (chat.getMessages().isEmpty()) {
                    chat.addMessages(response.getResponse());
                }
            }
        }
        return new RequestResponse<>(response.getResponse(), response.getConnectionState());
    }

    @Override
    public RequestResponse<List<? extends IMessageIn>> getMessages() {

        if(currentChat != null) {
            RequestResponse<List<MessageIn>> response = restConnect.get(PathEnum.GetMessages, currentChat.getId(), new Page(0,20).toMap(), token);
            currentChat.addMessages(response.getResponse());
            return new RequestResponse<>(response.getResponse(), response.getConnectionState());
        }
        return new RequestResponse<>(new ArrayList<MessageIn>(), ConnectionState.SUCCESS);

    }

    @Override
    public void sendMessage(String message) {
        hubConnect.sendMessage(message, currentChat.getId());
    }

    @Override
    public void logout() {
        restConnect.logout(token);
        departments = null;
        currentDepartment = null;
        chats = null;
        currentChat = null;
        users = null;
        loginUser = null;
        token = null;
        hubConnect.disconnect();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            GUI.getInstance().getStage().setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}