package com.example.olekc.musterforparty;

public class User {

    private String name;
    private boolean isSelected;

    public User(String name)
    {
        this.name = name;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean changeSelected()
    {
        isSelected = !isSelected;
        return isSelected;
    }
}
