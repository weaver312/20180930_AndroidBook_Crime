package com.weaverhong.lesson.a20180930_androidbook_crime.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weaverhong.lesson.a20180930_androidbook_crime.Activity.CrimeActivity;
import com.weaverhong.lesson.a20180930_androidbook_crime.Activity.CrimePagerActivity;
import com.weaverhong.lesson.a20180930_androidbook_crime.Model.Crime;
import com.weaverhong.lesson.a20180930_androidbook_crime.Model.CrimeLab;
import com.weaverhong.lesson.a20180930_androidbook_crime.R;

import java.util.List;

// Fragment其实就是Activity，只是它在初始化时与Activity略有差别
// 需要用inflater自己填充视图，指定父级Activity，故多了两个参数：LayoutInflater和ViewGroup，
// 而activity是自动填充了。
public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 如果不设置为true的话，菜单栏就不会显示任何的按钮
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }

    private void updateUI() {
        // 加载model/持久层/数据层。记得传入getActivity()作为上下文，这样才能得到正确的数据
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (crimes.size() == 0) {
            view.findViewById(R.id.crime_listhint).setVisibility(View.VISIBLE);
            view.findViewById(R.id.crime_recycler_view).setVisibility(View.INVISIBLE);
            return;
        } else {
            view.findViewById(R.id.crime_recycler_view).setVisibility(View.VISIBLE);
            view.findViewById(R.id.crime_listhint).setVisibility(View.INVISIBLE);
        }
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            // 加入SQLite后，更新要有这个新的步骤，把新的crimes更新到Adapter，
            // 然后Adapter传递给ListView中
            // 因为在大约57行调用的crimeLab.getCrimes经过了修改，使用了CursorWrapper
            // 从SQLite里取数据。
            // 而原先的CrimeLab则是
            mAdapter.setCrimes(crimes);

            // 这个方法不够高效，可以定位然后刷新指定位置的数据
            mAdapter.notifyDataSetChanged();
        }

        // 因为我是控制层，所以我要把数据传给视图层。这里Adapter就是视图层的适配。
        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);

        updateSubtitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                // 如果点击新建项目的按键
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                // 因为是从Activity/Fragment启动Activity，所以用newIntent而非newInstance
                // 如果是启动Fragment，用newInstance
                Intent intent = CrimePagerActivity
                        .newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                // 默认不处理
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    // 一个私有View
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            // 这个itemView是ViewHolder的一个内置类变量，它相当于一个虚无的View，而且这个View是
            // 本ViewHolder里最高级别的，相当于一个底层View容器。给它可以设置监听器，或者从它里面找里面包含的
            // 子Layout或组件
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
           // Toast.makeText(getActivity(), mCrime.getTitle() + "clicked!", Toast.LENGTH_SHORT).show();
           // Intent intent = new Intent(getActivity(), CrimeActivity.class);
           //
           // Intent的创建需要目标Activity、发起者提供一些信息这两者合作才能创建，
           // 所以intent的创建方法newIntent一般在目标Activity里面，但使用时传入Id等必须值
           // Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
           // 注意 Intent 的发起这时由Fragment类的内置方法startActivity发起
           //
           // 用ViewPager代替旧的
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    // Adapter兼有View和Activity的部分特点，但我觉得更像View
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 适配器第一个方法：初始化
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            // 适配器第二个方法：对每个Holder做自定义行为
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            // 适配器第三个方法：返回list的总数
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }
}
