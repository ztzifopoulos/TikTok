package com.example.tiktok.model;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

public class VideoFile implements Serializable
{
    private String videoName, dateCreated, videoPath= null;
    private ChannelName channelName = null;
    private int frameWidth , frameHeight , framerate = 0;
    private long length = 0;
    private ArrayList<String> associatedHashtags = new ArrayList<String>();
    int totalChunks;
    int chunkID;
    byte[] videoFileChunk;


    public VideoFile(String videoName , ChannelName channelName){
        this.videoName=videoName;
        this.channelName=channelName;
    }

    public VideoFile(String videoName , ChannelName channelName,String videoPath){
        this.videoName = videoName;
        this.channelName=channelName;
        this.videoPath=videoPath;

    }



    public VideoFile(String videoName, ChannelName channelName,int frameHeight , int frameWidth , int framerate , int length){
        this.videoName = videoName;
        this.channelName = channelName;
        //long length = getLength();
        this.length = length;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;

        }



    public void setVideoName(String videoName){
        this.videoName = videoName;
    }

   public String getVideoName(){
        return videoName;
    }

    public void setChannelName(ChannelName channelName){
        this.channelName = channelName;
    }

    public String getChannelName(){
        return channelName.getChannelName();
    }

    public ChannelName getObjectChannelName(){return channelName;}





    public String getDate(Path path)throws IOException{
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        FileTime temp = attr.creationTime();
        String dateCreated = temp.toString();
        return dateCreated;
    }
    public void setAssociatedHashtags(ArrayList<String> hashtags){
        this.associatedHashtags=hashtags;
    }

    public ArrayList<String> getAssociatedHashtags() {
        return associatedHashtags;
    }

    public void setVideoFileChunk(byte[] videoFileChunk){ this.videoFileChunk = videoFileChunk; }

    public void setChunkID(int chunkID) { this.chunkID = chunkID;}

    public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; }

    public byte[] getVideoFileChunk() {
        return videoFileChunk;
    }

    public int getChunkId() { return chunkID; } //new

    public int getTotalChunks() { return totalChunks; }

    public void setPath(String path){
        this.videoPath=path;
    }

    public String getPath(){
        return videoPath;

    }

    /*
    public ArrayList<byte[]> splitIntoChunks() {
        ArrayList<byte[]> chunks = new ArrayList<byte[]>();
        int start = 0;
        while (start < videoFileChunk.length)
        {
            int end = Math.min(videoFileChunk.length, start + videoFileChunkSize);
            chunks.add(Arrays.copyOfRange(videoFileChunk, start, end));
            start += videoFileChunkSize;
        }
        return chunks;
    }*/
/*
    public byte[] mergeChunks(ArrayList<byte[]> chunks,int videoFileChunkSize) throws IOException{
        byte[] newVideoFileChunk = new byte[chunks.size() * videoFileChunkSize];
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        for (byte[] chunk : chunks)
        {
            byteStream.write(chunk);
        }
        newVideoFileChunk = byteStream.toByteArray();
        return newVideoFileChunk;
    }
*/
}