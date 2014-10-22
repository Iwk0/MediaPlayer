package com.mediaplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by imishev on 10.10.2014 г..
 */
public class Track implements Parcelable {

    private String name;
    private String path;

    public Track() {
    }

    public Track(Parcel in){
        this.name = in.readString();
        this.path = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(path);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}