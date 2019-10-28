package com.lingsh.android.geoquiz4challenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    /**
     * 作弊问题标识 字符串TAG
     */
    private static final String CHEAT_QUESTIONS_LIST = "CheatQuestionsList";

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_gd, true),
            new Question(R.string.question_animal, false),
            new Question(R.string.question_hn, false),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_sh, false)
    };
    private int mQuestionSize = mQuestionBank.length;

    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    /**
     * 作弊题目名单
     */
    private boolean[] mCheatQuestionList = new boolean[mQuestionSize];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);

            // 用于解决 旋转清除mIsCheater变量 / 跳转问题清除作弊记录
            boolean[] mCheatQuestionList = savedInstanceState.getBooleanArray(CHEAT_QUESTIONS_LIST);
            if (mCheatQuestionList != null) {
                this.mCheatQuestionList = mCheatQuestionList;
            }
        }

        mQuestionTextView = findViewById(R.id.question_text_view);
        updateQuestion();
        // 给TextView添加监听器 用户点击文字区域时, 可以跳转到下一题
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);

            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsCheater = false;

                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton = findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsCheater = false;

                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start CheatActivity
                // 使用intent.putExtra() 父Activity 传参给子Activity
                // Intent intent = new Intent(MainActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
                // startActivityForResult() 需要子Activity 传参给父Activity
                // startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // 在Stop之前调用 用于保存一份状态
        Log.d(TAG, "onSaveInstanceState() called");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        // Bundle 本身是个k-v键值对 但只能存储:1,基本数据类型 2,实现序列化接口的对象

        outState.putBooleanArray(CHEAT_QUESTIONS_LIST, mCheatQuestionList);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean questionAnswer = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int toastTexId;
        if (mIsCheater || mCheatQuestionList[mCurrentIndex]) {
            toastTexId = R.string.judgment_toast;
            mCheatQuestionList[mCurrentIndex] = true;
        } else {
            if (questionAnswer == userPressedTrue) {
                toastTexId = R.string.correct_toast;
            } else {
                toastTexId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(MainActivity.this, toastTexId, Toast.LENGTH_SHORT).show();
    }
}
