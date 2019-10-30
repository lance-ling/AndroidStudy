package com.lingsh.android.criminalintent3;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private List<Crime> mCrimes;

    // 私有构造方法 单例做法
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

        // 自测
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime # " + i);
            crime.setSolved((i & 1) == 0);
            // 每10条记录就有一条需要报警
            crime.setRequiresPolice((i % 10) == 0 ? 1 : 0);
            mCrimes.add(crime);
        }
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID uuid) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(uuid)) {
                return crime;
            }
        }

        return null;
    }
}
