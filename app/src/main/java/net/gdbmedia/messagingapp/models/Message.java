package net.gdbmedia.messagingapp.models;

import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guest on 7/13/16.
 */
@IgnoreExtraProperties
@Parcel
public class Message {


    public Message(String id, String content, String uid, String timestamp, String convoId ){
        this.id = id;
        this.content = content;
        this.uid = uid;
        this.timestamp = timestamp;
        this.convoId = convoId;
    }

    public String getId() {
        return id;
    }

    private String id;
    private String content;

    public String getConvoId() {
        return convoId;
    }

    private String convoId;
    private String uid;
    private String timestamp;

    public Message() {
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("convoId", convoId);
        result.put("uid", uid);
        result.put("timestamp", timestamp);

        return result;
    }

}
