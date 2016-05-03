package com.houfubao.doctor.view;


import com.houfubao.doctor.logic.utils.QLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class QuestionOptionView extends LinearLayout {

	private static final String TAG = "QuestionOption";
	private  int mWidth = 0;

	BaseAdapter mAdapter;
	private MyDataSetObserver myDataSetObserver = new MyDataSetObserver();

	public QuestionOptionView(Context context) {
		this(context, null, 0);
	}
	
	public QuestionOptionView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public QuestionOptionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	void setItemWidth(int width) {
		mWidth = width;
	}

	void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(myDataSetObserver);
	}

	@Override
	protected void onAttachedToWindow() {
		QLog.i(TAG, "onAttachedToWindow x ");
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		mAdapter.unregisterDataSetObserver(myDataSetObserver);
		super.onDetachedFromWindow();
	}

	class MyDataSetObserver extends android.database.DataSetObserver {

		@Override
		public void onChanged() {
			QLog.i(TAG, "onChanged ");
			notifyDataSetChanged();
			super.onChanged();
		}

		@Override
		public void onInvalidated() {
			QLog.i(TAG, "onInvalidated ");
			super.onInvalidated();
		}
	}

	public void notifyDataSetChanged() {
		int width  = LayoutParams.WRAP_CONTENT;
		if (mWidth > 0){
			width = mWidth;
		}
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		// 备份现有的child
		int childcount = getChildCount();
		View[] v = new View[childcount];
		for (int i = 0; i < childcount; i++) {
			v[i] = getChildAt(i);
		}

		removeAllViews();

		int size = mAdapter.getCount();
		for (int i = 0; i < size; i++) {
			View newView = null;
			if (childcount > i) {
				newView = mAdapter.getView(i, v[i], this);
			} else {
				newView = mAdapter.getView(i, null, this);
			}
			
			addView(newView, lp);
		}
	}

}
