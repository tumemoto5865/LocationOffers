package com.android.udl.locationoffers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gerard on 13/03/17.
 */

public class MessagesSQLiteHelper extends SQLiteOpenHelper{

    String sqlCreate = "CREATE TABLE Messages " +
            "(_id INTEGER PRIMARY KEY, " +
            "title TEXT, " +
            "description TEXT, " +
            "imageid INTEGER )";

    public MessagesSQLiteHelper (Context context, String name,
                                 SQLiteDatabase.CursorFactory factory,
                                 int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Messages");
        db.execSQL(sqlCreate);
    }
}