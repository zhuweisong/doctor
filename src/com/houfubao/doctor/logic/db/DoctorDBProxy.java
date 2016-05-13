package com.houfubao.doctor.logic.db;


import java.util.List;

import com.houfubao.doctor.logic.main.BaseCallBack;
import com.houfubao.doctor.logic.main.DataResultCallbackBase;
import com.houfubao.doctor.logic.online.Chapter;
import com.houfubao.doctor.logic.online.DoctorStruct;
import com.houfubao.doctor.logic.online.Question;
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
		public void onQueryQuestion(String tag, int start, int count, final List<Question> questions){}
		public void onQueryChapter(String tag, final List<Chapter> questions){}
    }
    
	private static final String TAG = "AppstoreDBProxy";
	private DBUpdateHandler mDBWorkHandler;
	private DoctorDatabase mdb;
	
    public final static int DB_DONOTHING = 0;
    public final static int DB_QUERY_QUESTION = 1;
    public final static int DB_DELETE_QUESTION = 2;
    public final static int DB_INSERT_QUESTION = 3;
    
    public final static int DB_QUERY_CHAPTER = 4;
    public final static int DB_DELETE_CHAPTER = 5;
    public final static int DB_INSERT_CHAPTER = 6;
    
    private final static int MAIN_THREAD_ON_QUERY_QUESION = _ID + 1;
    private final static int MAIN_THREAD_ON_QUERY_CHAPTER = _ID + 2;
    
    public DoctorDBProxy() {}
    
	public void init(Looper worklooper, Context context) {
		mDBWorkHandler = new DBUpdateHandler(worklooper, context);
		mdb = new DoctorDatabase(context);
	}
        
	public void queryQuestion(DBProxyResultCallback callback, String tag, int start, int count) {
		String ownerId = callback.getOwnerId();
		onDBChanged(DB_QUERY_QUESTION, 
				new DoctorStruct.MessageObj(ownerId, start, count, tag, null));
	}
	
	public void insert(List<Question> list) {
		onDBChanged(DB_INSERT_QUESTION, new DoctorStruct.MessageObj(null, 0, 0, null, list));
	}
	
	
	///////////chapter////////////////
	//
	public void queryChapter(DBProxyResultCallback callback, String tag) {
		String ownerId = callback.getOwnerId();
		onDBChanged(DB_QUERY_CHAPTER, new DoctorStruct.MessageObj(ownerId, tag, null));
	}
	
	public void insertChapter(List<Chapter> list) {
		onDBChanged(DB_INSERT_CHAPTER, new DoctorStruct.MessageObj(null, null, list));
	}
	
    private void onDBChanged(int what, Object obj) {
    	
    	if (what != DB_DONOTHING) {
        	Message dbworkmsg = mDBWorkHandler.obtainMessage();
        	dbworkmsg.obj = obj;
    		dbworkmsg.what = what;
    		dbworkmsg.sendToTarget();
    	}
    }
    
    //在主线程中回调,使用基类的MainHandler
    @Override
	protected void handleSubMessageOnMainThread(Message msg) {
		DoctorStruct.MessageObj msgObj = (DoctorStruct.MessageObj)msg.obj; 
  	    String ownerId = msgObj.ownerId;
		DBProxyResultCallback callback = getCallbacker(ownerId);

    	switch (msg.what) {
		case MAIN_THREAD_ON_QUERY_QUESION:
			if (callback != null) {
				callback.onQueryQuestion((String)msgObj.obj0, msgObj.int1, msgObj.int2, (List<Question>)msgObj.obj1);
			}
			break;
			
		case MAIN_THREAD_ON_QUERY_CHAPTER:
			if (callback != null) {
				callback.onQueryChapter((String)msgObj.obj0, (List<Chapter>)msgObj.obj1);
			}
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
			DoctorStruct.MessageObj msgObj = (DoctorStruct.MessageObj)msg.obj; 
			List<Question> L1 = null;
			QLog.i(TAG, "AppstoreDBProxy " + msg.what);
			
			switch (msg.what) {
			case DB_QUERY_QUESTION:
				L1 = mdb.queryQuestion(msgObj.int1, msgObj.int2);
				msgObj.obj1 = L1;
				mHandler.obtainMessage(MAIN_THREAD_ON_QUERY_QUESION, msgObj).sendToTarget();
				break;

			case DB_INSERT_QUESTION:
				L1 = (List<Question>)msgObj.obj1;
				mdb.insert(L1);
				break;
				
			case DB_QUERY_CHAPTER:
				msgObj.obj1 = mdb.queryChapter();
				mHandler.obtainMessage(MAIN_THREAD_ON_QUERY_QUESION, msgObj).sendToTarget();
				break;
				
			case DB_DELETE_CHAPTER:
				break;
				
			case DB_INSERT_CHAPTER:
				List<Chapter> L2 = (List<Chapter>)msgObj.obj1;
				mdb.insertChapter(L2);
				break;
			    
			default:
				break;
			}
	
			super.handleMessage(msg);
		}
    }

}
