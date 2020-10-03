package com.aspsine.fragmentnavigator.demo.item;

public class chatListviewitem {
    private String iconPath;
    private String content;
    private String date;
    private Boolean MEorYOUR;
    public String getIconPath() { return iconPath; }
    public String getContent() { return content; }
    public String getDate() { return date; }
    public Boolean getMEorYOUR() { return MEorYOUR; }

    public chatListviewitem(String iconPath, String content, String date,Boolean MEorYOUR) {
        this.iconPath = iconPath;
        this.content = content;
        this.date = date;
        this.MEorYOUR = MEorYOUR;
    }
}
