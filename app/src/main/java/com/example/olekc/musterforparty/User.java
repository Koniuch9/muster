package com.example.olekc.musterforparty;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String name;
    public String photoUrl;
    public Map<String,Boolean> groups = new HashMap<>();
    public Map<String,Double> location = new HashMap<>();
    public Map<String,Boolean> block = new HashMap<>();
    @Exclude
    private boolean isSelected;
    @Exclude
    public String uid;

    public User(){}

    public User(String name, String photoUrl)
    {
        this.name = name;
        this.photoUrl = photoUrl;
        this.isSelected = false;
    }

    @Exclude
    public void setUid(String uid)
    {
        this.uid = uid;
    }

    @Exclude
    public Map<String,Object> toMap()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("groups",groups);
        map.put("location",location);
        map.put("block",block);
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
