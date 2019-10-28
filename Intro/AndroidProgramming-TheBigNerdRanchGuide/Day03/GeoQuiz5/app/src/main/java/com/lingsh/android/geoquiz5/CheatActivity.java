package com.lingsh.android.geoquiz5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static String TAG = "CheatActivity";

    public static String mCurrentApiLevelStr = "API Level " + Build.VERSION.SDK_INT;

    private static final String EXTRA_ANSWER_IS_TRUE = "com.lingsh.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.lingsh.android.geoquiz.answer_shown";
    private static final String EXTRA_COUNT_CHEAT = "com.lingsh.android.geoquiz.count_cheat";

    private static final String ANSWER_HAD_SHOWN = "CheatBehaviourHave";
    /**
     * 作弊问题标识 字符串TAG
     */
    public static final String CHEAT_QUESTIONS_LIST = "CheatQuestionsList";
    public static final String COUNT_CHEAT_RECORD = "CheatCountRecord";

    private boolean mAnswerIsTrue;
    private boolean mAnswerIsShown;
    private int mCheatCountMax = 3;
    private int mCheatCount = 0;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mCurrentApiLevel;
    private TextView mRemainCheatCnt;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int mCheatCount) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_COUNT_CHEAT, mCheatCount);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        // 移动到前面 是因为updateCheatView() 需要使用到该变量
        mAnswerTextView = findViewById(R.id.answer_text_view);
        mRemainCheatCnt = findViewById(R.id.remain_cheat_cnt_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);

        // 用于解决 旋转CheatActivity清除作弊痕迹
        if (savedInstanceState != null) {
            mAnswerIsShown = savedInstanceState.getBoolean(ANSWER_HAD_SHOWN);
            if (mAnswerIsShown) {
                updateCheatView();
            }
        }

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnswerIsShown = true;
                updateCheatView();
            }
        });

        mCurrentApiLevel = findViewById(R.id.current_api_level_view);
        mCurrentApiLevel.setText(mCurrentApiLevelStr);

        // 剩余作弊次数显示
        mCheatCount = getIntent().getIntExtra(EXTRA_COUNT_CHEAT, 0);
        updateRemainCheatTime();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ANSWER_HAD_SHOWN, mAnswerIsShown);
    }

    private void updateCheatView() {
        if (mAnswerIsTrue) {
            mAnswerTextView.setText(R.string.true_button);
        } else {
            mAnswerTextView.setText(R.string.false_button);
        }
        setAnswerShownResult(true);

        hideCheatButton();

        updateRemainCheatTime();
    }

    private void updateRemainCheatTime() {
        int remain_cnt = mCheatCountMax - mCheatCount;
        if (mAnswerIsShown) {
            mRemainCheatCnt.setText("剩余作弊次数: " + (--remain_cnt));
        } else {
            mRemainCheatCnt.setText("剩余作弊次数: " + remain_cnt);
        }
        Log.d(TAG, "updateRemainCheatTime mCheatCnt=" + mCheatCount + " remain_cnt=" + remain_cnt);

        if (remain_cnt <= 0) {
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        }
    }

    private void hideCheatButton() {
        Log.d(TAG, "hideCheatButton() called");
        // 添加功能: 显示答案后隐藏按钮
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 添加一个 API 21级(Lollipop)版本的代码
            // 在隐藏按钮的同时显示一段特效动画
            int cx = mShowAnswerButton.getWidth() / 2;
            int cy = mShowAnswerButton.getHeight() / 2;
            float radius = mShowAnswerButton.getWidth();
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    mShowAnswerButton, cx, cy, radius, 0
            );
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            });
            animator.start();

        } else {
            // 只是隐藏按钮
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Log.d(TAG, "result: " + EXTRA_ANSWER_SHOWN + " : " + isAnswerShown);
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }
}
