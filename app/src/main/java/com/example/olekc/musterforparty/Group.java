package com.example.olekc.musterforparty;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Group {

    public String name;
    public String admin;
    public Map<String, Boolean> members = new HashMap<>();
    public int type;
    public boolean visible;
    @Exclude
    public boolean member;
    @Exclude
    public String key;

    public Group(){}
    public Group(String admin, String name, int type, boolean visible)
    {
        this.admin = admin;
        this.name = name;
        this.type = type;
        this.visible = visible;
    }

    @Exclude
    public Map<String,Object> toMap()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("admin",admin);
        map.put("members",members);
        map.put("type",type);
        map.put("visible",visible);
        return map;
    }
}
