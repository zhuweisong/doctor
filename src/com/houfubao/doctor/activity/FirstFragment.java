package com.houfubao.doctor.activity;

import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.R;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.QuestionManager;
import com.houfubao.doctor.logic.utils.DoctorUtil;
import com.houfubao.doctor.logic.utils.PreferencesUtils;
import com.houfubao.doctor.logic.utils.QLog;
import com.houfubao.doctor.view.RowView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by sevenzhu on 2016/05/08.
 */
public class FirstFragment extends Fragment implements View.OnClickListener {

	private TextView mMyCollection;
	private TextView mMyError;
	private ViewGroup mGridView;
	private OnMainQuestionViewClicked mMainQuestionClicked;
	
    QuestionManager mQuestionManager = DoctorState.getInstance().getQuestionManager();
    /**
     * 顺序估量时的最后个题目顺号
     */
	private int mLastOrder;

	private static final List<Sample> SAMPLES = new ArrayList<Sample>();
	private static final String TAG = "FirstFragment";

	static {
		SAMPLES.add(new Sample(OrderTrainingActivity.class, 
				R.string.order_training, R.string.order_training, R.drawable.rectangle_blue));
		SAMPLES.add(new Sample(OrderTrainingActivity.class, 
				R.string.special_subject_training, R.string.special_subject_training, R.drawable.rectangle_red));
		SAMPLES.add(new Sample(OrderTrainingActivity.class, 
				R.string.function_test, R.string.function_test, R.drawable.rectangle_green));		
	}

    public static FirstFragment instance() {
        FirstFragment view = new FirstFragment();
		return view;
	}

    @Override
	public void onCreate(Bundle savedInstanceState) {

        //preload last question
		mLastOrder = PreferencesUtils.getInt(getActivity(), 
				DoctorConst.QUESTION_LAST_ORDER);
		mQuestionManager.preloadQuestion(mLastOrder);
		
		mMainQuestionClicked = new OnMainQuestionViewClicked();
		
		super.onCreate(savedInstanceState);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, null);
        mGridView = (ViewGroup)view.findViewById(R.id.main_list_view);
        mMyCollection = (TextView)view.findViewById(R.id.my_collection_question);
        mMyError = (TextView)view.findViewById(R.id.my_error_question);
        mMyCollection.setOnClickListener(this);
        mMyError.setOnClickListener(this);
        
        //
		Drawable drawable = getResources().getDrawable(R.drawable.jiakao_practice_star_day_s);
		int width = DoctorUtil.dpToPx(32, getResources());
		drawable.setBounds(0, 0, width, width);
		mMyCollection.setCompoundDrawables(null, drawable, null, null);
		
		Drawable drawable1 = getResources().getDrawable(R.drawable.media__video_delete);
		drawable1.setBounds(0, 0, width, width);
		mMyError.setCompoundDrawables(null, drawable1, null, null);	
        
        
        int size = SAMPLES.size();
        for (int i=0;i<size;i++) {
			Sample sample = SAMPLES.get(i);
			RowView rowView = new RowView(getActivity());
			rowView.setText(sample.txtId, sample.subtext, sample.drawrableId);
			rowView.setTag(i);
			rowView.setOnClickListener(mMainQuestionClicked);
			mGridView.addView(rowView);
		}
    
        return view;
    }
   
	
	private static class Sample {
		public Class<? extends Activity> viewClass;
		public int txtId;
		public int drawrableId; //图id
		public int subtext;

		public Sample(Class<? extends Activity> viewClass, int text, int subtext, int drawrableId) {
			this.viewClass = viewClass;
			this.txtId = text;
			this.subtext = subtext;
			this.drawrableId = drawrableId;
		}
	}

	class OnMainQuestionViewClicked implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int position = (Integer)v.getTag();
		    Class<? extends Activity> clazz = SAMPLES.get(position).viewClass;
			Intent i = new Intent(getActivity(), clazz);
			i.putExtra(OrderTrainingActivity.Question_Pos, mLastOrder);
			startActivity(i);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
