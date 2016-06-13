package com.houfubao.doctor.logic.online;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.logic.db.DoctorDBProxy;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.main.DoctorState.NetworkStateChanged;
import com.houfubao.doctor.logic.online.DoctorStruct.MessageObj;
import com.houfubao.doctor.logic.online.QuestionManager.QuestionResultCallback;
import com.houfubao.doctor.logic.utils.PreferencesUtils;
import com.houfubao.doctor.logic.utils.QLog;
import com.houfubao.doctor.logic.utils.VinsonAssertion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.SparseArray;

public class QuestionManagerImpl extends QuestionManager implements NetworkStateChanged  {

    private static final String TAG = "QuestionManagerImpl";
    private static final String preLoad = "preLoad";

    private final String KEY_QUESTION_UPDATEAT = "KEY_QUESTION_UPDATEAT"; //
    private final String KEY_CHAPTER_UPDATEAT = "KEY_CHAPTER_UPDATEAT"; //
    
    
    private static final int NETWORK_RQUEST_COUNT = 5; //每次从网络请求的数据量
    private static final int DB_RQUEST_COUNT = 5; //每次从数据库请求的数据量
    private static final int DB_PRELOAD_COUNT = 5; //每次从数据库请求的数据量
    private static final int ALL_CHAPTER = -1;
    
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	/** question cache of getAll, order为key  */
    protected SparseArray<Question> mQuestionACache = new SparseArray<Question>();
    protected List<Chapter> mChapterCache = new ArrayList<Chapter>();
    private boolean mHasGotChapterFromNet = false;
    private boolean mHasGotQuestionFromNet = false;
    private long mChapterUpdateAt = 0; //章节最后更新时间
    private long mQuestionUpdateAt = 0; //问题最后更新时间
    
    private int mLastOrder;
    private int mTotal = Integer.MAX_VALUE;
    private Context mContext;
    
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
		mContext = context;
       	mChapterUpdateAt = PreferencesUtils.getLong(context, KEY_CHAPTER_UPDATEAT, 0);
       	mQuestionUpdateAt = PreferencesUtils.getLong(context, KEY_QUESTION_UPDATEAT, 0);
       	
    	mRequestCallback = new QuestionRequestCallback();
    	mRequestor.addCallback(mRequestCallback);
    	
    	mDBRequestCallback = new DBRequestCallback();
    	mDbProxy.addCallback(mDBRequestCallback);
       	mDbProxy.queryChapter(mDBRequestCallback, TAG);
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
	@Override
	public void getQuestion(QuestionResultCallback callback, int order) { 
    	String ownerId = callback.getOwnerId();
    	
    	//1. 读入数据
    	Question question = mQuestionACache.get(order);
		QLog.i(TAG, "get Question:" + order + "|" + (question==null));
    	if (question != null) {
    		Question copy = new Question(question);
    		mHandler.obtainMessage(REQUEST_QUESTION_POS_SUCCEED, DoctorConst.FROM_SELF, 0,  
    				new DoctorStruct.MessageObj(ownerId, copy)).sendToTarget();
    	} else {
        	preloadFromNetwork(ownerId, order);
    	}
    	
    	//2. 提前读取
		preloadFromDB(order);
		
		return;
    }
	
	@Override    
    public void preloadQuestion(int order){
		preloadFromDB(order);
	}

    
    private void preloadFromNetwork(String ownerId, int pos) {
		mRequestor.getQuestions(mRequestCallback, ownerId, pos, NETWORK_RQUEST_COUNT);
	}
	
	private void preloadFromDB(int curOrder) {
		int maxOrder = Math.min(curOrder + DB_PRELOAD_COUNT, mTotal);

		for (int i = curOrder;i < maxOrder;i++) {
			if (mQuestionACache.get(i)==null) {
				QLog.i(TAG, "preloadFromDB:" + curOrder + "|" + DB_RQUEST_COUNT);
				mDbProxy.queryQuestion(mDBRequestCallback, preLoad, i, DB_RQUEST_COUNT);
				return;
			}
		}
	}
    
    
	@Override
    public void getQuestionCount(QuestionResultCallback callback, int chapterId) {
	  	String ownerId = callback.getOwnerId();
	  	
	  	Chapter chapter = mChapterCache.get(chapterId);
	  	if (chapter != null && chapter.getQuestionCount()>0) {
	  		mRequestCallback.onGetQuestionCountSucceed(ownerId, chapterId, chapter.getQuestionCount(), DoctorConst.FROM_SELF);
	  		return;
	  	}
	  	
    	mRequestor.getQuestionCount(mRequestCallback, ownerId, chapterId);
    }
	
