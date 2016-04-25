package com.houfubao.doctor;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
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
		SAMPLES.add(new Sample(OrderTraining.class, "Launcher", "Test Launcher interface", -1));
		SAMPLES.add(new Sample(RandomTraining.class, "Market", "Test market interface", -2));
		SAMPLES.add(new Sample(OrderTraining.class, "Launcher", "Test Launcher interface", -1));
		SAMPLES.add(new Sample(RandomTraining.class, "Market", "Test market interface", -2));
		
		SAMPLES.add(new Sample(OrderTraining.class, "Launcher", "Test Launcher interface", -1));
		SAMPLES.add(new Sample(RandomTraining.class, "Market", "Test market interface", -2));
		SAMPLES.add(new Sample(OrderTraining.class, "Launcher", "Test Launcher interface", -1));
		SAMPLES.add(new Sample(RandomTraining.class, "Market", "Test market interface", -2));
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
			rowView.setText(SAMPLES.get(position).text);
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
		public String text;
		public String subtext;
		public int resid; //图id

		public Sample(Class<? extends Activity> viewClass, String text,	String subtext, int id) {
			this.viewClass = viewClass;
			this.text = text;
			this.subtext = subtext;
			this.resid = id;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
}
