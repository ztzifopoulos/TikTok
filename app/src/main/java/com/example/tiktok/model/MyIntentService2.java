package com.example.tiktok.model;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import static com.example.tiktok.model.MainActivity.CHANNEL_NAME_EXTRA;

public class MyIntentService2 extends IntentService {
    public MyIntentService2() {
        super("MyIntentService2");
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(Intent intent) {
        String channelName = intent.getStringExtra(CHANNEL_NAME_EXTRA);
        runCon(channelName);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void runCon(String channelName){
        String[] conString ={"0", channelName};
        AppNode.main(conString);
    }
}