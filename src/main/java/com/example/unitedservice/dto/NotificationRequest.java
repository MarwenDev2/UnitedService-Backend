package com.example.unitedservice.dto;

public class NotificationRequest {

    private String title;
    private String body;
    private String icon;
    private String image;
    private String tag;
    private Object data;
    private Long userId; // Send to specific user
    private String type; // leave_request, mission, advance, system

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}