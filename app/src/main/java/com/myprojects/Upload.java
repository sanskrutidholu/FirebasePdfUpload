package com.myprojects;

public class Upload {


    public String id;
    public String name;
    public String url;
 
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }
 
    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}