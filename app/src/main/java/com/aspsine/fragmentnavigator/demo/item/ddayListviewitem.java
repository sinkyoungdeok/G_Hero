package com.aspsine.fragmentnavigator.demo.item;

public class ddayListviewitem {
    private String iconPath;
    private String name;
    private String name2;
    private String key;
    private String startDate;
    public String getIcon() { return iconPath; }
    public String getName() { return name; }
    public String getName2() { return name2; }
    public String getKey() { return key; }
    public String getStartDate() { return startDate; }

    public ddayListviewitem(String iconPath, String name, String name2,String key,String startDate) {
        this.iconPath = iconPath;
        this.name = name;
        this.name2 = name2;
        this.key = key;
        this.startDate = startDate;
    }
}
