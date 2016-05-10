package com.houfubao.doctor.view;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
	public ViewHolder() {
	}
	
	public ViewHolder(Object o) {
		object1 = o;
	}
	public Object object1;
	SparseArray<View> vArray = new SparseArray<View>();
	
    public <T extends View> T get(View view, int id) {
        View childView = vArray.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            vArray.put(id, childView);
        }
        return (T) childView;
    }
}