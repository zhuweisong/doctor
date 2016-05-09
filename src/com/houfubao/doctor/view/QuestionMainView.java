package com.houfubao.doctor.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
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
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.jiakao_practise_danxuanti_day);  
        ImageSpan imgSpan = new ImageSpan(b);  
        SpannableString spanString = new SpannableString("icon");  
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
        mTitle.setText(spanString);  
        mTitle.append(mQuestion.getTitle());

        //答案选项
        mOptionView.setOption(mQuestion.getOption());
        
        //正文
		mAnalysis.setText(mQuestion.getAnalysis());
		
	}
	  
    public int getOrder() {
    	return mOrder;
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
