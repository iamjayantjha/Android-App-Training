package com.androidapptraining;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mydatabase";
    public static final String TABLE_NAME_1 = "mytable1";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME_1+" (ID INTEGER PRIMARY KEY, NAME TEXT, MARKS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion){
            case 1:
                db.execSQL("ALTER TABLE "+TABLE_NAME_1+" ADD COLUMN SECTION TEXT");
                break;
            case 2:
                db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_1);
                break;
        }

    }

    public boolean insertData(String id, String name, String marks){
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("INSERT INTO "+TABLE_NAME_1+" VALUES ("+id+", '"+name+"', "+marks+")");
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID",id);
        contentValues.put("NAME",name);
        contentValues.put("MARKS",marks);
        long result = db.insert(TABLE_NAME_1,null,contentValues);
        //db.close();
        return result != -1;
    }

    public boolean updateData(String id, String name, String marks){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID",id);
        contentValues.put("NAME",name);
        contentValues.put("MARKS",marks);
        int result = db.update(TABLE_NAME_1,contentValues,"ID =?",new String[]{id});
        return result > 0;
    }

    public void deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_1,"ID =?",new String[]{id});
    }

    public Cursor readData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "ID LIKE ?";
        String[] selectionArgs = {id};
        String[] projection = {"ID","NAME","MARKS"};
        return db.query(TABLE_NAME_1,projection,selection,selectionArgs,null,null,null);
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME_1,null);
    }
}
