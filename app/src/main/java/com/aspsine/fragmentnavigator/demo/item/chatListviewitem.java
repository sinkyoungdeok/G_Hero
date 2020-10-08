package com.aspsine.fragmentnavigator.demo.item;

public class chatListviewitem {
    private String iconPath;
    private String content;
    private String date;
    private String name;
    private Boolean MEorYOUR;
    private Boolean read;
    private String prevName;
    private String prevDate;
    public String getIconPath() { return iconPath; }
    public String getContent() { return content; }
    public String getDate() { return date; }
    public String getName() { return name; }
    public Boolean getMEorYOUR() { return MEorYOUR; }
    public Boolean getRead() { return read; }
    public String getPrevName() { return prevName; }
    public String getPrevDate() { return prevDate; }

    public chatListviewitem(String iconPath, String content, String date,String name,Boolean MEorYOUR, Boolean read,String prevName, String prevDate) {
        this.iconPath = iconPath;
        this.content = content;
        this.date = date;
        this.name = name;
        this.MEorYOUR = MEorYOUR;
        this.read = read;
        this.prevName = prevName;
        this.prevDate = prevDate;
    }
}
