package com.houfubao.doctor.view;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
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

	private String mUserAnswer;
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
		void onOptionClick(int pos, boolean isRight, String userAnswer);
	}
	
	public QuestionMainView(Context context, OptionClickCallback callback) {
		super(context, null, 0);
		mQuestionManager = DoctorState.getInstance().getQuestionManager();
		mCallbackReference = new WeakReference<OptionClickCallback>(callback);
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

	void setQuestion(Question question) {
		mQuestion = question;
		updateView(question);
	}

	public void setQuestionOrder(int order) {
		QLog.i(TAG, "setQuestionOrder: " + order);
		mOrder = order;
		mQuestionManager.addCallback(mCallback);
		mQuestionManager.getQuestion(mCallback, order);
	}
	
	public void setUserAnswer(String answer) {
		mUserAnswer = answer;
	}

	private void updateView(Question question) {
		boolean isFinishedTheQuestion = (mUserAnswer != null);
		
		QLog.i(TAG, "updateView " + question.getOrder());
		// mSingleOrMulti
		int iconRes = question.isMultiChoice()? R.drawable.question_multi_choice : R.drawable.question_single_choice;
		VerticalImageSpan imgSpan = new VerticalImageSpan(getContext(),iconRes);
		SpannableString spanString = new SpannableString("icon");
		spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mTitle.setText(spanString);
		mTitle.append(question.getTitle());

		// 答案选项
		String[] options = mQuestion.getOption().split(DoctorConst.DOUBLE_SEPRATOR);
		mOptionView.removeAllViews();
		String rightAnswer = mQuestion.getAnswer();
		int optionPos = 0;
		for (String current : options) {
			ViewGroup vg = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.question_option_item, null);
			vg.setOnClickListener(mOptionClickListener);
			vg.setTag(optionPos);

			// Text
			TextView textView = (TextView) (vg.findViewById(R.id.option_item_text));
			textView.setText(current);

			// 图片
			if (isFinishedTheQuestion) { //用户刚才已经做过的题目
				String currentAnswer = Answer[optionPos];
				setOptionChoicedStatus(vg, currentAnswer, rightAnswer);
			}
			else {
				//没有做过的题
				StateListDrawable stalistDrawable = new StateListDrawable();
				int pressed = android.R.attr.state_pressed;
				stalistDrawable.addState(new int[] {pressed}, getResources().getDrawable(mPressedId[optionPos]));
				stalistDrawable.addState(new int[] {-pressed}, getResources().getDrawable(mResId1[optionPos]));
				ImageView imageView = (ImageView) (vg.findViewById(R.id.option_item_image));
				imageView.setImageDrawable(stalistDrawable);				
			}

			mOptionView.addView(vg);
			optionPos++;
		}

		// 正文
		mAnalysis.setText(mQuestion.getAnalysis());
		mAnalysis.setVisibility(isFinishedTheQuestion? View.VISIBLE : View.INVISIBLE);
	}

	
	public int getOrder() {
		return mOrder;
	}
	
	/**
	 * 当用户已经做了此题目后，设置其X和勾的状态
	 */
	void setOptionChoicedStatus(ViewGroup vg, String current, String rightAnswer) {
		ImageView imageView = (ImageView) (vg.findViewById(R.id.option_item_image));
		vg.setClickable(false);

		boolean isRight = current.equals(rightAnswer);
		if (isRight) {
			//当前答案是正确答案,打上勾
			imageView.setImageResource(R.drawable.jiakao_practise_true_day);
		}
		else {
			//用户回答不是正确答案的选项，打上X
			if (mUserAnswer.equals(current))
				imageView.setImageResource(R.drawable.jiakao_practise_false_day);
		}
	}


	class onOptionClicked implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int optionPos = (Integer) v.getTag();
			mUserAnswer = Answer[optionPos];
			String rightAnswer = mQuestion.getAnswer();
			boolean isRightChoice = mUserAnswer.equals(rightAnswer);
			
			//1. 设置各选项上A、B等的状态
			int count = mOptionView.getChildCount();
			for (int i = 0; i < count; i++) {
				ViewGroup vg = (ViewGroup) mOptionView.getChildAt(i);
				String currentAnswer = Answer[i];
				setOptionChoicedStatus(vg, currentAnswer, rightAnswer);
			}
			
			//2. 设置显示详情
			if (!isRightChoice) {
				mAnalysis.setVisibility(View.VISIBLE);
			}
				
			QLog.i(TAG, "onOptionClicked: " + optionPos);
			//3. 回调
			OptionClickCallback callback = mCallbackReference.get();
			if (callback != null) {
				callback.onOptionClick(mOrder, isRightChoice, mUserAnswer);
			}
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
			QLog.i(TAG, "onGetQuestionSucceed pos:" + pos + "|" + q.getOrder());
			setQuestion(q);
			super.onGetQuestionSucceed(pos, q);
		}
	}
}
