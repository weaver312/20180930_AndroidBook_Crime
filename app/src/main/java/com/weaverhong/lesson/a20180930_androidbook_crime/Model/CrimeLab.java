package com.weaverhong.lesson.a20180930_androidbook_crime.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weaverhong.lesson.a20180930_androidbook_crime.CrimeBaseHelper;
import com.weaverhong.lesson.a20180930_androidbook_crime.CrimeCursorWrapper;
import com.weaverhong.lesson.a20180930_androidbook_crime.Model.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    // 仍属于Model层
    // 用自我托管+单例，实现暂时存储CrimeList
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    // 加入SQLite的持久层方法
    private CrimeLab(Context context) {
        // 注意这里context上下文的用法，在16章会重新阐述
        // context是Application级别的上下文，比Activity还要高。
        // 因为CrimeLab是静态的单例，意味着它在创建后，会一直存在。
        // 如果用Activity作为CrimeLab的引用对象，则可能会被垃圾回收器清理。
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                // 这里获取可写的数据库，系统内部会有三步。
                // 一是打开db文件，不存在则创建
                // 二是如果是首次打开，调用BaseHelper的onCreate()方法
                // 三是如果已创建过，就尝试调用onUpgrade()进行升级
                .getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public int delCrime(UUID id) {
        return mDatabase.delete(CrimeTable.NAME, "UUID = ?",new String[] { id.toString() });
    }

    public List<Crime> getCrimes() {
        // return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
        // return null;
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        // 注意这里不是用SQL-where语句，而是update方法，为了防止SQL注入
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns=null, selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        // return cursor;
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }
}
