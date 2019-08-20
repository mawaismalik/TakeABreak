package com.example.malix.takeabreak.ui;


import android.graphics.drawable.Drawable;

public class AppList {



    private String name;
    Drawable icon;
    private String packageName;
    boolean isSelected;
    public AppList(String name, Drawable icon , String packageName) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