	@Override
    public void getChapterInfo(QuestionResultCallback callback) {
	  	String ownerId = callback.getOwnerId();
	  	
		if (mChapterCache.size()>0) {
			List<Chapter> cpList = new ArrayList<Chapter>(mChapterCache);
			mRequestCallback.onGetChapterSucceed(ownerId, mChapterUpdateAt, cpList, DoctorConst.FROM_SELF);
			return;
		}
		
		mRequestor.getChapter(mRequestCallback, ownerId, mChapterUpdateAt);
    }

	
	private final static int REQUEST_QUESTION_LIST_SUCCEED = _ID + 1;
	private final static int REQUEST_QUESTION_LIST_FAILED = _ID + 2;
	
	private final static int REQUEST_QUESTION_POS_SUCCEED = _ID + 3;
	private final static int REQUEST_QUESTION_POS_FAILED = _ID + 4;
	
	private final static int REQUEST_CHAPTER_SUCCEED = _ID + 5;
	private final static int REQUEST_CHAPTER_FAILED = _ID + 6;
	
	
    @Override
	protected void handleSubMessageOnMainThread(Message msg) {
    	
    	MessageObj msgObj = (MessageObj)msg.obj;
    	int from = msg.arg1;
    	
		switch (msg.what) {
		case REQUEST_QUESTION_LIST_SUCCEED:
			List<Question> qs = (List<Question>)msgObj.obj0;
			handleRequestQuestionListOnMainThread(msgObj.ownerId, msgObj.int1, msgObj.int2, qs, from);
			break;
			
		case REQUEST_QUESTION_LIST_FAILED:
			handleQuestionRequestFailedOnMainThread(msgObj.ownerId, msgObj.int1, msgObj.int2);
			break;
			
		case REQUEST_QUESTION_POS_SUCCEED:
			handleRequestQuestionPosSucceedOnMainThread(msgObj.ownerId, msgObj.int1, (Question)msgObj.obj0, from);
			break;
			
		case REQUEST_QUESTION_POS_FAILED:
			handleRequestQuestionPosFailedOnMainThread(msgObj.ownerId, msgObj.int1);			
			break;
			
		case REQUEST_CHAPTER_SUCCEED:
			handleRequestCharpterSucceedOnMainThread(msgObj.ownerId, (Long)msgObj.obj0, (List<Chapter>)msgObj.obj1, from);
			break;
			
		case REQUEST_CHAPTER_FAILED:
			break;
			
		default:
			break;
		}
		super.handleSubMessageOnMainThread(msg);
	}
    
