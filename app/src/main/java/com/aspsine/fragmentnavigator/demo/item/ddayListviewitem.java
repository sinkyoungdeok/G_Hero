package com.aspsine.fragmentnavigator.demo.item;

public class ddayListviewitem {
    private String iconPath;
    private String name;
    private String name2;
    private String key;
    public String getIcon() { return iconPath; }
    public String getName() { return name; }
    public String getName2() { return name2; }
    public String getKey() { return key; }

    public ddayListviewitem(String iconPath, String name, String name2,String key) {
        this.iconPath = iconPath;
        this.name = name;
        this.name2 = name2;
        this.key = key;
    }
}
