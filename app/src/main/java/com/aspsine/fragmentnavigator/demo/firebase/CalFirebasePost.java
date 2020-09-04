package com.aspsine.fragmentnavigator.demo.firebase;

import java.util.HashMap;
import java.util.Map;



public class CalFirebasePost {
    public String content;
    public String date;
    public String scheduleOrDday;
    public CalFirebasePost(){

    }
    public CalFirebasePost(String content, String date, String scheduleOrDday){
        this.scheduleOrDday = scheduleOrDday;
        this.content = content;
        this.date = date;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("content",content);
        result.put("date",date);
        result.put("scheduleOrDday",scheduleOrDday);
        return result;
    }
}

