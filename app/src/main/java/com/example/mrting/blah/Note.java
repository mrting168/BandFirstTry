package com.example.mrting.blah;

/**
 * Created by mrting on 6/6/2016.
 */
public class Note {
    private String date;
    private String note;

    public Note(){}
    public Note(String date, String note){
        this.date=date;
        this.note=note;
    }

    public String getDate(){
        return this.date;
    }
    public String getNote(){
        return this.note;
    }

    public void setDate(String date){
        this.date=date;
    }
    public void setNote(String note){
        this.note=note;
    }
    public String toString(){
        String string;
        string= "Time Stamp: " + this.date + "\n Note: " + this.note;
        return string;
    }
}
