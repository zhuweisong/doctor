package com.houfubao.doctor.activity;

import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.R;
import com.houfubao.doctor.R.drawable;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.QuestionManager;
import com.houfubao.doctor.logic.utils.PreferencesUtils;
import com.houfubao.doctor.logic.utils.QLog;
import com.houfubao.doctor.view.RowView;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by sevenzhu on 2016/05/08.
 */
public class FirstFragment extends Fragment implements AdapterView.OnItemClickListener {

	private GridView mGridView;
	private ExampleListAdapter mAdapter;

    QuestionManager mQuestionManager = DoctorState.getInstance().getQuestionManager();
    /**
     * 顺序估量时的最后个题目顺号
     */
	private int mLastOrder;

	private static final List<Sample> SAMPLES = new ArrayList<Sample>();
	private static final String TAG = "FirstFragment";

	static {
		SAMPLES.add(new Sample(OrderTrainingActivity.class, R.string.order_training, "4", R.drawable.rectangle_blue));
		SAMPLES.add(new Sample(RandomTrainingActivity.class, R.string.ramdon_training, "5", R.drawable.rectangle_blue));
		SAMPLES.add(new Sample(OrderTrainingActivity.class, R.string.special_subject_training, "6", R.drawable.rectangle_blue));
		SAMPLES.add(new Sample(RandomTrainingActivity.class, R.string.notdon_training, "7", R.drawable.rectangle_blue));
		
		SAMPLES.add(new Sample(OrderTrainingActivity.class, R.string.function_test, "8", R.drawable.rectangle_blue));
		SAMPLES.add(new Sample(OrderTrainingActivity.class, R.string.function_test, "8", R.drawable.rectangle_blue));
		SAMPLES.add(new Sample(OrderTrainingActivity.class, R.string.function_test, "8", R.drawable.rectangle_blue));
		SAMPLES.add(new Sample(OrderTrainingActivity.class, R.string.function_test, "8", R.drawable.rectangle_blue));
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
		
		super.onCreate(savedInstanceState);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, null);
        mGridView = (GridView)view.findViewById(R.id.main_list_view);
//      mGridView.setNumColumns(4);
        mGridView.setOnItemClickListener(this);
        mAdapter = new ExampleListAdapter();
        mGridView.setAdapter(mAdapter);
    
		
        return view;
    }
    
	private class ExampleListAdapter implements ListAdapter {


		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public int getCount() {
			return SAMPLES.size();
		}

		@Override
		public Object getItem(int position) {
			return SAMPLES.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RowView rowView;
			if (convertView != null) {
				rowView = (RowView) convertView;
			} else {
				rowView = new RowView(getActivity());
			}

			Sample sample = SAMPLES.get(position);
			rowView.setText(sample.txtId, sample.drawrableId);
			rowView.setSubtext(sample.subtext);
			return rowView;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean isEmpty() {
			return SAMPLES.isEmpty();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}
	}
	
	private static class Sample {
		public Class<? extends Activity> viewClass;
		public int txtId;
		public int drawrableId; //图id
		public String subtext;

		public Sample(Class<? extends Activity> viewClass, int text, String subtext, int drawrableId) {
			this.viewClass = viewClass;
			this.txtId = text;
			this.subtext = subtext;
			this.drawrableId = drawrableId;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	    Class<? extends Activity> clazz = SAMPLES.get(position).viewClass;
		Intent i = new Intent(getActivity(), clazz);
		i.putExtra(OrderTrainingActivity.Question_Pos, mLastOrder);
		startActivity(i);
	}
	
}
