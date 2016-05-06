package com.houfubao.doctor.logic.utils;

import java.util.ArrayList;
import java.util.List;

public class SimplePool<T> {
	List<T> mList = new ArrayList<T>();
	
	public SimplePool(int maxsize) {
		mList = new ArrayList<T>(maxsize);
	}
	
	public T acquire() {
		if (mList.size()>0) {
			T temp = mList.get(0);
			mList.remove(0);
			return temp;
		}
		return null;
	}
	
	public void release(T instance) {
		mList.remove(instance); //先删除，以免存在相同的对象
		mList.add(instance);
	}
	
}
