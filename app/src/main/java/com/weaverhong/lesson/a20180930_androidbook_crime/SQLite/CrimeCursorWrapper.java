package com.weaverhong.lesson.a20180930_androidbook_crime.SQLite;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.weaverhong.lesson.a20180930_androidbook_crime.Model.Crime;
import com.weaverhong.lesson.a20180930_androidbook_crime.SQLite.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

// 其实这个Wrapper很简单，自己写一个也就是这样，就是把ORM变成了bean
// 但鉴于“不要重复造轮子”的原则，从CursorWrapper之后扩展就可以
public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        // 注意这里的Date，仍然是java.util.Date
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
