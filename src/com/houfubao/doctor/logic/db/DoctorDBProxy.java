package com.houfubao.doctor.logic.db;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.houfubao.doctor.logic.main.BaseCallBack;
import com.houfubao.doctor.logic.main.DataResultCallbackBase;
import com.houfubao.doctor.logic.main.Question;
import com.houfubao.doctor.logic.utils.QLog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 用工作线程更新数据库，以免占用主线程
 * 
 * @author sevenzhu
 *
 */
public class DoctorDBProxy extends BaseCallBack<DoctorDBProxy.DBProxyResultCallback> {
	
    public static abstract class DBProxyResultCallback extends DataResultCallbackBase {

    	/** on app added */
    	//@Override //TODO: 
		public void onQueryQuestion(final ArrayList<Question> questions){}

    }
    

    
	private static final String TAG = "AppstoreDBProxy";
	private DBUpdateHandler mDBWorkHandler;
	private DoctorDatabase mdb;
	
    public final static int DB_DONOTHING = 0;
    public final static int DB_QUERY_APP = 1;
    public final static int DB_DELETE_APP = 2;
    public final static int DB_UPDATE_APP = 3;
    public final static int DB_INSERT_APP = 4;
    
    
    private final static int MAIN_THREAD_ON_QUERY_COMMEND = _ID + 1;
    private final static int MAIN_THREAD_ON_QUERY_MESSAGE = _ID + 2;
    private final static int MAIN_THREAD_ON_QUERY_WHITE = _ID + 3;
    private final static int MAIN_THREAD_ON_QUERY_DOWNLOAD = _ID + 4;
    
	public void init(Looper worklooper, Context context) {
		mDBWorkHandler = new DBUpdateHandler(worklooper, context);
		mdb = new DoctorDatabase(context);
	}
    
    public DoctorDBProxy() {
    	
    }
    
    private void onDBCommendationChanged(int what, int status, Object obj) {
    	
    	if (what != DB_DONOTHING) {
        	Message dbworkmsg = mDBWorkHandler.obtainMessage();
    		dbworkmsg.arg1 = status;
        	dbworkmsg.obj = obj;
    		dbworkmsg.what = what;
    		dbworkmsg.sendToTarget();
    	}
    }
    
    //在主线程中回调,使用基类的MainHandler
    @Override
	protected void handleSubMessageOnMainThread(Message msg) {
		 Iterator<Entry<String, WeakReference<DBProxyResultCallback>>> iter = null;
		 DBProxyResultCallback callback = null;

			
    	switch (msg.what) {
		case MAIN_THREAD_ON_QUERY_COMMEND:

			break;
			
		case MAIN_THREAD_ON_QUERY_MESSAGE:
			break;
			
		case MAIN_THREAD_ON_QUERY_WHITE:

			break;
			
		case MAIN_THREAD_ON_QUERY_DOWNLOAD:
	
			break;
			
		default:
			break;
		}
	}
    
    
    //在工作线程中
    final class DBUpdateHandler extends Handler {

    	public DBUpdateHandler(Looper looper, Context context) {
    		super(looper);
    		
    	}
    	
		@Override
		public void handleMessage(Message msg) {
	
			
			QLog.i(TAG, "AppstoreDBProxy " + msg.what);
			
			switch (msg.what) {

			default:
				break;
			}
	
			super.handleMessage(msg);
		}
    }

}
