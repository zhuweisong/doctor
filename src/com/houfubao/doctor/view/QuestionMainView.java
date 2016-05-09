package com.houfubao.doctor.view;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.houfubao.doctor.R;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.Question;
import com.houfubao.doctor.logic.online.QuestionManager;

public class QuestionMainView extends RelativeLayout {
	public final static String TAG = "QuestionMainView";
	
	int mOrder;

	ImageView mSingleOrMulti;
	TextView mTitle;
	QuestionOptionView mOptionView;
	OptionAdapter mOptionAdapter;
	TextView mAnalysis;
	
	Question mQuestion;
	QuestionManager mQuestionManager;
	QuestionManager.QuestionResultCallback mCallback = new MyQuestionManager();
	
	public QuestionMainView(Context context) {
		super(context, null, 0);
		mQuestionManager = DoctorState.getInstance().getQuestionManager();

		initVew(context);
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
		View rootView = LayoutInflater.from(context).inflate(R.layout.question_main_view, this);
		mSingleOrMulti = (ImageView)rootView.findViewById(R.id.question_single_or_multi);
		mTitle = (TextView)rootView.findViewById(R.id.question_title);
		mOptionView = (QuestionOptionView)rootView.findViewById(R.id.question_option);
		mAnalysis = (TextView)findViewById(R.id.question_anlaysis);	
		
		mOptionAdapter = new OptionAdapter();
		mOptionView.setAdapter(mOptionAdapter);
	}
	
	public void setQuestion(Question question) {
		mQuestion = question;
		mOptionAdapter.updateOptions(mQuestion.getOption());
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
	class OptionAdapter extends BaseAdapter {
		String []mOptions;
		public OptionAdapter() {
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mOptions.length;
		}
		
		public void updateOptions(String options) {
			mOptions = options.split(DoctorConst.DOUBLE_SEPRATOR);
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			if (mOptions != null) {
				return mOptions[pos];
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int pos, View v, ViewGroup group) {
			
			if (v == null) {
				TextView temp = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.question_option, null);
				 v = temp;
				 v.setOnClickListener(new onOptionClicked());
			}
			String string = mOptions[pos];
			((TextView)v).setText(string);
			return v;
		}
	}
	
	class onOptionClicked implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			
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
