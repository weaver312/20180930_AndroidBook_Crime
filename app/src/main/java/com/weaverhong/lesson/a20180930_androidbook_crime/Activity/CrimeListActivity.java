package com.weaverhong.lesson.a20180930_androidbook_crime.Activity;

import android.support.v4.app.Fragment;

import com.weaverhong.lesson.a20180930_androidbook_crime.Fragment.CrimeListFragment;
import com.weaverhong.lesson.a20180930_androidbook_crime.SingleFragmentActivity;

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
