package com.aspsine.fragmentnavigator.demo.firebase;

import java.util.HashMap;
import java.util.Map;

public class ChatFirebasePost {
    public String id;
    public String name;
    public String content;
    public String date;
    public ChatFirebasePost(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }
    public ChatFirebasePost(String id, String name,String content,String date) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.date = date;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("content",content);
        result.put("date", date);

        return result;
    }
}
