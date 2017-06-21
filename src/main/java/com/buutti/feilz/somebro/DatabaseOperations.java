package com.buutti.feilz.somebro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOperations extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="POSTS";
    public static final String TABLE_NAME = "POST";
    public static final String COL_1 = "POST_ID";
    public static final String COL_2 = "PICTURE_PATH";
    public static final String COL_3 = "POST";

    public static final int database_version=1;
    public String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME+"(POST_ID INTEGER PRIMARY KEY AUTOINCREMENT, PICTURE_PATH TEXT," +
            "POST TEXT)";

    public DatabaseOperations(Context c) {
        super(c, DATABASE_NAME, null, database_version);
        Log.d("DatabaseOperations","Created tables");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
    }

    public boolean putInfo(String picturepath, String post){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2,picturepath);
        cv.put(COL_3, post);
        long res = db.insert(TABLE_NAME,null, cv);
        return res != -1;
    }

    public boolean putText(String post){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_3, post);
        long res = db.insert(TABLE_NAME,null, cv);
        return res != -1;
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }
    public void updateInfo(String id,String picturepath, String post) {
        SQLiteDatabase sq = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, id);
        cv.put(COL_2, picturepath);
        cv.put(COL_3, post);
        sq.update(TABLE_NAME, cv, "ID=?", new String[]{id});
    }
}
