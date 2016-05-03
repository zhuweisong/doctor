package com.houfubao.doctor.logic.online;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.houfubao.doctor.logic.utils.VinsonAssertion;

public abstract class Requestor {
	protected Map<String, WeakReference<RequestCallback>> mCallbackRefs= new HashMap<String, WeakReference<RequestCallback>>();
	
	
	/**
	 * 从后台读取数据
	 */
	public abstract void getQuestions(RequestCallback callback, String tag, int from, int count);
	
	public abstract void getQuestionCount(RequestCallback callback, String tag, int chapterId);
	
	
	
	
	/**
	 * callback注册/清除
	 */
	//////////////////
	protected RequestCallback getCallbacker(String hashcode) {
		WeakReference<RequestCallback> callbackRef = mCallbackRefs.get(hashcode);
		if (callbackRef != null)
			return callbackRef.get();
		return null;
	}
	
	protected String getSubScriberKey(RequestCallback subscriber) {
		VinsonAssertion.Assert(subscriber != null, "subscriber is null");
		if (subscriber == null)
			return null;
		return String.valueOf(subscriber.hashCode());
	}
	
	public void addCallback(RequestCallback callback) {
		if (callback != null) {
           	String hashcode = getSubScriberKey(callback);
        	WeakReference<RequestCallback> callbackRef  = new WeakReference<RequestCallback>(callback);
        	mCallbackRefs.put(hashcode, callbackRef);
		}
	}
	
	public void removeCallback(RequestCallback callback) {
		if (callback != null) {
        	String hashcode = getSubScriberKey(callback);
        	mCallbackRefs.remove(hashcode);
		}
	}
	
}
