package com.lingsh.android.criminalintent6;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    // case: "Friday, Jul 22, 2016"
    private String inFormat = "EE, MMM dd, yyyy";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        // 此处使用resume而不是start
        // 是因为如果前面的activity是透明的, 那么本页面可能只是在暂停状态, 不会触发start
        // 而resume是返回页面必然触发
        // 实现从详细页面返回后直接刷新列表信息
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            // 关联RecyclerView和Adapter
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            // 因为ViewPager可以左右滑动 修改的记录可能会有多条 而position只能记住点击的列表项
            // notifyDataSetChanged()方法会通知RecyclerView刷新全部的可见列表项
            mCrimeAdapter.notifyDataSetChanged();
        }
    }

    public abstract class BaseCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected Crime mCrime;
        protected TextView mTitleTextView;
        protected TextView mDateTextView;

        public BaseCrimeHolder(LayoutInflater inflater, ViewGroup parent, int resource) {
            super(inflater.inflate(resource, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            initialComponents();
        }

        /**
         * 让子类初始化自己独有的组件
         */
        protected abstract void initialComponents();

        /**
         * 组件内容绑定 让子类自己实现
         */
        protected abstract void bind(Crime crime);

        @Override
        public void onClick(View v) {
            // 从Fragment调用Activity
            // Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    private class CrimeHolder extends BaseCrimeHolder {

        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.list_item_crime);
        }

        @Override
        protected void initialComponents() {
            // 一次性 实例化相关组件
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        @Override
        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(DateFormat.format(inFormat, mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }
    }

    private class CrimeRequiredPoliceHolder extends BaseCrimeHolder {
        private Button mCrimeRequiredPoliceButton;

        public CrimeRequiredPoliceHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.list_item_crime_required_police);
        }

        @Override
        protected void initialComponents() {
            mCrimeRequiredPoliceButton = itemView.findViewById(R.id.crime_required_police_button);

            mCrimeRequiredPoliceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), R.string.called_police, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(DateFormat.format(inFormat, mCrime.getDate()));
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public BaseCrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            // 根据getItemViewType获得类型分别返回不同视图
            if (viewType == 0) {
                return new CrimeHolder(layoutInflater, parent);
            } else {
                return new CrimeRequiredPoliceHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            // 绑定相应的Crime实例
            Crime crime = mCrimes.get(position);

            // 根据得到的不同item view type 绑定不同的Fragment视图
            if (getItemViewType(position) == 0) {
                ((CrimeHolder) holder).bind(crime);
            } else {
                ((CrimeRequiredPoliceHolder) holder).bind(crime);
            }
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            // 0:不需要 1:需要
            return crime.getRequiresPolice();
        }
    }
}
