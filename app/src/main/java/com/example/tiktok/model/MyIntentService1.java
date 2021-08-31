package com.example.tiktok.model;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MyIntentService1 extends IntentService {
    public MyIntentService1() {
        super("MyIntentService2");
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(Intent intent) {
        runPub1();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void runPub1(){
        String[] pubString = {"1", "kappa"};
        AppNode.main(pubString);
    }
}