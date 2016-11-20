package com.example.mrting.blah;

/**
 * Created by mrting on 5/11/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper{
    //Database Version
    private static final int DATABASE_VERSION=5;
    //Database Name
    private static final String DATABASE_NAME= "bandInfo11.db";
    //Contents table name
    private static final String table_Data= "data";
    private static final String table_drugs="drugs";
    private static final String table_symptoms="symptoms";
    //Shops Table Columns names
    private static final String KEY_TimeStamp="timeID";
    private static final String KEY_heartRate= "heartRate";
    private static final String KEY_skinTemperature="skinTemperature";
    private static final String KEY_quality="quality";
    private static final String KEY_GSR= "GSR";
    private static final String KEY_Date="date";
    private static final String KEY_stepCount="stepCount";

    //private static final String KEY_Date2="date";
    private static final String KEY_Drug="drug";
    private static final String KEY_Notes="notes";

    private static final String KEY_log= "log";


    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        //String CREATE_TABLE_INFO= "CREATE TABLE bandInfo" + "(" + KEY_TimeStamp + "INTEGER PRIMARY KEY" + KEY_heartRate + "INTEGER" + KEY_skinTemperature + "FLOAT";
        //db.execSQL(CREATE_TABLE_INFO);
        db.execSQL("CREATE TABLE data" + "(timeID integer primary key, heartRate text, skinTemperature text, quality text, GSR text, date text, stepCount text)");
        db.execSQL("CREATE TABLE drugs" + "(date text primary key, drug text, notes text)");
        db.execSQL("CREATE TABLE symptoms" + "(date text primary key, log text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + table_Data);
        db.execSQL("DROP TABLE IF EXISTS " + table_drugs);
        db.execSQL("DROP TABLE IF EXISTS " + table_symptoms);
        //Creating tables again
        onCreate(db);
    }
    public void addData(BandData data){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        //values.put(KEY_TimeStamp, data.getTimeID());
        values.put(KEY_heartRate, data.getHeartRate());
        values.put(KEY_skinTemperature, data.getSkinTemperature());
        values.put(KEY_quality, data.getQuality());
        values.put(KEY_GSR, data.getGSR());
        values.put(KEY_Date, data.getDate());
        values.put(KEY_stepCount, data.getStepCount());
        //inserting row
        db.insert("data", null, values); //shouldn't it be the table name, not the database name?
        //db.close(); //close database connection
    }
    public BandData getData(int id){
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor= db.query("bandInfo", new String[] {KEY_TimeStamp, KEY_heartRate, KEY_skinTemperature, KEY_quality, KEY_GSR, KEY_Date, KEY_stepCount}, KEY_TimeStamp + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor !=null)
            cursor.moveToFirst();
        BandData contact=  new BandData(cursor.getInt(0), cursor.getInt(1), cursor.getFloat(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5), cursor.getLong(6));
        //return
           return contact;
    }
    public ArrayList<BandData> getAllData(int iterations, String ID) {
        ArrayList<BandData> dataList = new ArrayList<BandData>();
        String selectQuery = /*"SELECT * FROM data";*/ "SELECT*FROM data ORDER BY ROWID DESC LIMIT 2000";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            //for(int i=0; i<iterations; i++){cursor.moveToPrevious();}
            do{
                BandData data= new BandData();
                if(ID.toLowerCase()=="all"){
                    data.setTimeID(Integer.parseInt(cursor.getString(0)));
                    data.setHeartRate(Integer.parseInt(cursor.getString(1)));
                    data.setSkinTemperature(Float.parseFloat(cursor.getString(2)));
                    data.setQuality(cursor.getString(3));
                    data.setGSR(Integer.parseInt(cursor.getString(4)));
                    data.setDate(cursor.getString(5));
                    data.setStepCount(Long.parseLong(cursor.getString(6)));
                    dataList.add(data);
                    iterations--;
                }
                if(ID.toLowerCase() == "heart" && Float.parseFloat(cursor.getString(2))==0 && Integer.parseInt(cursor.getString(4))==0) {
                    data.setTimeID(Integer.parseInt(cursor.getString(0)));
                    data.setHeartRate(Integer.parseInt(cursor.getString(1)));
                    data.setQuality(cursor.getString(3));
                    data.setDate(cursor.getString(5));
                    data.setStepCount(Long.parseLong(cursor.getString(6)));
                    dataList.add(data);
                    iterations--;
                }
                if(ID.toLowerCase() == "skintemp" && Float.parseFloat(cursor.getString(2))!=0){
                    data.setTimeID(Integer.parseInt(cursor.getString(0)));
                    data.setSkinTemperature(Float.parseFloat(cursor.getString(2)));
                    data.setDate(cursor.getString(5));
                    data.setStepCount(Long.parseLong(cursor.getString(6)));
                    dataList.add(data);
                    iterations--;
                }
                if(ID.toLowerCase()== "gsr" && Integer.parseInt(cursor.getString(4))!=0){
                    data.setTimeID(Integer.parseInt(cursor.getString(0)));
                    data.setGSR(Integer.parseInt(cursor.getString(4)));
                    data.setDate(cursor.getString(5));
                    data.setStepCount(Long.parseLong(cursor.getString(6)));
                    dataList.add(data);
                    iterations--;
                }
            }while(cursor.moveToNext() && iterations>0);
        }
        if(dataList.size()<iterations)
            return null;
        return dataList;
    }
    public ArrayList<String> getDataAsString(int iterations, String ID) {
        ArrayList<String> dataList = new ArrayList<String>();
        String selectQuery = "SELECT*FROM data ORDER BY ROWID DESC LIMIT 2000";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                BandData data= new BandData();
                if(ID.toLowerCase()=="all"){
                    data.setTimeID(Integer.parseInt(cursor.getString(0)));
                    data.setHeartRate(Integer.parseInt(cursor.getString(1)));
                    data.setSkinTemperature(Float.parseFloat(cursor.getString(2)));
                    data.setQuality(cursor.getString(3));
                    data.setGSR(Integer.parseInt(cursor.getString(4)));
                    data.setDate(cursor.getString(5));
                    data.setStepCount(Long.parseLong(cursor.getString(6)));
                    dataList.add(data.toString());
                    iterations--;
                }
                if(ID.toLowerCase() == "heart" && Float.parseFloat(cursor.getString(2))==0 && Integer.parseInt(cursor.getString(4))==0) {
                    data.setTimeID(Integer.parseInt(cursor.getString(0)));
                    data.setHeartRate(Integer.parseInt(cursor.getString(1)));
                    data.setQuality(cursor.getString(3));
                    data.setDate(cursor.getString(5));
                    data.setStepCount(Long.parseLong(cursor.getString(6)));
                    dataList.add(data.toHeartString());
                    iterations--;
                }
                if(ID.toLowerCase() == "skintemp" && Float.parseFloat(cursor.getString(2))!=0){
                    data.setTimeID(Integer.parseInt(cursor.getString(0)));
                    data.setSkinTemperature(Float.parseFloat(cursor.getString(2)));
                    data.setDate(cursor.getString(5));
                    data.setStepCount(Long.parseLong(cursor.getString(6)));
                    dataList.add(data.toSkinTempString());
                    iterations--;
                }
                if(ID.toLowerCase()== "gsr" && Integer.parseInt(cursor.getString(4))!=0){
                    data.setTimeID(Integer.parseInt(cursor.getString(0)));
                    data.setGSR(Integer.parseInt(cursor.getString(4)));
                    data.setDate(cursor.getString(5));
                    data.setStepCount(Long.parseLong(cursor.getString(6)));
                    dataList.add(data.toGSRString());
                    iterations--;
                }
            }while(cursor.moveToNext() && iterations>0);
        }
        return dataList;
    }
    public void deleteDataTable(){
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("DELETE FROM data");
        db.close();
    }
    public void deleteDrugTable(){
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("DELETE FROM drugs");
        db.close();
    }
    public void deleteNotes(){
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("DELETE FROM symptoms");
        db.close();
    }
    public void addDrug(Drug drug){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=  new ContentValues();

        values.put(KEY_Date, drug.getDate());
        values.put(KEY_Drug, drug.getDrugName());
        values.put(KEY_Notes, drug.getNotes());
        db.insert("drugs", null, values);
    }
    public Drug getDrug(int option, String search){
        SQLiteDatabase db= this.getReadableDatabase();
        String selectQuery= "SELECT * FROM drugs";
        Cursor cursor= db.rawQuery(selectQuery, null);
        if(cursor!=null){
            for(cursor.moveToFirst(); cursor.getString(option)!=search; cursor.moveToNext());
            Drug drug= new Drug(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            return drug;
        }
        return null;
    }
    public ArrayList getAllDrugsAsString(){
        SQLiteDatabase db= this.getReadableDatabase();
        String selectQuery= "SELECT*FROM drugs";
        ArrayList<String> dataList= new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToLast()){
            Drug drug= new Drug();
            do{
                drug.setDate(cursor.getString(0));
                drug.setDrugName(cursor.getString(1));
                drug.setNotes(cursor.getString(2));
                dataList.add(drug.toDrugString());
            }while(cursor.moveToPrevious());
        }
        return dataList;
    }

    public void addNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();

        values.put(KEY_Date, note.getDate());
        values.put(KEY_log, note.getNote());
        db.insert("symptoms", null, values);
    }
    public ArrayList getAllNotesAsString(){
        SQLiteDatabase db= this.getReadableDatabase();
        String selectQuery= "SELECT * FROM symptoms";
        ArrayList<String> dataList= new ArrayList<>();
        Cursor cursor= db.rawQuery(selectQuery, null);
        if(cursor.moveToLast()){
            Note note= new Note();
            do{
                note.setDate(cursor.getString(0));
                note.setNote(cursor.getString(1));
                dataList.add(note.toString());
            }while(cursor.moveToPrevious());
        }
        return dataList;
    }
}
