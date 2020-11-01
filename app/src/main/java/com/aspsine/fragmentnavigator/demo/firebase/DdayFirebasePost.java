package com.aspsine.fragmentnavigator.demo.firebase;

import java.util.HashMap;
import java.util.Map;

public class DdayFirebasePost {
    public String content;
    public String startDate;
    public String ddayUrl;
    public DdayFirebasePost() {

    }
    public DdayFirebasePost(String content, String startDate, String ddayUrl) {
        this.content = content;
        this.startDate = startDate;
        this.ddayUrl = ddayUrl;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("content",content);
        result.put("startDate", startDate);
        result.put("ddayUrl", ddayUrl);
        return result;
    }
}
