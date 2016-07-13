package net.gdbmedia.messagingapp.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guest on 7/13/16.
 */
public class Message {



    private String content;
    private String uid;
    private String timestamp;

    public Message(String content, String uid, String timestamp) {
        this.content = content;
        this.uid = uid;
        this.timestamp = timestamp;
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
        result.put("uid", uid);
        result.put("timestamp", timestamp);

        return result;
    }

}
