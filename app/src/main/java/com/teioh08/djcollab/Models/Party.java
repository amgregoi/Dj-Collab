package com.teioh08.djcollab.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Party implements Parcelable{
    int HostId;
    int Id;
    String Name;
    ArrayList<String> SongList;


    protected Party(Parcel in) {
        HostId = in.readInt();
        Id = in.readInt();
        Name = in.readString();
        SongList = (ArrayList<String>) in.readSerializable();
    }

    public static final Creator<Party> CREATOR = new Creator<Party>() {
        @Override
        public Party createFromParcel(Parcel in) {
            return new Party(in);
        }

        @Override
        public Party[] newArray(int size) {
            return new Party[size];
        }
    };

    public int getHostId() {
        return HostId;
    }

    public void setHostId(int hostId) {
        HostId = hostId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public List<String> getSongList() {
        return SongList;
    }

    public void setSongList(ArrayList<String> songList) {
        SongList = songList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(HostId);
        dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeSerializable(SongList);
    }
}
