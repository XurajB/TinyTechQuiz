package com.iandrobot.tinytechquiz.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by surajbhattarai on 9/16/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    //table
    public static final String TABLE_QUESTIONS = "questions";
    //fields
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_OPTION_1 = "option_1";
    public static final String COLUMN_OPTION_2 = "option_2";
    public static final String COLUMN_OPTION_3 = "option_3";
    public static final String COLUMN_OPTION_4 = "option_4";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_ANSWER_INDEX = "answer_index";

    //queries
    private static final String DATABASE_CREATE = "create table "
            + TABLE_QUESTIONS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_QUESTION
            + " text not null, " + COLUMN_OPTION_1
            + " text not null, " + COLUMN_OPTION_2
            + " text not null, " + COLUMN_OPTION_3
            + " text not null, " + COLUMN_OPTION_4
            + " text not null, " + COLUMN_CATEGORY
            + " text not null, " + COLUMN_DIFFICULTY
            + " text not null, " + COLUMN_ANSWER_INDEX
            + " integer not null);";


    private static final String DATABASE_NAME = "tinytechquiz.db";
    private static final int DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        onCreate(db);
    }
}
