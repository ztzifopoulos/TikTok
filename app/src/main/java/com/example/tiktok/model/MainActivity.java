package com.example.tiktok.model;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.tiktok.R;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    public static final String VIDEOS_PATH_EXTRA = "videos_dir";
    public static final String CHANNEL_NAME_EXTRA = "channel_name";
    public static final String VIDEO_NAME = "video_name";
    String viDir = "C:\\Users\\Zacharias\\AndroidStudioProjects\\TikTok\\app\\src\\main\\assets\\PublishersVideos";
    private boolean listAssetFiles(String path) {
        String [] list;
        try {
            list = getAssets().list(path);
            if (list.length > 0) {
                //folder
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file))
                        return false;
                    else {
                        if(file.contains("mp4"))
                            videoList.add(file);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    MyRecyclerViewAdapter adapter;
    public static ArrayList<String> videoList = new ArrayList<String>();
    EditText editText;
    RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listAssetFiles("PublishersVideos");
        adapter = new MyRecyclerViewAdapter(this, videoList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        Intent intent = new Intent();
        Intent intent1 = new Intent();
        Intent intent2 = new Intent();
        intent2.putExtra(CHANNEL_NAME_EXTRA, "kappa");
        intent.setClass(this, MyIntentService.class);
        intent1.setClass(this, MyIntentService1.class);
        intent2.setClass(this, MyIntentService2.class);
        startService(intent);
        startService(intent1);
        startService(intent2);
    }
    @Override
    public void onItemClick(View view, int position){
        Intent intent = new Intent(this, VideoPlayActivity.class);
        intent.putExtra(VIDEO_NAME, adapter.getItem(position));
        startActivity(intent);
    }
}