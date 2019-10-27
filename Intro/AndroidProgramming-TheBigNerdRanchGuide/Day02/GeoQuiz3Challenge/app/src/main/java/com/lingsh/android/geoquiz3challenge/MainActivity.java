package com.lingsh.android.geoquiz3challenge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_gd, true),
            new Question(R.string.question_animal, false),
            new Question(R.string.question_hn, false),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_sh, false)
    };

    private int mQuestionNumber = mQuestionBank.length;
    private int mHaveAnswerNumber = 0;
    private int mHaveAnswerTrueNumber = 0;
    private boolean[] mHaveAnswer = new boolean[mQuestionNumber];

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
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
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton = findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                updateQuestion();
            }
        });
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

        // 更新选项按钮
        updateOptButton();

        // 回答完了
        if (mHaveAnswerNumber >= mQuestionNumber) {
            double score = (double) mHaveAnswerTrueNumber / mHaveAnswerNumber;
            String toast_score = "Congratulation! You have answer: " +
                    mHaveAnswerTrueNumber + " questions.\nGet Score: " + (score * 100) + "%";

            Toast.makeText(MainActivity.this, toast_score, Toast.LENGTH_LONG).show();
        }
    }

    private void updateOptButton() {
        // 答过的题目不能再次回答
        Button mTrueButton = findViewById(R.id.true_button);
        this.mTrueButton = mTrueButton;
        mTrueButton.setEnabled(!mHaveAnswer[mCurrentIndex]);

        Button mFalseButton = findViewById(R.id.false_button);
        this.mFalseButton = mFalseButton;
        mFalseButton.setEnabled(!mHaveAnswer[mCurrentIndex]);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean questionAnswer = mQuestionBank[mCurrentIndex].isAnswerTrue();

        // 设置该题为答过
        mHaveAnswer[mCurrentIndex] = true;
        mHaveAnswerNumber++;

        int toastTexId;
        if (questionAnswer == userPressedTrue) {
            toastTexId = R.string.correct_toast;
            mHaveAnswerTrueNumber++;
        } else {
            toastTexId = R.string.incorrect_toast;
        }

        updateOptButton();

        Toast.makeText(MainActivity.this, toastTexId, Toast.LENGTH_SHORT).show();
    }
}
