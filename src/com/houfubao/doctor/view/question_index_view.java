package com.houfubao.doctor.view;


import com.houfubao.doctor.R;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnHeaderClickListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnHeaderLongClickListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 在顺序做题页面，底部可以伸缩的面板 
 * @author zhuweisong
 */
public class question_index_view extends RelativeLayout implements OnItemClickListener,
OnHeaderClickListener, OnHeaderLongClickListener {

    private GridView mGridView;
    
	public question_index_view(Context context) {
		this(context, null, 0);	
	}
	
	public question_index_view(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public question_index_view(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(R.layout.question_index, this);


        mGridView = (GridView)rootView.findViewById(R.id.asset_grid);
        mGridView.setOnItemClickListener(this);

        /*
         * Currently set in the XML layout, but this is how you would do it in
         * your code.
         */
        // mGridView.setColumnWidth((int) calculatePixelsFromDips(100));
        // mGridView.setNumColumns(StickyGridHeadersGridView.AUTO_FIT);
        mGridView.setAdapter(new StickyGridHeadersSimpleArrayAdapter<String>(context
                .getApplicationContext(), getResources().getStringArray(R.array.countries),
                R.layout.header, R.layout.item));


        ((StickyGridHeadersGridView)mGridView).setOnHeaderClickListener(this);
        ((StickyGridHeadersGridView)mGridView).setOnHeaderLongClickListener(this);
	}

	@Override
	public boolean onHeaderLongClick(AdapterView<?> parent, View view, long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHeaderClick(AdapterView<?> parent, View view, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
