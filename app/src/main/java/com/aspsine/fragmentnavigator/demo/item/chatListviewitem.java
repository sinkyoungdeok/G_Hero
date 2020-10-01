package com.aspsine.fragmentnavigator.demo.item;

public class chatListviewitem {
    private int icon;
    private String content;
    private String date;
    private Boolean MEorYOUR;
    public int getIcon() { return icon; }
    public String getContent() { return content; }
    public String getDate() { return date; }
    public Boolean getMEorYOUR() { return MEorYOUR; }

    public chatListviewitem(int icon, String content, String date,Boolean MEorYOUR) {
        this.icon = icon;
        this.content = content;
        this.date = date;
        this.MEorYOUR = MEorYOUR;
    }
}
