package com.houfubao.doctor.logic.online;

import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.main.DoctorState.NetworkStateChanged;
import com.houfubao.doctor.logic.online.DoctorStruct.MessageObj;
import com.houfubao.doctor.logic.utils.QLog;
import com.houfubao.doctor.logic.utils.VinsonAssertion;

import android.content.Context;
import android.os.Message;
import android.util.SparseArray;

public class QuestionManagerImpl extends QuestionManager implements NetworkStateChanged  {

    private static final String TAG = "QuestionManagerImpl";
    private static final int RQUEST_COUNT = 2;
    
	/** question cache of getAll  */
    protected SerialList mAllApps = new SerialList();
    protected SparseArray<Question> mCache = new SparseArray<Question>();
    /** */
    Requestor mRequestor;
    QuestionRequestCallback mRequestCallback;
    
    public QuestionManagerImpl(Requestor requestor) {
    	mRequestor = requestor;
	}
    
	public void init(Context context) {
    	mRequestCallback = new QuestionRequestCallback();
    	mRequestor.addCallback(mRequestCallback);
	}
	
	public void terminate() {
		mRequestor.removeCallback(mRequestCallback);
		mRequestCallback = null;
	}

	@Override
	public void getQuestion(QuestionResultCallback subscriber, int start, int count) { 
		VinsonAssertion.Assert(start >=0 && count>0, "parameter is error");
		int total = mAllApps.getITotal();
		
		if (start >= total) {
			QLog.i(TAG, "start > total" + "|" + start + "|" + total);
			return;
		}
		
		String ownerId = getSubScriberKey(subscriber);
		int request_start = Math.min(start, total);
		int request_end = Math.min(start + count, total); 
		List<Question> rsp = mAllApps.index(request_start, request_end);
		if (rsp != null) {
			QLog.i(TAG, "getAllApps onAppListSucceeded in cached:" +  request_start + "|" + request_end);
			List<Question> allRsp = new ArrayList<Question>(rsp);
			mRequestCallback.onGetQuestionsSucceed(ownerId, start, request_end - request_start , allRsp, DoctorConst.FROM_SELF);
			return;
		}

		//search by network
		mRequestor.getQuestions(mRequestCallback, ownerId, start, count);
    }

	/**
     * 获取指定位置的题目
     */
    public void getQuestion(QuestionResultCallback callback, int pos) { 
    	String ownerId = callback.getOwnerId();
    	
    	Question question = mCache.get(pos);
    	if (question != null) {
    		Question copy = new Question(question);
    		mHandler.obtainMessage(REQUEST_QUESTION_POS, DoctorConst.FROM_SELF, 0,  
    				new DoctorStruct.MessageObj(ownerId, copy)).sendToTarget();
    		return;
    	}
    	preloadFromNetwork(ownerId, pos);
    }
    
	void preloadFromNetwork(String ownerId, int pos) {
		mRequestor.getQuestions(mRequestCallback, ownerId, pos, RQUEST_COUNT);
	}
    
    
	@Override
    public void getQuestionCount(int chapterId) {
    	
    }
	

	
	private final static int REQUEST_QUESTION_LIST = _ID + 1;
	private final static int REQUEST_QUESTION_POS = _ID + 2;
	
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
			
			break;
			

		default:
			break;
		}
		super.handleSubMessageOnMainThread(msg);
	}
    
    private void handleQuestionListOnMainThread(String ownerId, 
    		int start, int count, List<Question> ql, int from) {
		if (from == DoctorConst.FROM_NETWORK) {
			//
			for (Question q : ql) {
				mCache.put(q.getPos(), q);
			}
			
			//写入数据库
			//.....
			
		}
		

		QuestionResultCallback callback = getCallbacker(ownerId);
		if (callback != null) {
			callback.onGetQuestionSucceed(start, ql.get(0));
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
		
	}
	
}
