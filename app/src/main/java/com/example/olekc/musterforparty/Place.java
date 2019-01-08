package com.example.olekc.musterforparty;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Place {

    public Map<String, Double> location = new HashMap<>();
    public String name;
    public String note;

    @Exclude
    public String key;

    public Place(){}

    public Place(String name, String note)
    {
        this.name = name;
        this.note = note;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("location",location);
        map.put("note",note);
        return map;
    }
}
