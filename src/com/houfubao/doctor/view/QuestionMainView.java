package com.houfubao.doctor.view;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.houfubao.doctor.R;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.Question;
import com.houfubao.doctor.logic.online.QuestionManager;
import com.houfubao.doctor.logic.utils.QLog;

public class QuestionMainView extends RelativeLayout  {
	public final static String TAG = "QuestionMainView";

	private int mOrder;

	private TextView mTitle;
	private LinearLayout mOptionView;
	private TextView mAnalysis;

	private Handler mHandler;

	private Question mQuestion;
	private QuestionManager mQuestionManager;
	private QuestionManager.QuestionResultCallback mCallback = new MyQuestionManager();
	private WeakReference<OptionClickCallback> mCallbackReference;
	private onOptionClicked mOptionClickListener = new onOptionClicked();

	private static final int mResId1[] = {
			R.drawable.jiakao_practise_a_n_day,
			R.drawable.jiakao_practise_b_n_day,
			R.drawable.jiakao_practise_c_n_day,
			R.drawable.jiakao_practise_d_n_day,
			R.drawable.jiakao_practise_e_n_day,
			R.drawable.jiakao_practise_f_n_day };

	private static final int mPressedId[] = {
			R.drawable.jiakao_practise_a_s_day,
			R.drawable.jiakao_practise_b_s_day,
			R.drawable.jiakao_practise_c_s_day,
			R.drawable.jiakao_practise_d_s_day,
			R.drawable.jiakao_practise_e_s_day,
			R.drawable.jiakao_practise_f_s_day };

	private static final String Answer[] = { "A", "B", "C", "D", "E", "F" };
	
	static public interface OptionClickCallback {
		void onOptionClick(boolean isRight, String userAnswer);
	}
	
	public QuestionMainView(Context context, OptionClickCallback callback) {
		super(context, null, 0);
		mQuestionManager = DoctorState.getInstance().getQuestionManager();
		mCallbackReference = new WeakReference<OptionClickCallback>(callback);
		mHandler = new MyHandler(this);
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
		mTitle = (TextView) rootView.findViewById(R.id.question_title);
		mOptionView = (LinearLayout) rootView.findViewById(R.id.question_option);
		mAnalysis = (TextView) findViewById(R.id.question_anlaysis);
		mAnalysis.setVisibility(View.INVISIBLE);

	}

	public void setQuestion(Question question) {
		mQuestion = question;
		updateView(question);
	}

	public void setQuestionOrder(int order) {
		QLog.i(TAG, "setQuestionOrder: " + order);
		mOrder = order;
		mQuestionManager.addCallback(mCallback);
		mQuestionManager.getQuestion(mCallback, order);
	}

	private void updateView(Question question) {

		// mSingleOrMulti
		VerticalImageSpan imgSpan = new VerticalImageSpan(getContext(),
				R.drawable.jiakao_practise_danxuanti_day);
		SpannableString spanString = new SpannableString("icon");
		spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mTitle.setText(spanString);
		mTitle.append(mQuestion.getTitle());

		// 答案选项
		String[] options = mQuestion.getOption().split(DoctorConst.DOUBLE_SEPRATOR);
		mOptionView.removeAllViews();
		int pos = 0;
		for (String string : options) {
			ViewGroup v = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.question_option_item, null);
			v.setOnClickListener(mOptionClickListener);
			v.setTag(pos);

			// Text
			TextView textView = (TextView) (v.findViewById(R.id.option_item_text));
			textView.setText(string);

			// 图片
			StateListDrawable stalistDrawable = new StateListDrawable();
			ImageView imageView = (ImageView) (v.findViewById(R.id.option_item_image));
			int pressed = android.R.attr.state_pressed;
			stalistDrawable.addState(new int[] {pressed}, getResources().getDrawable(mPressedId[pos]));
			stalistDrawable.addState(new int[] {-pressed}, getResources().getDrawable(mResId1[pos]));
			imageView.setBackground(stalistDrawable);
			mOptionView.addView(v);
			pos++;
		}

		// 正文
		mAnalysis.setText(mQuestion.getAnalysis());
	}

	
	public int getOrder() {
		return mOrder;
	}


	class onOptionClicked implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int pos = (Integer) v.getTag();

			int count = mOptionView.getChildCount();
			for (int i = 0; i < count; i++) {
				ViewGroup vg = (ViewGroup) mOptionView.getChildAt(i);
				vg.setClickable(false);

				String selectedAnswer = Answer[i];
				boolean isRight = selectedAnswer.equals(mQuestion.getAnswer());
				if (isRight) {
					ImageView imageView = (ImageView) (vg.findViewById(R.id.option_item_image));
					imageView.setImageResource(R.drawable.ic_right);
				}
			}

			ImageView imageView = (ImageView) (v.findViewById(R.id.option_item_image));

			String userAnswer = Answer[pos];
			boolean isRight = userAnswer.equals(mQuestion.getAnswer());
			imageView.setImageResource(isRight ? R.drawable.ic_right : R.drawable.ic_error);

			OptionClickCallback callback = mCallbackReference.get();
			if (callback != null) {
				callback.onOptionClick(isRight, userAnswer);
			}
		}

	}

	// *
	static class MyHandler extends Handler {
		WeakReference<QuestionMainView> mQVReference;

		public MyHandler(QuestionMainView qmv) {
			mQVReference = new WeakReference<QuestionMainView>(qmv);
		}

		@Override
		public void handleMessage(Message msg) {
			QuestionMainView qView = mQVReference.get();
			if (qView == null) {
				return;
			}

			switch (msg.what) {
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	/**
	 * 获取题目的回调
	 *
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
