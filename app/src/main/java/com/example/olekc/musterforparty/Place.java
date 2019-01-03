package com.example.olekc.musterforparty;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Place {

    public Map<String, Double> location;
    public String name;


    public Place(){}

    public Place(String name)
    {
        this.name = name;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("location",location);
        return map;
    }
}
