package com.houfubao.doctor;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
	private GridView mGridView;
	private ExampleListAdapter mAdapter;
	
	private static final List<Sample> SAMPLES = new ArrayList<Sample>();
	private static final String TAG = "MainActivity";

	static {
		SAMPLES.add(new Sample(OrderTraining.class, R.string.order_training, "", -1));
		SAMPLES.add(new Sample(RandomTraining.class, R.string.ramdon_training, "", -2));
		SAMPLES.add(new Sample(OrderTraining.class, R.string.special_subject_training, "", -1));
		SAMPLES.add(new Sample(RandomTraining.class, R.string.notdon_training, "", -2));
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mGridView = (GridView)findViewById(R.id.main_grid);
        mGridView.setNumColumns(4);
        mGridView.setOnItemClickListener(this);
        mAdapter = new ExampleListAdapter();
        mGridView.setAdapter(mAdapter);
    }

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
				rowView = new RowView(MainActivity.this);
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
		Intent i = new Intent(MainActivity.this, clazz);
		startActivity(i);
	}
	
}
