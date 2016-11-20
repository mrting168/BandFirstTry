package com.example.mrting.blah;

/**
 * Created by mrting on 5/11/2016.
 */
public class BandData {
    private int timeID;
    private int heartRate;
    private String quality;
    private float skinTemperature;
    private int GSR;
    private String time;
    private long stepCount;
    public BandData()
    {}
    public BandData(int timeID, int heartRate, float skinTemperature, String quality,  int GSR, String time, long stepCount)
    {
        this.timeID= timeID;
        this.heartRate= heartRate;
        this.skinTemperature= skinTemperature;
        this.GSR=GSR;
        this.quality= quality;
        this.time= time;
        this.stepCount= stepCount;
    }
    public void setQuality(String quality){this.quality= quality;}
    public void setHeartRate(int heartRate)
    {
        this.heartRate= heartRate;
    }
    public void setSkinTemperature(float skinTemperature)
    {
        this.skinTemperature= skinTemperature;
    }
    public void setTimeID(int timeID){
        this.timeID= timeID;
    }
    public void setGSR(int GSR){this.GSR=GSR;}
    public void setDate(String time){this.time= time;}
    public void setStepCount(long stepCount){this.stepCount=stepCount;}

    public int getHeartRate()
    {
        return this.heartRate;
    }
    public float getSkinTemperature()
    {
        return this.skinTemperature;
    }
    public int getTimeID(){
        return this.timeID;
    }
    public String getQuality(){return this.quality;}
    public int getGSR(){return this.GSR;}
    public String getDate(){return this.time;}
    public long getStepCount(){return this.stepCount;}
    public String getTimeIDAsString(){
        return this.time.substring(7,12);
    }

    public String toHeartString(){
        String heartString;
        heartString= "ID:"+ timeID + "|| HeartRate:" + heartRate + "\nQuality:" + quality + "\nTime: " + time + "\nSteps: " + stepCount;
        return heartString;
    }
    public String toSkinTempString(){
        String skinTempString;
        skinTempString= "ID:" + timeID + "|| Skin Temperature:" + skinTemperature + "\nTime: "+ time + "\nSteps: " + stepCount;
        return skinTempString;
    }
    public String toGSRString(){
        String GSRString;
        GSRString= "ID" + timeID + "|| GSR:" + GSR +"\nTime: " + time + "\nSteps" + stepCount;
        return GSRString;
    }
}
