package com.weaverhong.lesson.a20180930_androidbook_crime.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.print.PrinterId;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.weaverhong.lesson.a20180930_androidbook_crime.Bitmap.PictureUtils;
import com.weaverhong.lesson.a20180930_androidbook_crime.Model.Crime;
import com.weaverhong.lesson.a20180930_androidbook_crime.Model.CrimeLab;
import com.weaverhong.lesson.a20180930_androidbook_crime.R;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mDeleteButton;
    private CrimeLab mCrimeLab;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 最简单的方法，没有信息传递，父级Activity直接启动了一个Fragment
        // Fragment也不尝试获取一些Activity/Intent中的信息
        // mCrime = new Crime();
        //
        // 老方法：
        // 获取从CrimeListFragment调取的Activity的Intent中传入的信息
        // 可以看到比较暴力，本Fragment直接取Activity.getIntent()然后从里面取Extra
        // 这里Fragment直接取Activity不太合适，因为Fragment失去了可复用性
        // 调用这个Fragment的Activity不一定包含Fragment需要的数据，比如crimeId
        // UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        // mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        //
        // 新方法：
        // 通过Argument组件实现从下至上，从Fragment到Activity的信息获取
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        // 访问持久层，获得所需图像
        // 注意这里传递的是mCrime，可以尽量避免对上层的修改，只要在下面持久层改改改就凑活能用了
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        // mDateButton.setEnabled(false);
        // 新的行为，这本来就是一个fragment，现在通过这个fragment调起另一个fragment
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                // 直接创建一个DatePicker：
                // DatePickerFragment dialog = new DatePickerFragment();
                // 通过newInstance传入需要的值的创建：
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                // 注意这里不能单纯用 this 了，这里是Listener内部，this指的是Listener而非Fragment
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mCrimeLab = CrimeLab.get(getActivity());

        mDeleteButton = (Button) v.findViewById(R.id.crime_delete);
        mDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCrimeLab.delCrime(mCrime.getId());
                getActivity().finish();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐式Intent构造：
                // 要执行的操作，SEND表示发送邮件
                Intent i = new Intent(Intent.ACTION_SEND);
                // 数据类型，可以是html、mpeg等
                i.setType("text/plain");
                // 置入Extras
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));

                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        // pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // 检查是否有联系人应用，因为如果没有的话应用会崩溃
        // PackageManager类知道设备上安装的所有组件和Activity
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            // 没有对应应用，就会把按钮调回不可用模式
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        // 隐式Intent。控制器在这里像一个十字路口，交汇了各个资源
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 保证mPhotoFile存在且拿到了相机
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        // 开启照相按钮
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 告诉相机，我的URI是这个，你照完相直接写到这个URI里
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.weaverhong.lesson.a20180930_androidbook_crime.fileprovider",
                        mPhotoFile);
                // 把告诉的话放进Intent
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                // 找到一个默认的相机Activity
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                // 给相机写图片的权限，这个很重要
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                // 开启相机，并希望返回一个照片
                startActivityForResult(captureImage, REQUEST_PHOTO);
                // OK，接下来的任务就都在onActivityResult()方法里面了
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // resultCode就是那个有三种的：OK、Negative、Neutral
        // 私以为可以在request之后再判断resultCode
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // 这个 RequestCode 很有用，如果CrimeFragment发起了多个子Fragment并都要求返回内容
        // 这时就需要用requestCode判断是谁发回了结果
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            // 对持久层进行修改，保证改后的日期能传递到首页CrimeListFragment中
            updateDate();
        }

        // 新添加，对联系人隐式Intent返回数据的处理
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // 表示我只要获得联系人名字就可以
            // 这里用的太简单了，更复杂的用法参考developer.android.com
            String[] queryfields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // 用cursor才能获取需要的数据，这里倒是很像SQLite里的操作
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryfields, null, null, null);
            try {
                // 反复检查是否获得了结果
                if (c.getCount() == 0) {
                    return;
                }

                // 拉出第一条作为返回
                // 哈哈哈这里和我用Hibernate时候一样，虽然查的是一个值，但一
                // 返回就是一个List，然后我只好get(index=0)来取出结果
                c.moveToFirst();
                // 这里第一个结果下标也是零
                // 看来天下ORM是一家，所以干啥要这么多轮子
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }

        // 新添加，对PHOTO返回来的数据处理
        if (requestCode == REQUEST_PHOTO && data !=null) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.weaverhong.lesson.a20180930_androidbook_crime.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return "REPORT: " + suspect;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