	private void handleRequestQuestionPosSucceedOnMainThread(String ownerId, int order, Question q, int from) {
		if (from == DoctorConst.FROM_NETWORK) {
			mQuestionACache.put(q.getOrder(), q);
		}
		QLog.i(TAG, "handleRequestQuestionPosSucceedOnMainThread " + q.getOrder());
		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null ) {
			callback.onGetQuestionSucceed(order, q);
		}
	}

	private void handleRequestQuestionListOnMainThread(String ownerId, int start, int count, List<Question> ql, int from) {
		if (from == DoctorConst.FROM_NETWORK) {
			//
			for (Question q : ql) {
				mQuestionACache.put(q.getOrder(), q);
			}
			
			//从网络来的数据，写入数据库
			mDbProxy.insert(ql);
		}
		
		Question question = mQuestionACache.get(start);
		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null && question != null) {
			callback.onGetQuestionSucceed(start, question);
		}
    }
    
    private void handleQuestionCountOnMainThread(String ownerId, int chapterId, int count, int from) {
		if (from == DoctorConst.FROM_NETWORK && chapterId == ALL_CHAPTER) {
			mTotal = count;
		}
		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null ) {
			callback.onGetQuestionCountSucceed(chapterId, count);
		}
    }
    
    
    private void handleRequestCharpterSucceedOnMainThread(String ownerId, Long updateAt, List<Chapter> list, int from) {

    	if (from == DoctorConst.FROM_NETWORK) {
    		mHasGotChapterFromNet = true;
    		
    		if (list.size()>0) {
    			//更新数据
    			mDbProxy.insertChapter(list);
    			
    			//写入preference
    			mChapterUpdateAt = updateAt;
    	       	PreferencesUtils.putLong(mContext, KEY_CHAPTER_UPDATEAT, mChapterUpdateAt);

                
    			//重新从数据库读入数据
    			mDbProxy.queryChapter(mDBRequestCallback, ownerId);
    		}

    		QLog.i(TAG, "handleRequestCharpterSucceedOnMainThread: " + formatter.format(new Date(updateAt)));
		} 
    	
		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null ) {
			callback.onGetChapterSucceed(list);
		}
	}

	private void handleRequestQuestionPosFailedOnMainThread(String ownerId, int int1) {
		
		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null ) {
			callback.onGetQuestionFailed(int1);
		}
	}

	private void handleQuestionRequestFailedOnMainThread(String ownerId, int int1, int int2) {
		
	}
	


	/**
	 * 从网络返回数据的回调
	 */
	class QuestionRequestCallback extends RequestCallback {
		
		@Override
		public void onGetQuestionsSucceed(String ownerId, int start, int count, List<Question> questions, int from) {
			mHandler.obtainMessage(REQUEST_QUESTION_LIST_SUCCEED, from, 0, 
					new DoctorStruct.MessageObj(ownerId, start, count, questions, null))
					.sendToTarget();
		}
		
		@Override
		public void onGetQuestionsFailed(String ownerId, int start, int count, int from) {
			mHandler.obtainMessage(REQUEST_QUESTION_LIST_FAILED, from, 0, 
					new DoctorStruct.MessageObj(ownerId, start, count, null, null))
					.sendToTarget();
		}
		
		@Override
		public void onGetChapterSucceed(String tag, long updateAt, List<Chapter> chapters, int from){
			mHandler.obtainMessage(REQUEST_CHAPTER_SUCCEED, from, 0, 
					new DoctorStruct.MessageObj(tag, 0, 0, updateAt, chapters))
					.sendToTarget();
		}
		
		@Override
		public void onGetChapterFailed(String tag, long updateAt, int from){
			mHandler.obtainMessage(REQUEST_CHAPTER_FAILED, from, 0, 
					new DoctorStruct.MessageObj(tag, 0, 0, updateAt, null))
					.sendToTarget();
		}
	}
	
	/**
	 * 从数据库返回数据的回调
	 */
	class DBRequestCallback extends DoctorDBProxy.DBProxyResultCallback {

		@Override
		public void onQueryQuestion(String ownerId, int start, int count, List<Question> ql) {
			
			QLog.i(TAG, "onQueryQuestion from DB :" + start + " count:" + count + "|" + ql.size());
			
			for (Question q : ql) {
				QLog.i(TAG, "onQueryQuestion from DB 11:" + q.toString());	
				mQuestionACache.put(q.getOrder(), q);
			}
			
			Question question = mQuestionACache.get(start);
			QuestionResultCallback callback = getCallbacker(ownerId);
			if (callback != null && question != null) {
				callback.onGetQuestionSucceed(start, question);
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
		
		@Override
		public void onQueryChapter(String tag, final List<Chapter> chapters){
			QLog.i(TAG, "onQueryChapter from DB :" + chapters.size());

			if (chapters.size() > 0){
				mTotal = 0;
				mChapterCache.clear();
				mChapterCache.addAll(chapters);				
				for (Chapter chapter : chapters) {
					if (chapter.getLevel() == 1){
						mTotal += chapter.getQuestionCount();
					}
				}
			}
			
			QuestionResultCallback callback = getCallbacker(tag);
			if (callback != null) {
				callback.onGetChapterSucceed(chapters);
			}
		}
	}
	
	@Override
	public void onNetworkStateChanged(boolean isConnected) {
		if (!mHasGotChapterFromNet) {
			mRequestor.getChapter(mRequestCallback, TAG, mChapterUpdateAt);
		}
	}
	
}
