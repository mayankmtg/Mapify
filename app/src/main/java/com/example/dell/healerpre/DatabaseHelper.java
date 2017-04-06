package com.example.dell.healerpre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dell on 06-04-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "healerdata.db";
    public static final String TABLE_NAME = "Healers";
    public static final String COLUMN1 = "NAME";
    public static final String COLUMN2 = "LATITUDE";
    public static final String COLUMN3 = "LONGITUDE";
    public static final String COLUMN4 = "RATING";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN1 + " TEXT," +
                COLUMN2 + " REAL," +
                COLUMN3 + " REAL," +
                COLUMN4 + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
    public boolean insertData(String name, double lat, double lng, double rat){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN1, name);
        contentValues.put(COLUMN2, lat);
        contentValues.put(COLUMN3, lng);
        contentValues.put(COLUMN4, rat);
        long result= db.insert(TABLE_NAME, null, contentValues);
        if(result==-1){
            return false;
        }
        else{
            return true;
        }

    }

    public Cursor getData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+ TABLE_NAME, null);
        return res;
    }
}
