package com.example.olekc.musterforparty;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String name;
    @Exclude
    private boolean isSelected;
    public Map<String,Boolean> groups = new HashMap<>();
    public Map<String,Double> location = new HashMap<>();

    public User(){}

    public User(String name)
    {
        this.name = name;
        this.isSelected = false;
    }

    @Exclude
    public Map<String,Object> toMap()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("groups",groups);
        map.put("location",location);
        return map;
    }

    @Exclude
    public boolean isSelected() {
        return isSelected;
    }

    @Exclude
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Exclude
    public boolean changeSelected()
    {
        isSelected = !isSelected;
        return isSelected;
    }
}
