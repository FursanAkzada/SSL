package com.example.food;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String pname, String pic, String panel, String lamp, String pole, String date, String device, String latitude, String longitude, String location, byte[] image, String others){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO CRUD VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, pname);
        statement.bindString(2, pic);
        statement.bindString(3, panel);
        statement.bindString(4, lamp);
        statement.bindString(5, pole);
        statement.bindString(6, date);
        statement.bindString(7, device);
        statement.bindString(8, latitude);
        statement.bindString(9, longitude);
        statement.bindString(10, location);
        statement.bindBlob(11, image);
        statement.bindString(12, others);

        statement.executeInsert();
    }

    public void updateData(String pname, String pic, String panel, String lamp, String pole, String others, int id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE CRUD SET pname = ?, pic = ?, panel = ?, lamp = ?, pole = ?, others = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, pname);
        statement.bindString(2, pic);
        statement.bindString(3, panel);
        statement.bindString(4, lamp);
        statement.bindString(5, pole);
        statement.bindString(6, others);
        statement.bindString(7, String.valueOf(id));

        statement.execute();
        database.close();
    }

    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM CRUD WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
