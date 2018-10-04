package com.weaverhong.lesson.a20180930_androidbook_crime.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weaverhong.lesson.a20180930_androidbook_crime.SQLite.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key AUTOINCREMENT, " +
                CrimeTable.Cols.UUID + "," +
                CrimeTable.Cols.TITLE + "," +
                CrimeTable.Cols.DATE + "," +
                CrimeTable.Cols.SOLVED + ", " +
                CrimeTable.Cols.SUSPECT +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 这里的空格不要忘打，否则会报错
        db.execSQL("CREATE TABLE " + CrimeTable.NAME);
    }
}
