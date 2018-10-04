package com.weaverhong.lesson.a20180930_androidbook_crime.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab_WithoutSQLite {
    // 仍属于Model层
    // 用自我托管+单例，实现暂时存储CrimeList
    private static CrimeLab_WithoutSQLite sCrimeLab;

    private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    // 加入SQLite的持久层方法
    // private CrimeLab(Context context) {
    //     mContext = context.getApplicationContext();
    //     mDatabase = new CrimeBaseHelper(mContext)
    //             .getWritableDatabase();
    //     mCrimes = new ArrayList<>();
    // }

    // 旧的方法，没有持久层
    private CrimeLab_WithoutSQLite(Context context) {
        mCrimes = new ArrayList<>();
        // 这里本来应该是去持久层获取/等待控制器去持久层获取，但是
        // 先随机生成100条，看一下显示效果
        for (int i = 0; i < 3; i ++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i%2 == 0);
            mCrimes.add(crime);
        }
    }

    public static CrimeLab_WithoutSQLite get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab_WithoutSQLite(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
        // 加入添加功能后，就没有必要生成一百条记录了，可以自己生成了
    }

    public void delCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                mCrimes.remove(crime);
                return;
            }
        }
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }
}
