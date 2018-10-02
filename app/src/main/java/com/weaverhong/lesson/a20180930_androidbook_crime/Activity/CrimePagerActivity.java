package com.weaverhong.lesson.a20180930_androidbook_crime.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.weaverhong.lesson.a20180930_androidbook_crime.Fragment.CrimeFragment;
import com.weaverhong.lesson.a20180930_androidbook_crime.Model.Crime;
import com.weaverhong.lesson.a20180930_androidbook_crime.Model.CrimeLab;
import com.weaverhong.lesson.a20180930_androidbook_crime.R;

import java.util.List;
import java.util.UUID;

// 给CrimeActivity添加左右翻页功能。弃用旧的CrimeActivity及对应视图
public class CrimePagerActivity extends AppCompatActivity{

    private static final String EXTRA_CRIME_ID =
            "com.weaverhong.android.bookcrime.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 传统的创建视图的方法：
        setContentView(R.layout.activity_crime_pager);
        // 纯代码创建视图的方法：
        // ViewPager viewPager = new ViewPager(this);
        // setContentView(viewPager);
        // 相比而言，使用XML可以分离View和Controller，这是好处之一
        // XML配置比代码更灵活，除了它是静态的之外，几乎没有缺点
        // 代码创建的唯一好处是，如果这个Activity布局了只需要一个简答的Widget，代码会方便一丝丝

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            // 这个适配器感觉写得更清楚，只有两个方法：获取Pager总数量、获取某一下标对应的内容实体
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
               // 手动调整至选中的crimeId的项目中
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
