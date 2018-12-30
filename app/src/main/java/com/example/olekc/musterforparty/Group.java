package com.example.olekc.musterforparty;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Group {

    public String name;
    public Map<String, Boolean> members = new HashMap<>();
    public int type;
    public boolean visible;

    public Group(){}
    public Group(String name, int type, boolean visible)
    {
        this.name = name;
        this.type = type;
        this.visible = visible;
    }

    @Exclude
    public Map<String,Object> toMap()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("members",members);
        map.put("type",type);
        map.put("visible",visible);
        return map;
    }
}
