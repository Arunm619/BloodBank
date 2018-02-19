package com.atribus.bloodbankyrc.Model;

/**
 * Created by root on 2/12/17.
 */

public class post {
    private String post_title;
    private String post_content;
    private String post_description;
    private String img_path;
    private String datecreated;
    private String url;

    //object -> json object-> string

    //String -> fcm push notifications
    // String body


    public post(String post_title, String post_content, String post_description, String img_path, String datecreated, String url) {
        this.post_title = post_title;
        this.post_content = post_content;
        this.post_description = post_description;
        this.img_path = img_path;
        this.datecreated = datecreated;
        this.url = url;
    }

    public post() {
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_description() {
        return post_description;
    }

    public void setPost_description(String post_description) {
        this.post_description = post_description;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
