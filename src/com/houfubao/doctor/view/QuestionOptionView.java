package com.houfubao.doctor.view;


import com.houfubao.doctor.R;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.utils.QLog;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

	private static final String TAG = "QuestionOption";
	private OptionAdapter mAdapter = new OptionAdapter();
	private MyDataSetObserver myDataSetObserver = new MyDataSetObserver();
	private Handler mHandler;
	private static final int mResId1[] = {
			R.drawable.jiakao_practise_a_n_day,R.drawable.jiakao_practise_b_n_day,
			R.drawable.jiakao_practise_c_n_day,R.drawable.jiakao_practise_d_n_day,
			R.drawable.jiakao_practise_e_n_day,R.drawable.jiakao_practise_f_n_day
		};

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
	
	void setCallbackHandler(Handler handler) {
		mHandler = handler;
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
			if (v == null) {
				vh = new ViewHolder();
				v = (ViewGroup)LayoutInflater.from(getContext()).inflate(R.layout.question_option, null);
				v.setTag(vh);
				v.setOnClickListener(new onOptionClicked());
			}else {
				vh = (ViewHolder)v.getTag();
			}
			
			vh.object1 = pos;
			
			//Text
			String string = mOptions[pos];
			TextView textView = (TextView)(vh.get(v, R.id.option_text));
			textView.setText(string);

			//Image
			ImageView imageView = (ImageView)(vh.get(v, R.id.option_image));
			imageView.setImageResource(mResId1[pos]);
			return v;
		}
	}
	
	class onOptionClicked implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			ViewHolder vh = (ViewHolder)v.getTag();
			int pos = (Integer)vh.object1;
			ImageView imageView = (ImageView)(vh.get(v, R.id.option_image));
			
			mHandler.obtainMessage(QuestionMainView.MSG_ID_ON_OPTION_CLICKED, pos).sendToTarget();
		}
		
	}
}
