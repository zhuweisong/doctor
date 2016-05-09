package com.houfubao.doctor.logic.online;

import java.util.List;

import com.houfubao.doctor.logic.db.DoctorDBProxy;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.main.DoctorState.NetworkStateChanged;
import com.houfubao.doctor.logic.online.DoctorStruct.MessageObj;
import com.houfubao.doctor.logic.utils.QLog;
import com.houfubao.doctor.logic.utils.VinsonAssertion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.SparseArray;

public class QuestionManagerImpl extends QuestionManager implements NetworkStateChanged  {

    private static final String TAG = "QuestionManagerImpl";
    private static final int NETWORK_RQUEST_COUNT = 5; //每次从网络请求的数据量
    private static final int DB_RQUEST_COUNT = 5; //每次从数据库请求的数据量
    private static final int DB_PRELOAD_COUNT = 5; //每次从数据库请求的数据量
    private static final int ALL_CHAPTER = -1;
    private final String KEY_LAST_ORDER = "LAST_QUESTION_ORDER"; //
    
	/** question cache of getAll  */
    protected SparseArray<Question> mCache = new SparseArray<Question>();
    protected SparseArray<Chapter> mChapterCache = new SparseArray<Chapter>();
    
    
    private SharedPreferences mSharedPreference;
    private int mLastOrder;
    private int mTotal = Integer.MAX_VALUE;
    
    /** */
    Requestor mRequestor;
    QuestionRequestCallback mRequestCallback;
    
    DoctorDBProxy mDbProxy;
    DBRequestCallback mDBRequestCallback;

    
    public QuestionManagerImpl(Requestor requestor, DoctorDBProxy db) {
    	mRequestor = requestor;
    	mDbProxy = db;
	}
    
	public void init(Context context) {
       	mSharedPreference = context.getSharedPreferences(
       			DoctorConst.SHAREDPREFERENCE_KEY_SET,
       			Context.MODE_PRIVATE |Context.MODE_MULTI_PROCESS);
       	mLastOrder = mSharedPreference.getInt(KEY_LAST_ORDER, 1);
       	
    	mRequestCallback = new QuestionRequestCallback();
    	mRequestor.addCallback(mRequestCallback);
    	
    	mDBRequestCallback = new DBRequestCallback();
    	mDbProxy.addCallback(mDBRequestCallback);
       	mDbProxy.queryQuestion(mDBRequestCallback, TAG, mLastOrder, DB_RQUEST_COUNT);
       	preloadFromDB(mLastOrder);
	}
	
	public void terminate() {
		mRequestor.removeCallback(mRequestCallback);
		mRequestCallback = null;
		
		mDbProxy.removeCallback(mDBRequestCallback);
		mDBRequestCallback = null;
	}

	@Override
	public void getQuestion(QuestionResultCallback subscriber, int start, int count) { 
		VinsonAssertion.Assert(start >= 0 && count>0, "parameter is error");

//		if (start >= mTotal) {
//			QLog.i(TAG, "start > total" + "|" + start + "|" + mTotal);
//			return;
//		}
//		
//		String ownerId = getSubScriberKey(subscriber);
//		int request_start = Math.min(start, mTotal);
//		int request_end = Math.min(start + count, mTotal); 
//		List<Question> rsp = mAllApps.index(request_start, request_end);
//		if (rsp != null) {
//			QLog.i(TAG, "getAllApps onAppListSucceeded in cached:" +  request_start + "|" + request_end);
//			List<Question> allRsp = new ArrayList<Question>(rsp);
//			mRequestCallback.onGetQuestionsSucceed(ownerId, start, request_end - request_start , allRsp, DoctorConst.FROM_SELF);
//			return;
//		}

		//search by network
//		mRequestor.getQuestions(mRequestCallback, ownerId, start, count);
    }

	/**
     * 获取指定位置的题目
     */
    public void getQuestion(QuestionResultCallback callback, int order) { 
    	String ownerId = callback.getOwnerId();
    	
    	Question question = mCache.get(order);
		QLog.i(TAG, "get Question:" + order + "|" + (question==null));
    	if (question != null) {
    		Question copy = new Question(question);
    		mHandler.obtainMessage(REQUEST_QUESTION_POS, DoctorConst.FROM_SELF, 0,  
    				new DoctorStruct.MessageObj(ownerId, copy)).sendToTarget();
    	} else {
        	preloadFromNetwork(ownerId, order);
    	}
    	
		preloadFromDB(order);
		
		return;
    }
    
    private void preloadFromNetwork(String ownerId, int pos) {
		mRequestor.getQuestions(mRequestCallback, ownerId, pos, NETWORK_RQUEST_COUNT);
	}
	
