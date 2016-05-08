package com.lhh.apst.fragments;

import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.MainTabActivity;
import com.houfubao.doctor.OrderTrainingActivity;
import com.houfubao.doctor.R;
import com.houfubao.doctor.RandomTrainingActivity;
import com.houfubao.doctor.RowView;

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
import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * Created by linhonghong on 2015/8/11.
 */
public class ExamingFragment extends Fragment implements AdapterView.OnItemClickListener {

	private ListView mGridView;
	private ExampleListAdapter mAdapter;
	
	private static final List<Sample> SAMPLES = new ArrayList<Sample>();
	private static final String TAG = "MainActivity";

	static {
		SAMPLES.add(new Sample(OrderTrainingActivity.class, R.string.order_training, "", -1));
		SAMPLES.add(new Sample(RandomTrainingActivity.class, R.string.ramdon_training, "", -2));
		SAMPLES.add(new Sample(OrderTrainingActivity.class, R.string.special_subject_training, "", -1));
		SAMPLES.add(new Sample(RandomTrainingActivity.class, R.string.notdon_training, "", -2));
		SAMPLES.add(new Sample(MainTabActivity.class, R.string.function_test, "", -2));
	}

    public static ExamingFragment instance() {
        ExamingFragment view = new ExamingFragment();
		return view;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.examing_fragment, null);
        mGridView = (ListView)view.findViewById(R.id.main_list_view);
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
			Log.i(TAG, "position:" + position);
			rowView.setText(SAMPLES.get(position).txtId);
			rowView.setSubtext(SAMPLES.get(position).subtext);
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
		public int resid; //图id
		public String subtext;

		public Sample(Class<? extends Activity> viewClass, int text, String subtext, int id) {
			this.viewClass = viewClass;
			this.txtId = text;
			this.subtext = subtext;
			this.resid = id;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	    Class<? extends Activity> clazz = SAMPLES.get(position).viewClass;
		Intent i = new Intent(getActivity(), clazz);
		startActivity(i);
	}
	
}
