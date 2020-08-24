package com.aspsine.fragmentnavigator.demo.firebase;

import java.util.HashMap;
import java.util.Map;

public class CodeFirebasePost {
    public String id;
    public String code;
    public CodeFirebasePost(){

    }
    public CodeFirebasePost(String id, String code){
        this.id = id;
        this.code = code;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("code",code);
        return result;
    }
}
