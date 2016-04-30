package com.houfubao.doctor;

import java.util.List;

import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.Question;
import com.houfubao.doctor.logic.online.QuestionManager;

import android.app.Activity;
import android.os.Bundle;

public class OrderTraining extends Activity {
	public final static String Question_Pos = "QUESTION_POS";
	QuestionManager mQuestion;
	QuestionManager.QuestionResultCallback mCallback = new MyQuestionManager();
	int mPos = 1;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mQuestion = DoctorState.getInstance().getQuestionManager();
		if (savedInstanceState != null) {
			mPos = savedInstanceState.getInt(Question_Pos, 0);			
		}
	}

    
	@Override
	protected void onDestroy() {
		mQuestion = null;
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		mQuestion.addCallback(mCallback);
		mQuestion.getQuestion(mCallback, mPos);
		super.onStart();
	}

	@Override
	protected void onStop() {
		mQuestion.removeCallback(mCallback);
		super.onStop();
	}


	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(Question_Pos, mPos);
		super.onSaveInstanceState(outState);
	}


	/**
	 * 获取题目的回调
	 * @author zhuweisong
	 *
	 */
	class MyQuestionManager extends QuestionManager.QuestionResultCallback {

		@Override
		public void onGetQuestionSucceed(int from, int count, List<Question> list) {
			
			super.onGetQuestionSucceed(from, count, list);
		}

		@Override
		public void onGetQuestionFailed(int from, int count) {
			
			super.onGetQuestionFailed(from, count);
		}

		@Override
		public void onGetQuestionSucceed(int pos, Question q) {

			super.onGetQuestionSucceed(pos, q);
		}
	}

}
