package com.aspsine.fragmentnavigator.demo.firebase;

import java.util.HashMap;
import java.util.Map;



public class CalFirebasePost {
    public String content;
    public String startDate;
    public String startTime;
    public String endDate;
    public String endTime;
    public String scheduleOrDday;
    public CalFirebasePost(){

    }
    public CalFirebasePost(String content, String startDate, String startTime, String endDate, String endTime, String scheduleOrDday){
        this.scheduleOrDday = scheduleOrDday;
        this.content = content;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("content",content);
        result.put("startDate",startDate);
        result.put("startTime", startTime);
        result.put("endDate", endDate);
        result.put("endTime", endTime);
        result.put("scheduleOrDday",scheduleOrDday);
        return result;
    }
}

