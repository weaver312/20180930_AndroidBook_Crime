package com.weaverhong.lesson.a20180930_androidbook_crime.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.weaverhong.lesson.a20180930_androidbook_crime.R;

public class DatePickerFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 这个创建AlertDialog实例的方法叫流接口
        // 首先传入Activity Context（Activity里用this，fragment里用getActivity() ）
        return new AlertDialog.Builder(getActivity())
                // 调用两个方法，标题（内容）和监听器（种类和监听器实例）
                // 现在监听器按钮有三种，positive, negative, neutral
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
