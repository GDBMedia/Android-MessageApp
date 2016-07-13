package net.gdbmedia.messagingapp.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Guest on 7/13/16.
 */
@IgnoreExtraProperties
public class User {

    private String name;
    private String id;
    private String email;
    private List<Conversation> conversations = new ArrayList<>();


    public User() {}

    public User(String name, String id, String email, List<Conversation> conversations) {
        this.name = name;
        this.id = id;
        this.conversations = conversations;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }
    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("id", id);
        result.put("email", email);
        result.put("conversations", conversations);

        return result;
    }



}
