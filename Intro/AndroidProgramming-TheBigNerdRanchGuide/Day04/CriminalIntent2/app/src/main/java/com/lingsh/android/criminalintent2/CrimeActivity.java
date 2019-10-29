package com.lingsh.android.criminalintent2;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class CrimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        // 使用FragmentManager管理fragment视图
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 获得activity_crime.xml中所设定的fragment容器(位置) 里面是否已经有fragment在队列中
        // 因为activity被销毁时, FragmentManager会将fragment队列保存下来
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        // 如果fragment队列中没有fragment
        if (fragment == null) {
            fragment = new CrimeFragment();
            // 创建并提交一个fragment事务(用于添加/移除/附加/替换队列中fragment)
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
