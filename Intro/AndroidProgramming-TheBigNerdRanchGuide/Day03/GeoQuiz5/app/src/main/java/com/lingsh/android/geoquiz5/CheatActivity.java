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
    private TextView mCurrentApiLevelView;
    private TextView mRemainCheatCntView;

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

        // 移动到前面 是因为updateCheatView() 需要使用到这些组件
        initBindActivityView();

        // 用于解决 旋转CheatActivity清除作弊痕迹
        if (savedInstanceState != null) {
            mAnswerIsShown = savedInstanceState.getBoolean(ANSWER_HAD_SHOWN);
            if (mAnswerIsShown) {
                updateCheatView();
            }
        }

        // 获取父Activity传入的参数
        // 作弊: 该问题的答案
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        // 剩余作弊次数显示
        mCheatCount = getIntent().getIntExtra(EXTRA_COUNT_CHEAT, 0);

        // 组件的操作
        // 显示答案按钮 点击监听
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnswerIsShown = true;
                updateCheatView();

            }
        });

        mCurrentApiLevelView.setText(mCurrentApiLevelStr);

        // 页面的更新 (一次)
        updateRemainCheatTime();
    }

    private void initBindActivityView() {
        mAnswerTextView = findViewById(R.id.answer_text_view);
        mRemainCheatCntView = findViewById(R.id.remain_cheat_cnt_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mCurrentApiLevelView = findViewById(R.id.current_api_level_view);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ANSWER_HAD_SHOWN, mAnswerIsShown);
    }

    private void updateCheatView() {
        // 显示答案
        showQuestionAnswer();

        // 隐藏按钮
        hideCheatButton();

        // 更新剩余作弊次数
        updateRemainCheatTime();
    }

    private void showQuestionAnswer() {
        if (mAnswerIsTrue) {
            mAnswerTextView.setText(R.string.true_button);
        } else {
            mAnswerTextView.setText(R.string.false_button);
        }
        setAnswerShownResult(true);
    }

    private void updateRemainCheatTime() {
        int remain_cnt = mCheatCountMax - mCheatCount;

        // 是否已经作弊 显示过答案
        if (mAnswerIsShown) {
            mRemainCheatCntView.setText("剩余作弊次数: " + (--remain_cnt));
        } else {
            mRemainCheatCntView.setText("剩余作弊次数: " + remain_cnt);
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
            mShowAnswerButton.post(
                    // 先等按钮视图正确加载
                    new Runnable() {
                        @Override
                        public void run() {
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
                        }
                    }
            );
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
