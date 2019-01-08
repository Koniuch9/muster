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
    public Map<String, Object> places = new HashMap<>();
    public Map<String, Boolean> invite = new HashMap<>();
    @Exclude
    public boolean member;
    @Exclude
    public String key;

    public Group(){}

    public Group(String admin, String name)
    {
        this.admin = admin;
        this.name = name;
    }

    @Exclude
    public Map<String,Object> toMap()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("admin",admin);
        map.put("members",members);
        map.put("places",places);
        return map;
    }
}
