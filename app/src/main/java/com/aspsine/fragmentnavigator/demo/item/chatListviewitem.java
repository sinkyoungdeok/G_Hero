package com.aspsine.fragmentnavigator.demo.item;

public class chatListviewitem {
    private String iconPath;
    private String content;
    private String date;
    private String name;
    private Boolean MEorYOUR;
    private Boolean read;
    public String getIconPath() { return iconPath; }
    public String getContent() { return content; }
    public String getDate() { return date; }
    public String getName() { return name; }
    public Boolean getMEorYOUR() { return MEorYOUR; }
    public Boolean getRead() { return read; }

    public chatListviewitem(String iconPath, String content, String date,String name,Boolean MEorYOUR, Boolean read) {
        this.iconPath = iconPath;
        this.content = content;
        this.date = date;
        this.name = name;
        this.MEorYOUR = MEorYOUR;
        this.read = read;
    }
}
