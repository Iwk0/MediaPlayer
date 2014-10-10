package com.mediaplayer.model;

/**
 * Created by imishev on 10.10.2014 г..
 */
public class Track {

    private String name;
    private String path;

    public Track() {
        name = null;
        path = null;
    }

    public Track(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}