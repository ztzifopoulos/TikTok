package com.example.tiktok.model;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class MyIntentService extends IntentService {
    public MyIntentService() {
        super("MyIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent){
        runBroker();
    }
    private void runBroker(){
        String[] broString = {"1234"};
        Broker.main(broString);
    }
}