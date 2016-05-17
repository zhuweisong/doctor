package com.houfubao.doctor.view;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
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
import com.houfubao.doctor.logic.utils.QLog;

public class QuestionMainView extends RelativeLayout {
	public final static String TAG = "QuestionMainView";

	int mOrder;

	ImageView mSingleOrMulti;
	TextView mTitle;
	QuestionOptionView mOptionView;
	TextView mAnalysis;

	Handler mHandler;

	Question mQuestion;
	QuestionManager mQuestionManager;
	QuestionManager.QuestionResultCallback mCallback = new MyQuestionManager();

	public QuestionMainView(Context context) {
		super(context, null, 0);
		mQuestionManager = DoctorState.getInstance().getQuestionManager();

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
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.question_main_view, this);
		mSingleOrMulti = (ImageView) rootView
				.findViewById(R.id.question_single_or_multi);
		mTitle = (TextView) rootView.findViewById(R.id.question_title);
		mOptionView = (QuestionOptionView) rootView
				.findViewById(R.id.question_option);
		mAnalysis = (TextView) findViewById(R.id.question_anlaysis);
		mOptionView.setCallbackHandler(mHandler);

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
		mOptionView.setOption(mQuestion.getOption());

		// 正文
		mAnalysis.setText(mQuestion.getAnalysis());

	}

	/**
	 * 垂直居中的ImageSpan
	 * 
	 * @author KenChung
	 */
	public class VerticalImageSpan extends ImageSpan {
		public VerticalImageSpan(Context context, int resid) {
			super(context, resid);
		}

		public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
			Drawable d = getDrawable();
			Rect rect = d.getBounds();
			if (fm != null) {
				FontMetricsInt fmPaint = paint.getFontMetricsInt();
				int fontHeight = fmPaint.bottom - fmPaint.top;
				int drHeight = rect.bottom - rect.top;
				int top = drHeight / 2 - fontHeight / 4;
				int bottom = drHeight / 2 + fontHeight / 4;
				fm.ascent = -bottom;
				fm.top = -bottom;
				fm.bottom = top;
				fm.descent = top;
			}
			return rect.right;
		}

		@Override
		public void draw(Canvas canvas, CharSequence text, int start, int end,
				float x, int top, int y, int bottom, Paint paint) {
			Drawable b = getDrawable();
			canvas.save();
			int transY = 0;
			transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
			canvas.translate(x, transY);
			b.draw(canvas);
			canvas.restore();

		}
	}

	public int getOrder() {
		return mOrder;
	}

	private void onOptionClicked(int optionpos) {

	}

	// *
	static final int MSG_ID_ON_OPTION_CLICKED = 1;

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
			case MSG_ID_ON_OPTION_CLICKED:
				qView.onOptionClicked(msg.arg1);
				break;

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
		public void onGetQuestionSucceed(int from, int count,
				List<Question> list) {

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
