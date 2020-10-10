package com.aspsine.fragmentnavigator.demo.item;

public class calendarListviewitem {
    private String startTime;
    private String endTime;
    private String content;

    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getContent() { return content; }

    public calendarListviewitem(String startTime, String endTime, String content) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
    }
}
