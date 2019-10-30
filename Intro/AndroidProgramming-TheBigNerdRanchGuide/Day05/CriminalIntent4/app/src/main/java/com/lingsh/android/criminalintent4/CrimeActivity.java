package com.lingsh.android.criminalintent4;


import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    // 自产自销
    private static final String EXTRA_CRIME_ID = "com.lingsh.android.criminalintent4.crime_id";

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);

        intent.putExtra(EXTRA_CRIME_ID, crimeId);

        return intent;

    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
