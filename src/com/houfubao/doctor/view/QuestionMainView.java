package com.houfubao.doctor.view;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.houfubao.doctor.R;
import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.Question;
import com.houfubao.doctor.logic.online.QuestionManager;

public class QuestionMainView extends RelativeLayout {
	public final static String TAG = "QuestionMainView";
	
	int mOrder;

	ImageView mSingleOrMulti;
	TextView mTitle;
	QuestionOptionView mOptionView;
	TextView mAnalysis;
	
	Question mQuestion;
	QuestionManager mQuestionManager;
	QuestionManager.QuestionResultCallback mCallback = new MyQuestionManager();
	
	public QuestionMainView(Context context) {
		super(context, null, 0);
		initVew(context);
		mQuestionManager = DoctorState.getInstance().getQuestionManager();

	}
	
	@Override
	protected void onAttachedToWindow() {

		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		mQuestionManager.removeCallback(mCallback);
		super.onDetachedFromWindow();
	}

	void initVew(Context context) {
		View rootView = LayoutInflater.from(context).inflate(R.layout.question_main, this);
		mSingleOrMulti = (ImageView)rootView.findViewById(R.id.question_single_or_multi);
		mTitle = (TextView)rootView.findViewById(R.id.question_title);
		mOptionView = (QuestionOptionView)rootView.findViewById(R.id.question_option);
		mAnalysis = (TextView)findViewById(R.id.question_anlaysis);	
	}
	
	public void setQuestion(Question question) {
		mQuestion = question;
		updateView(question);
	}
	
	public void setQuestionOrder(int order) {
		Log.i(TAG, "setQuestionOrder: " + order);
		mOrder = order;
		mQuestionManager.addCallback(mCallback);
		mQuestionManager.getQuestion(mCallback, order);
	}
	
	private void updateView(Question question) {
		//mSingleOrMulti
		mTitle.setText(mQuestion.getTitle());
		mAnalysis.setText(mQuestion.getAnalysis());
	}
	  
    public int getOrder() {
    	return mOrder;
    }

	/**
	 * 答案选项 
	 */
	class AnswerOptionAdapter extends BaseAdapter {
		String []mAnswerOption;
		
		void updateAnswerOption(String [] option) {
			mAnswerOption = option;
		}
		
		@Override
		public int getCount() {
			if (mAnswerOption != null)
				return mAnswerOption.length;
			return 0;
		}

		@Override
		public Object getItem(int pos) {
			if (mAnswerOption != null) {
				return mAnswerOption[pos];
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View v, ViewGroup group) {
			TextView option = null;
			if (v == null) {
				option = new TextView(getContext());
			}
			
			option.setText(mAnswerOption[pos]);			
			return option;
		}
	}
	
	
	/**
	 * 获取题目的回调
	 * @author zhuweisong
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
			setQuestion(q);
			super.onGetQuestionSucceed(pos, q);
		}
	}
	
}
