package com.weaverhong.lesson.a20180930_androidbook_crime.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.weaverhong.lesson.a20180930_androidbook_crime.R;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "com.weaverhong.android.bookcrime.date";
    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // 初始化方法，很重要，核心信息展示都在这里了
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        // 来自java.util的日历方法，要先get实例才能使用
        Calendar calendar = Calendar.getInstance();
        // 这里把CrimeFragment的日期传过来，
        // 以后要考虑怎么把DatePicker的日期传回给CrimeFragment
        // 猜测：通过getActivityOnResult
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity())
                // 下面这个null的作用：
                // root Optional view to be the parent of the generated hierarchy.
                .inflate(R.layout.dialog_date, null);
        // 也可以直接用代码创建DialogDate：
        // DatePicker datePicker = new DatePicker(getActivity());

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        // 这个创建AlertDialog实例的方法叫流接口
        // 首先传入Activity Context（Activity里用this，fragment里用getActivity() ）
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                // 调用两个方法，标题（内容）和监听器（种类和监听器实例）
                // 现在监听器按钮有三种，positive, negative, neutral
                .setTitle(R.string.date_picker_title)
                // .setPositiveButton(android.R.string.ok, null)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year,month,day).getTime();
                                // 需要传递两个参数，一个用来告诉父级Activity/Fragment自己已OK
                                // 另一个传递信息
                                sendResult(Activity.RESULT_OK, date);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        // 如果不存在目标，则什么也不做
        // 而这里的TargetFragment是由Target设定的（参见CrimeFragment大约106行附近）
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
