package com.example.songbook;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;

@IgnoreExtraProperties
public class Song extends OutputStream {
    public String singer, songname, text;
    public Song () {}

    @Override
    public void write(int b) throws IOException {

    }

    public Song(String singer, String songname, String text) {
        this.singer = singer;
        this.songname = songname;
        this.text = text;
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
