package com.houfubao.doctor.view;


import com.houfubao.doctor.R;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.utils.QLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QuestionOptionView extends LinearLayout {

	private static final String TAG = "QuestionOption";
	private  int mWidth = 0;

	OptionAdapter mAdapter = new OptionAdapter();
	
	
	private MyDataSetObserver myDataSetObserver = new MyDataSetObserver();

	public QuestionOptionView(Context context) {
		this(context, null, 0);
	}
	
	public QuestionOptionView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public QuestionOptionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(LinearLayout.VERTICAL);
		mAdapter.registerDataSetObserver(myDataSetObserver);
	}
	
	void setOption(String options) {
		mAdapter.updateOptions(options);
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
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

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
}
