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
    protected MainHandler mHandler = new MainHandler();
    
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
	
    /**
     * Handler of incoming messages from service.
     */
    public class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	String hashcode = null;
        	E callback = null;
        	
            switch(msg.what) {
            case ADD_CALLBACK:
            	callback = (E)msg.obj;     	
               	hashcode = getSubScriberKey(callback);
            	WeakReference<E> callbackRef  = new WeakReference<E>(callback);
        		mCallbackRefs.put(hashcode, callbackRef);
                break;
                
            case REMOVE_CALLBACK:
            	callback = (E)msg.obj;           	
            	hashcode = getSubScriberKey(callback);
            	mCallbackRefs.remove(hashcode);
            	break;
            	
            default:
            	handleSubMessageOnMainThread(msg);
            	break;
            }
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
}
