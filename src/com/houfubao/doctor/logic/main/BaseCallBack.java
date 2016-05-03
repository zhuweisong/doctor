package com.houfubao.doctor.logic.main;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.houfubao.doctor.logic.utils.QLog;
import com.houfubao.doctor.logic.utils.VinsonAssertion;


import android.os.Handler;
import android.os.Message;

public class BaseCallBack<E extends DataResultCallbackBase> {
	
	private static final String TAG = "BaseCallBack";
	private static final int ADD_CALLBACK = 1;
	private static final int REMOVE_CALLBACK = 2;
	protected static final int _ID = 100;
	/** Reference of observer*/
	protected Map<String, WeakReference<E>> mCallbackRefs= new HashMap<String, WeakReference<E>>();
    protected MainHandler mHandler;
    
    public BaseCallBack() {
    	mHandler = new MainHandler(this);
    }
    
	   /**aysn for   java.util.ConcurrentModificationException
     * Add the new callback
     * @param callback
     */
    public void addCallback(E callback) {
    	if (callback == null) {
    		QLog.e(TAG, "addCallback error callback == null");
    		return;
    	}
    	Message msg = mHandler.obtainMessage(ADD_CALLBACK, callback);
    	msg.sendToTarget();
    }
    
	/** aysn for   java.util.ConcurrentModificationException
	 * Remove the callback
	 * @param callback
	 */
	public void removeCallback(E callback) {
		if (callback != null) {
	    	Message msg = mHandler.obtainMessage(REMOVE_CALLBACK, callback);
	    	msg.sendToTarget();
		}
	}
	
	protected void handleSubMessageOnMainThread(Message msg) {
	}
	
    void internal_addcallback(Object object) {
    	E callback = (E)object;
    	String hashcode = getSubScriberKey(callback);
    	WeakReference<E> callbackRef  = new WeakReference<E>(callback);
		mCallbackRefs.put(hashcode, callbackRef);
    }
    
    void internal_removecallback(Object object) {
    	E callback = (E)object;           	
    	String hashcode = getSubScriberKey(callback);
    	mCallbackRefs.remove(hashcode);
    }
    
	void dump() {

		Iterator<Entry<String, WeakReference<E>>> iter = mCallbackRefs.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, WeakReference<E>> entry = (Entry<String, WeakReference<E>>)iter.next();
			String key = entry.getKey();
			WeakReference<E> value = entry.getValue();
			
			QLog.i(TAG, "BaseCallBack dump" + key + " value:" + value.get());
		}
	}
	
//	protected void 
	
	protected E getCallbacker(String hashcode) {
		WeakReference<E> callbackRef = mCallbackRefs.get(hashcode);
		if (callbackRef != null)
			return callbackRef.get();
		return null;
	}
	
	protected String getSubScriberKey(E subscriber) {
		VinsonAssertion.Assert(subscriber != null, "subscriber is null");
		if (subscriber == null)
			return null;
		return subscriber.getOwnerId();
	}
	
    /**
     * Handler of incoming messages from service.
     */
    protected static class MainHandler<T extends BaseCallBack> extends Handler {
    	WeakReference <T> mRef;
    	
    	MainHandler(T t) {
    		mRef = new WeakReference <T>(t);
    	}
    	
        @Override
        public void handleMessage(Message msg) {
        	T baseclass = mRef.get();
        	if (baseclass == null) {
        		return;
        	}
 
            switch (msg.what) {
            case ADD_CALLBACK:
            	baseclass.internal_addcallback(msg.obj);
                break;
                
            case REMOVE_CALLBACK:
            	baseclass.internal_removecallback(msg.obj);
            	break;
            	
            default:
            	baseclass.handleSubMessageOnMainThread(msg);
            	break;
            }
        }
    }
}
