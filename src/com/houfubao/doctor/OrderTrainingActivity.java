package com.houfubao.doctor;

import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.Question;
import com.houfubao.doctor.logic.online.QuestionManager;
import com.houfubao.doctor.logic.utils.SimplePool;
import com.houfubao.doctor.view.QuestionMainView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class OrderTrainingActivity extends Activity {
	public final static String Question_Pos = "QUESTION_POS";

	int mPos = 1;
	QuestionMainView questionMainView;
	
	ViewPager mPager;
	MyQuestionPageAdapter mPageAdapter;
	
	QuestionManager mQuestion;
	MyQuestionManagerCallback mCallback;
		 
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_activity);
		ViewGroup group = (ViewGroup)findViewById(R.id.question_container);
		mPager = (ViewPager)findViewById(R.id.question_viewpager);
		mPageAdapter = new MyQuestionPageAdapter();
		mPager.setAdapter(mPageAdapter);
				
		mCallback = new MyQuestionManagerCallback();
		mQuestion = DoctorState.getInstance().getQuestionManager();
		mQuestion.addCallback(mCallback);
		mQuestion.getQuestionCount(mCallback, -1);
		if (savedInstanceState != null) {
			mPos = savedInstanceState.getInt(Question_Pos, 1);
		}
	}
  
    
	@Override
	protected void onDestroy() {
		mQuestion = null;
		super.onDestroy();
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	protected void onStop() {
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


	class MyQuestionPageAdapter extends PagerAdapter {
		//数据
		int mTotal;
		SparseArray<Question> mQuestions = new SparseArray<Question>();
		
		//View
		SparseArray<QuestionMainView> mView = new SparseArray<QuestionMainView>();
		SimplePool<QuestionMainView> mViewPool = new SimplePool<QuestionMainView>(6);

		
		@Override
		public int getCount() {
			return mTotal;
		}

		public void setQuestionCount(int count) {
			mTotal = count;
			notifyDataSetChanged();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			QuestionMainView view = mView.get(position);
			mView.remove(position);
			mViewPool.release(view);
			((ViewPager) container).removeView(view);
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			QuestionMainView view = mViewPool.acquire();
			if (view == null) {
				view = new QuestionMainView(getBaseContext());
			}
			view.setQuestionOrder(position+1);
			container.addView(view);
			return view;
		}
	}
	

	/**
	 * 获取题目的回调
	 * @author zhuweisong
	 */
	class MyQuestionManagerCallback extends QuestionManager.QuestionResultCallback {

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
			questionMainView.setQuestion(q);
			super.onGetQuestionSucceed(pos, q);
		}

		@Override
		public void onGetQuestionCountSucceed(int chapterId, int count) {
			mPageAdapter.setQuestionCount(count);
			super.onGetQuestionCountSucceed(chapterId, count);
		}

		@Override
		public void onGetQuestionCountFailed(int chapterId) {
			// TODO Auto-generated method stub
			super.onGetQuestionCountFailed(chapterId);
		}
		
		
	}
	

	

}
