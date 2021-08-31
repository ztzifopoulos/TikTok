package com.example.tiktok.model;
import java.io.Serializable;
import java.util.ArrayList;

public class Value implements Serializable {
    VideoFile videoFile;
    public Value(VideoFile videoFile){
        this.videoFile = videoFile;
    }
    public Value(String videofile){

    }

    public Value(String videofile,ChannelName channelName){
        VideoFile videoFile= new VideoFile(videofile,channelName);

    }

    public Value(String videofile,ChannelName channelName,String videoPath){
        VideoFile videoFile= new VideoFile(videofile,channelName,videoPath);

    }
    public VideoFile getVideoFile() {
        return videoFile;
    }
    public void setVideoFile(VideoFile videoFile) {
        this.videoFile = videoFile;
    }
    public ArrayList<String> getVideoHashtags(){
        return videoFile.getAssociatedHashtags();
    }
}