package com.example.tiktok.model;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.widget.MediaController;
import android.widget.VideoView;
import android.net.Uri;
import com.example.tiktok.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static com.example.tiktok.model.MainActivity.VIDEO_NAME;

public class VideoPlayActivity extends AppCompatActivity {
    VideoView video;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void copyFile(File source, File destination) throws IOException {
        FileUtils.copy(new FileInputStream(source), new FileOutputStream(destination));
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        Intent intent = getIntent();
        String title = intent.getStringExtra(VIDEO_NAME);
        /*String channelName = null;
        if (title.contains("kappa"))
            channelName = "kappa";
        if (title.contains("lamda"))
            channelName = "lamda";
        if (title.contains("Mi"))
            channelName = "Mi";
        String videosDir = "C:\\Users\\Zacharias\\AndroidStudioProjects\\TikTok\\app\\src\\main\\assets\\PublishersVideos" + "\\" + channelName + "\\" + title;
        File source = new File(videosDir);
        String destPath = "C:\\Users\\Zacharias\\AndroidStudioProjects\\TikTok\\app\\src\\main\\res\\raw";
        File destination = new File(destPath);
        try {
            copyFile(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        video = (VideoView)findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(video);
        video.setMediaController(mediaController);
        if (title.equals("-kappa.mp4"))
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.kappa));
        if (title.equals("-kappa2.mp4"))
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.kappa2));
        if (title.equals("-kappa3.mp4"))
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.kappa3));
        if (title.equals("-lamda1.mp4"))
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.lamba1));
        if (title.equals("-lamda2.mp4"))
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.lamba2));
        if (title.equals("-mi1.mp4"))
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.mi1));
        if (title.equals("-mi2.mp4"))
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.mi2));
        video.start();
    }
}