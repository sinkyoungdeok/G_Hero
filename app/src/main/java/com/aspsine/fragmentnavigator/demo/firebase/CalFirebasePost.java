package com.aspsine.fragmentnavigator.demo.firebase;

import java.util.HashMap;
import java.util.Map;

public class CalFirebasePost {
    public String content;
    public String date;
    public CalFirebasePost(){

    }
    public CalFirebasePost(String content, String date){
        this.content = content;
        this.date = date;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("content",content);
        result.put("date",date);
        return result;
    }
}
