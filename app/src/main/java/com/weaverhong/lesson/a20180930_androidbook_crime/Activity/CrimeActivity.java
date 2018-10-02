package com.weaverhong.lesson.a20180930_androidbook_crime.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.weaverhong.lesson.a20180930_androidbook_crime.Fragment.CrimeFragment;
import com.weaverhong.lesson.a20180930_androidbook_crime.Model.Crime;
import com.weaverhong.lesson.a20180930_androidbook_crime.SingleFragmentActivity;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_ID =
            "com.weaverhong.android.bookcrime.crime_id";

    // newIntent发起跳转的行为仍然属于Activity下面
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
//        旧方法，这时Fragment获取Intent中内容并不是托管得到的
//        而是Fragment里直接通过UUID“抢夺”了Intent中的信息
//        return new CrimeFragment();

//        现在换用newInstance方法，Activity在创建Fragment的时候通过此方法
//        传入需要的crimeId这一参数
//        这里起名字叫newInstance理由是：
//        这个实例是由class生成的，且生成过程中给自己创建了一个bundle
//        而且还把传入的id存进了bundle
//        仿照这个过程，可以多写几种newInstance，有不同的参数传入，
//        从而构建更Bundle内容更丰富的Fragment，实现Fragment的复用
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
