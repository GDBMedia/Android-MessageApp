package net.gdbmedia.messagingapp.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Guest on 7/13/16.
 */
public class Conversation {

    private List<User> users = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public Conversation(List<User> users, List<Message> messages) {
        this.users = users;
        this.messages = messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("messages", messages);

        return result;
    }

}
