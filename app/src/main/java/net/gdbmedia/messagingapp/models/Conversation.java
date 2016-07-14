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
public class Conversation {

    private List<String> userIds = new ArrayList<>();


    private String conversationId;



    private List<String> messages = new ArrayList<>();

    public Conversation() {}

    public Conversation(List<String> userIds, String conversationId, List<String> messages) {
        this.userIds = userIds;
        this.conversationId = conversationId;
        this.messages = messages;
    }

    public List<String> getUserIds() {
        return userIds;
    }


    public String getId() {
        return conversationId;
    }


    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }


    public void addMessage(String message) {
        this.messages.add(message) ;
    }
    public void addUser(String userId) {
        this.userIds.add(userId) ;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userIds", userIds);
        result.put("messages", messages);

        return result;
    }

}
