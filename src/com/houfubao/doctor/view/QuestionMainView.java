package com.houfubao.doctor.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.houfubao.doctor.R;
import com.houfubao.doctor.logic.online.Question;

public class QuestionMainView extends RelativeLayout {
	Question mQuestion;
	ImageView mSingleOrMulti;
	TextView mTitle;
	QuestionOptionView mOptionView;
	TextView mAnalysis;
	
	public QuestionMainView(Context context) {
		super(context, null, 0);
		initVew(context);
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
	
	private void updateView(Question question) {
		//mSingleOrMulti
		mTitle.setText(mQuestion.getTitle());
		mAnalysis.setText(mQuestion.getAnalysis());
	}
	  
    public int getPos() {
    	if (mQuestion != null)
    		return mQuestion.getOrder();
    	else {
			return -1;
		}
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
	
	

}
