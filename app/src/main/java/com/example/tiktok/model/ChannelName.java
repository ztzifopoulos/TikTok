package com.example.tiktok.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ChannelName implements Serializable
{
    private String channelName;
    private ArrayList<String> hashtagsPublished = new ArrayList<String>();
    private HashMap<String, ArrayList<Value>> userVideoFilesMap = new HashMap<String, ArrayList<Value>>(); //allagi se value sketo!!! kai String Arraylist ????

    public ChannelName(){ }

    public ChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public String getChannelName(){

        return channelName;
    }

    public void setChannelName(String channelName){

        this.channelName = channelName;
    }

    public void setHashtagsPublished(ArrayList<String> hashtagsPublished){
        this.hashtagsPublished = hashtagsPublished;
    }

    public void setUserVideoFilesMap(HashMap<String, ArrayList<Value>> userVideoFilesMap){
        this.userVideoFilesMap = userVideoFilesMap;
    }


    public void addHashtagPublished(ArrayList<String> hashtags){

        hashtagsPublished.addAll(hashtags);
    }
    public void removeHashtagPublished(String hashtag){
        hashtagsPublished.remove(hashtag);
    }

    public void addUserVideoFile(Value value){
        Iterator it = userVideoFilesMap.entrySet().iterator();
        while (it.hasNext())
        {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            ArrayList<Value> vArr = (ArrayList<Value>)pair.getValue();
            if (!vArr.contains(value)) {
                vArr.add(value);
                pair.setValue(vArr);
                this.addHashtagPublished(value.getVideoHashtags());
            }
        }
    }
}