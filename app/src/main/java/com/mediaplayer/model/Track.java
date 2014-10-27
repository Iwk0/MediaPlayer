package com.mediaplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by imishev on 10.10.2014 г..
 */
public class Track implements Parcelable {

    private int id;
    private int duration;
    private String name;
    private String path;
    private String album;
    private boolean isSelected;

    public Track() {
    }

    public Track(Parcel in) {
        this.id = in.readInt();
        this.duration = in.readInt();
        this.name = in.readString();
        this.path = in.readString();
        this.album = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(duration);
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(album);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;

        Track track = (Track) o;

        if (id != track.id) return false;
        if (name != null ? !name.equals(track.name) : track.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}