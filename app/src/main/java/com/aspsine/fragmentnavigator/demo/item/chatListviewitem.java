package com.aspsine.fragmentnavigator.demo.item;

public class chatListviewitem {
    private int icon;
    private String content;
    private String date;
    public int getIcon() { return icon; }
    public String getContent() { return content; }
    public String getDate() { return date; }

    public chatListviewitem(int icon, String content, String date) {
        this.icon = icon;
        this.content = content;
        this.date = date;
    }
}
