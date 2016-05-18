package com.houfubao.doctor.view;


import java.lang.ref.WeakReference;

import com.houfubao.doctor.R;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.utils.QLog;
import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QuestionOptionView extends LinearLayout {

	private static final int mResId1[] = {
		R.drawable.jiakao_practise_a_n_day,R.drawable.jiakao_practise_b_n_day,
		R.drawable.jiakao_practise_c_n_day,R.drawable.jiakao_practise_d_n_day,
		R.drawable.jiakao_practise_e_n_day,R.drawable.jiakao_practise_f_n_day
	};

	private static final int mPressedId[] = {
		R.drawable.jiakao_practise_a_s_day,R.drawable.jiakao_practise_b_s_day,
		R.drawable.jiakao_practise_c_s_day,R.drawable.jiakao_practise_d_s_day,
		R.drawable.jiakao_practise_e_s_day,R.drawable.jiakao_practise_f_s_day
		};
	
	private static final String Answer[] = {"A","B","C","D","E","F"};

	interface OptionClickCallback {
		void onOptionClick(boolean isRight);
	}
	
	private static final String TAG = "QuestionOption";
	private OptionAdapter mAdapter = new OptionAdapter();
	private MyDataSetObserver myDataSetObserver = new MyDataSetObserver();
	private String mAnswer;
	private WeakReference<OptionClickCallback> mCallbackReference;

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
	
	void setOptionCallback(OptionClickCallback callback) {
		mCallbackReference = new WeakReference<QuestionOptionView.OptionClickCallback>(callback);
	}
	
	void setOption(String options, String answer) {
		mAdapter.updateOptions(options);
		mAnswer = answer;
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
			ViewHolder vh = null;

			vh = new ViewHolder();
			vh.object1 = pos;
			v = (ViewGroup)LayoutInflater.from(getContext()).inflate(R.layout.question_option_item, null);
			v.setTag(vh);
			v.setOnClickListener(new onOptionClicked());
			
			//Text
			String string = mOptions[pos];
			TextView textView = (TextView)(vh.get(v, R.id.option_item_text));
			textView.setText(string);
			
			//图片
			StateListDrawable stalistDrawable = new StateListDrawable();
			ImageView imageView = (ImageView)(vh.get(v, R.id.option_item_image));
			int pressed = android.R.attr.state_pressed;
			stalistDrawable.addState(new int []{pressed}, getResources().getDrawable(mPressedId[pos]));
			stalistDrawable.addState(new int []{-pressed}, getResources().getDrawable(mResId1[pos]));
			imageView.setBackground(stalistDrawable);
				
			return v;
		}
	}
	
	class onOptionClicked implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			ViewHolder vh = (ViewHolder)v.getTag();

			int count = getChildCount();
			for(int i = 0; i<count; i++) {
				ViewGroup vg = (ViewGroup)getChildAt(i);
				vg.setClickable(false);
				
				String selectedAnswer = Answer[i];
				boolean isRight = selectedAnswer.equals(mAnswer);
				if (isRight) {
					ImageView imageView = (ImageView)(vg.findViewById(R.id.option_item_image));	
					imageView.setImageResource(R.drawable.ic_right);
				}
			}
			
			ImageView imageView = (ImageView)(vh.get(v, R.id.option_item_image));			
			int pos = (Integer)vh.object1;
			String selectedAnswer = Answer[pos];
			boolean isRight = selectedAnswer.equals(mAnswer);
			imageView.setImageResource(isRight? R.drawable.ic_right : R.drawable.ic_error);
			
			OptionClickCallback callback = mCallbackReference.get();
			if (callback != null) {
				callback.onOptionClick(isRight);
			}
		}
		
	}
	

}
