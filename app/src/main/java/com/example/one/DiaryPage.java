package com.example.one;

public class DiaryPage {
    private String title;
    private String content;

    public DiaryPage(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
