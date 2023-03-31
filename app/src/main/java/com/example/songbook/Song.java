package com.example.songbook;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.File;
import java.lang.reflect.Array;

@IgnoreExtraProperties
public class Song {
    public String access, singer, songname, text;
    public Song () {}
    public Song(String access, String singer, String songname, String text) {
        this.access = access;
        this.singer = singer;
        this.songname = songname;
        this.text = text;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
