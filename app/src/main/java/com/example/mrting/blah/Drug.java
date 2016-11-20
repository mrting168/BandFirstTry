package com.example.mrting.blah;

/**
 * Created by mrting on 6/1/2016.
 */
public class Drug {
    private String date;
    private String drugName;
    private String notes;

    public Drug(){}
    public Drug(String date, String drugName, String notes){
        this.drugName=drugName;
        this.date=date;
        this.notes=notes;
    }

    public void setNotes(String notes){
        this.notes=notes;
    }
    public void setDate(String date){
        this.date= date;
    }
    public void setDrugName(String drugName){
        this.drugName=drugName;
    }

    public String getDate(){
        return this.date;
    }
    public String getDrugName(){
        return this.drugName;
    }
    public String getNotes(){
        return this.notes;
    }

    public String toDrugString(){
        String string;
        string= "TimeStamp:" + this.date + " \nDrug Name:" + this.drugName+ " \nNotes: "+ this.notes;
        return string;
    }
}
