package com.caiji.musicplayer.model;

import androidx.annotation.NonNull;

//封装歌曲有用的信息
public class MusicData {
    private String name;    //歌曲名字
    private String id;      //歌曲id
    private String picUrl;  //专辑url
    private String artist;  //歌手
    private String playUrl; //播放链接

    @NonNull
    @Override
    public String toString() {
        return "MusicData{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", artist='" + artist + '\'' +
                ", playUrl='" + playUrl + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.playUrl = "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPlayUrl() {
        return playUrl;
    }
}
