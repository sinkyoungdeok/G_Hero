package com.aspsine.fragmentnavigator.demo.firebase;

import java.util.HashMap;
import java.util.Map;

public class UserFirebasePost {
    public String id;
    public String otherHalf;
    public String name;
    public String birthday;
    public String firstDay;
    public String profileUrl;
    public String code;
    public String FCMToken;
    public String firstEnrolled;
    public String gender;
    public String backgroundUrl;

    public UserFirebasePost(){

    }
    public UserFirebasePost(String id, String otherHalf,String name, String birthday, String firstDay,String profileUrl, String code, String FCMToken, String firstEnrolled,String gender,String backgroundUrl){
        this.id = id;
        this.otherHalf = otherHalf;
        this.name = name;
        this.birthday = birthday;
        this.firstDay = firstDay;
        this.profileUrl = profileUrl;
        this.code = code;
        this.FCMToken = FCMToken;
        this.firstEnrolled = firstEnrolled;
        this.gender = gender;
        this.backgroundUrl = backgroundUrl;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("otherHalf",otherHalf);
        result.put("name",name);
        result.put("birthday",birthday);
        result.put("firstDay",firstDay);
        result.put("profileUrl",profileUrl);
        result.put("code",code);
        result.put("FCMToken",FCMToken);
        result.put("firstEnrolled",firstEnrolled);
        result.put("gender",gender);
        result.put("backgroundUrl",backgroundUrl);
        return result;
    }
}