	private void preloadFromDB(int curOrder)  {
		int maxOrder = Math.min(curOrder + DB_PRELOAD_COUNT, mTotal);
		for (int i = curOrder;i < maxOrder;i++) {
			if (mCache.get(i)==null) {
				mDbProxy.queryQuestion(mDBRequestCallback, TAG, i, DB_RQUEST_COUNT);
				return;
			}
		}
	}
    
    
	@Override
    public void getQuestionCount(QuestionResultCallback callback, int chapterId) {
	  	String ownerId = callback.getOwnerId();
	  	
	  	Chapter chapter = mChapterCache.get(chapterId);
	  	if (chapter != null && chapter.getCount()>0) {
	  		mRequestCallback.onGetQuestionCountSucceed(ownerId, chapterId, chapter.getCount(), DoctorConst.FROM_SELF);
	  		return;
	  	}
	  	
    	mRequestor.getQuestionCount(mRequestCallback, ownerId, chapterId);
    }
	

	
	private final static int REQUEST_QUESTION_LIST = _ID + 1;
	private final static int REQUEST_QUESTION_POS = _ID + 2;
	private final static int REQUEST_QUESTION_COUNT = _ID + 3;
	
    @Override
	protected void handleSubMessageOnMainThread(Message msg) {
    	
    	MessageObj msgObj = (MessageObj)msg.obj;
    	int from = msg.arg1;
    	
		switch (msg.what) {
		case REQUEST_QUESTION_LIST:
			List<Question> qs = (List<Question>)msgObj.obj0;
			handleQuestionListOnMainThread(msgObj.ownerId,
					msgObj.int1, msgObj.int2, qs, from);
			break;
			
		case REQUEST_QUESTION_POS:
			handleQuestionPosOnMainThread(msgObj.ownerId, msgObj.int1, (Question)msgObj.obj0, from);
			break;
			
		case REQUEST_QUESTION_COUNT:
			handleQuestionCountOnMainThread(msgObj.ownerId, msgObj.int1, msgObj.int2, from);
			break;
			

		default:
			break;
		}
		super.handleSubMessageOnMainThread(msg);
	}
    
    private void handleQuestionPosOnMainThread(String ownerId, int order, Question q, int from) {
		if (from == DoctorConst.FROM_NETWORK) {
			mCache.put(q.getOrder(), q);
		}
		
		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null ) {
			callback.onGetQuestionSucceed(order, q);
		} else {
			QLog.e(TAG, "handleQuestionPosOnMainThread error" + order);
		}
	}

	private void handleQuestionListOnMainThread(String ownerId, 
    		int start, int count, List<Question> ql, int from) {
		if (from == DoctorConst.FROM_NETWORK) {
			//
			for (Question q : ql) {
				mCache.put(q.getOrder(), q);
			}
			
			//从网络来的数据，写入数据库
			mDbProxy.insert(ql);
		}
		
		Question question = mCache.get(start);
		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null && question != null) {
			callback.onGetQuestionSucceed(start, question);
		} else {
			QLog.e(TAG, "handleQuestionListOnMainThread error" + start);
		}
    }
    
    private void handleQuestionCountOnMainThread(String ownerId, int chapterId, int count, int from) {
		if (from == DoctorConst.FROM_NETWORK && chapterId == ALL_CHAPTER) {
			mTotal = count;
		}
		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null ) {
			callback.onGetQuestionCountSucceed(chapterId, count);
		} else {
			QLog.e(TAG, "handleQuestionCountOnMainThread error" + chapterId);
		}
    }
    
	@Override
	public void onNetworkStateChanged(boolean isConnected) {

	}

	/**
	 * 从网络返回数据的回调
	 */
	class QuestionRequestCallback extends RequestCallback {
		public void onGetQuestionsSucceed(String ownerId, int start, int count, List<Question> questions, int from) {
			mHandler.obtainMessage(REQUEST_QUESTION_LIST, from, 0, 
					new DoctorStruct.MessageObj(ownerId, start, count, questions, null))
					.sendToTarget();
		}
		
		public void onGetQuestionsFailed(String ownerId, int start, int count) {			
		}

		@Override
		public void onGetQuestionCountSucceed(String tag, int chaporder, int count, int from) {
			mHandler.obtainMessage(REQUEST_QUESTION_COUNT, from, 0, 
					new DoctorStruct.MessageObj(tag, chaporder, count, null, null))
					.sendToTarget();
			super.onGetQuestionCountSucceed(tag, chaporder, count, from);
		}

		@Override
		public void onGetQuestionCountFailed(String tag, int chaporder) {
			// TODO Auto-generated method stub
			super.onGetQuestionCountFailed(tag, chaporder);
		}
		
		
	}
	
	/**
	 * 从数据库返回数据的回调
	 */
	class DBRequestCallback extends DoctorDBProxy.DBProxyResultCallback {

		@Override
		public void onQueryQuestion(String ownerId, int start, int count, List<Question> ql) {
			
			QLog.e(TAG, "onQueryQuestion :" + start + " count:" + count + "|" + ql.size());
			
			for (Question q : ql) {
				mCache.put(q.getOrder(), q);
			}
			
			Question question = mCache.get(start);
			QuestionResultCallback callback = getCallbacker(ownerId);
			if (callback != null && question != null) {
				callback.onGetQuestionSucceed(start, question);
			} else {
				QLog.e(TAG, "handleQuestionListOnMainThread error" + start);
			}
			
			/**
			 * 判断数据库中取到的数据是否全部已存在，如果不存在，则要在后台拉取数据
			 */
			int limit = start + count;
			if (count < ql.size() && limit < count) {
				mRequestor.getQuestions(mRequestCallback, ownerId, start, count);
			}
			
			super.onQueryQuestion(ownerId, start, count, ql);
		}
		
	}
	
}
