package net.gdbmedia.messagingapp.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Guest on 7/13/16.
 */
@IgnoreExtraProperties
@Parcel
public class User {

    private String name;
    private String id;
    private String email;
    private List<String> conversationIds = new ArrayList<>();


    public User() {}

    public User(String name, String id, String email, List<String> conversationIds) {
        this.name = name;
        this.id = id;
        this.conversationIds = conversationIds;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<String> getConversationIds() {
        return conversationIds;
    }
    public void addConversationId(String conversationId) {
        this.conversationIds.add(conversationId);
    }
    public void setConversationIds(List<String> conversationIds) {
        this.conversationIds = conversationIds;
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
        result.put("conversationIds", conversationIds);

        return result;
    }



}
