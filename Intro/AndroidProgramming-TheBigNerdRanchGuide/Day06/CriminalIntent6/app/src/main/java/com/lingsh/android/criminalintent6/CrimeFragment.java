package com.lingsh.android.criminalintent6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    // 2109-11-01 星期五
    private String inFormatDateYMDE = "yyyy-MM-dd EE";
    // 17:17:17
    private String inFormatTimehms = "hh:mm:ss";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        // setArguments()方法必须在fragment创建后, 添加给activity之前完成
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) Objects.requireNonNull(getArguments()).getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 实例化fragment视图的布局
        // 直接调用LayoutInflater.inflate(...)方法生成fragment视图
        // 1.布局的资源id 2.视图的父视图 3.布局生成器是否将生成的视图添加到父视图(false, 表示以代码的方式手动添加)
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        // 同一绑定组件
        mTitleField = view.findViewById(R.id.crime_title);
        mDateButton = view.findViewById(R.id.crime_date);
        mTimeButton = view.findViewById(R.id.crime_time);
        mSolvedCheckBox = view.findViewById(R.id.crime_solved);

        // 对组件进行操作
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 故意留下空白
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 故意留下空白
            }
        });

        updateDate();
        // mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                // DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                // 接收DatePickerFragment返回的日期数据
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                // 传入FragmentManager参数 系统会自动创建并提交事务
                dialog.show(Objects.requireNonNull(fragmentManager), DIALOG_DATE);
            }
        });

        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(Objects.requireNonNull(fragmentManager), DIALOG_TIME);
            }
        });

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) Objects.requireNonNull(data).getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }

        if (requestCode == REQUEST_TIME) {
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(time);
            updateTime();
        }
    }

    private void updateDate() {
        Date date = mCrime.getDate();
        String inFormatDateYMDE = this.inFormatDateYMDE;
        CharSequence format = DateFormat.format(inFormatDateYMDE, date);
        System.out.printf("date:%s\tformat:%s\tresult:%s\n", date, inFormatDateYMDE, format);

        mDateButton.setText(format);
    }

    /**
     * TODO: fix bugs
     * E/AndroidRuntime: FATAL EXCEPTION: main
     * Process: com.lingsh.android.criminalintent6, PID: 14279
     * java.lang.NullPointerException: Attempt to invoke virtual method 'long java.util.Date.getTime()' on a null object reference
     * at java.util.Calendar.setTime(Calendar.java:1197)
     * at android.text.format.DateFormat.format(DateFormat.java:355)
     * at com.lingsh.android.criminalintent6.CrimeFragment.updateDate(CrimeFragment.java:155)
     * at com.lingsh.android.criminalintent6.CrimeFragment.onActivityResult(CrimeFragment.java:143)
     * at com.lingsh.android.criminalintent6.TimePickerFragment.sendResult(TimePickerFragment.java:96)
     * at com.lingsh.android.criminalintent6.TimePickerFragment.access$100(TimePickerFragment.java:23)
     * at com.lingsh.android.criminalintent6.TimePickerFragment$1.onClick(TimePickerFragment.java:82)
     * at androidx.appcompat.app.AlertController$ButtonHandler.handleMessage(AlertController.java:167)
     * at android.os.Handler.dispatchMessage(Handler.java:102)
     * at android.os.Looper.loop(Looper.java:150)
     * at android.app.ActivityThread.main(ActivityThread.java:5659)
     * at java.lang.reflect.Method.invoke(Native Method)
     * at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:822)
     * at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:712)
     */
    private void updateTime() {
        Date date = mCrime.getDate();
        CharSequence format = DateFormat.format(inFormatTimehms, date);
        System.out.printf("date:%s\tformat:%s\tresult:%s\n", date, inFormatTimehms, format);

        mTimeButton.setText(format);
    }
}
