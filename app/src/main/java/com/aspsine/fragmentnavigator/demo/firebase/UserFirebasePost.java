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
    public String phoneNumber;
    public String firstEnrolled;
    public String gender;

    public UserFirebasePost(){

    }
    public UserFirebasePost(String id, String otherHalf,String name, String birthday, String firstDay,String profileUrl, String code, String phoneNumber, String firstEnrolled,String gender){
        this.id = id;
        this.otherHalf = otherHalf;
        this.name = name;
        this.birthday = birthday;
        this.firstDay = firstDay;
        this.profileUrl = profileUrl;
        this.code = code;
        this.phoneNumber = phoneNumber;
        this.firstEnrolled = firstEnrolled;
        this.gender = gender;
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
        result.put("phoneNumber",phoneNumber);
        result.put("firstEnrolled",firstEnrolled);
        result.put("gender",gender);
        return result;
    }
}
