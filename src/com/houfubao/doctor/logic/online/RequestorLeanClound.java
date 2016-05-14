package com.houfubao.doctor.logic.online;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import android.content.Context;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.houfubao.doctor.logic.db.ChapterEntry;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.utils.QLog;

/**
 * 
 */

public class RequestorLeanClound extends Requestor {

	private final static String TAG = "LeanCloundManager";
	
	static class QuestionColumns { 
	  public static final String QID = "qid";
	  public static final String TITLE = "title";
	  public static final String OPTION = "option";
	  public static final String ANSWER = "sAnswer";
	  public static final String ATTR = "lAttr";
	  public static final String CHAPTER = "iChapter";
	  public static final String PICTURE = "sPicture";
	  public static final String DETAIL_ANALYSIS = "sDetailAnalysis";
	  public static final String ORDER = "iOrder";
	  public static final String UPDATE_AT = "updatedAt";
	}
	

	  
	public void init(Context context) {
	    AVOSCloud.setDebugLogEnabled(true);
	    AVOSCloud.initialize(context.getApplicationContext(), DoctorConst.APP_ID, DoctorConst.APP_KEY);
	}
	
	@Override
	public void getQuestionCount(RequestCallback callback, String tag, int chapterId){
		String owner = getSubScriberKey(callback);
		if (owner == null) {
			return;
		}
		
		QLog.e(TAG, "getQuestionCount ");
	    AVQuery<AVObject> query = new AVQuery<AVObject>("question");
	    if (chapterId > 0) {
	    	query.whereEqualTo(ChapterEntry.ChapterColumns.COLUMN_ORDER, chapterId);
	    }
	    String callbackTag = owner + DoctorConst.SEPRATOR + tag + DoctorConst.SEPRATOR + chapterId;
	    query.countInBackground(new MyCountCallback(callbackTag));
	}
	

	/**
	 * 查询问题总数回调接口
	 */
	class MyCountCallback extends CountCallback {
		String mTag;
		public MyCountCallback(String tag){
			mTag = tag;
		}
		@Override
		public void done(int count, AVException e) {
			String[] extra = mTag.split(DoctorConst.SEPRATOR);
			String ownerId = extra[0];
			RequestCallback callback = getCallbacker(ownerId);
			if (callback == null) {
				QLog.e(TAG, "handle FindCallback is error!!!");
				return;
			}
			String tag = extra[1];
		    int chapid = Integer.parseInt(extra[2]);
			if (e == null) {
			    callback.onGetQuestionCountSucceed(tag, chapid, count, DoctorConst.FROM_NETWORK);
			}
			else {
				callback.onGetQuestionCountFailed(tag, chapid, DoctorConst.FROM_NETWORK);
			}
		}
	}
	
	
	@Override
	public void getQuestions(RequestCallback callback, String tag, int from, int count) {
		String owner = getSubScriberKey(callback);
		if (owner == null) {
			return;
		}
		QLog.e(TAG, "getQuestions from:" + from + "|" + count);
	    AVQuery<AVObject> query = new AVQuery<AVObject>("question");
	    query.whereGreaterThanOrEqualTo(QuestionColumns.ORDER, from);
	    query.whereLessThan(QuestionColumns.ORDER, from + count);
	    
	    String callbackTag = owner + DoctorConst.SEPRATOR + tag 
	    			+ DoctorConst.SEPRATOR + from 
	    			+ DoctorConst.SEPRATOR + count;

	    query.findInBackground(new MyFindCallback(callbackTag));
	        
	}
	
	
	/**
	 * 查询问题列表回调接口
	 */
    class MyFindCallback extends FindCallback<AVObject> {
    	String mTag;
    	public MyFindCallback(String tag) {
    		mTag = tag;
    	}
    	
		@Override
		public void done(List<AVObject> list, AVException e) {
			String[] extra = mTag.split(DoctorConst.SEPRATOR);
			String ownerId = extra[0];
			RequestCallback callback = getCallbacker(ownerId);
			if (callback == null) {
				QLog.e(TAG, "handle FindCallback is error!!!");
				return;
			}
			
			String tag = extra[1];
		    int start = Integer.parseInt(extra[2]);
		    int count = Integer.parseInt(extra[3]);
		    
			if (e == null) {

				List<Question> ql = new ArrayList<Question>();
				for(AVObject avObject : list) {
					Question q = new Question();
					q.setQId(avObject.getInt(QuestionColumns.QID));
					q.setTitle(avObject.getString(QuestionColumns.TITLE));
					q.setOption(avObject.getString(QuestionColumns.OPTION));
					q.setAnswer(avObject.getString(QuestionColumns.ANSWER));
					q.setAttr(avObject.getInt(QuestionColumns.ATTR));
					q.setChapter(avObject.getInt(QuestionColumns.CHAPTER));
					q.setPicture(avObject.getString(QuestionColumns.PICTURE));
					q.setAnalysis(avObject.getString(QuestionColumns.DETAIL_ANALYSIS));
	                Date updatedAt = avObject.getUpdatedAt();
					q.setUpdateAt(updatedAt.getTime());
					q.setPos(avObject.getInt(QuestionColumns.ORDER));
					ql.add(q);
	                
	                QLog.i(TAG, "getQuestions:" + q.toString());
				}

			    callback.onGetQuestionsSucceed(tag, start, count, ql, DoctorConst.FROM_NETWORK);
			}
			else {
				e.printStackTrace();
				callback.onGetQuestionsFailed(ownerId, start, count, DoctorConst.FROM_NETWORK);
			}
		}
    }

	@Override
	public void getChapter(RequestCallback callback, String tag, long updateAt) {

		String owner = getSubScriberKey(callback);
		if (owner == null) {
			return;
		}
		QLog.i(TAG, "getChapter ");
	    AVQuery<AVObject> query = new AVQuery<AVObject>("chapter");
	    query.whereGreaterThan(ChapterEntry.ChapterColumns.COLUMN_UPDATE_AT, new Date(updateAt));

	    String callbackTag = owner + DoctorConst.SEPRATOR + tag 
	    			+ DoctorConst.SEPRATOR + updateAt;

	    query.findInBackground(new MyFindChapterCallback(callbackTag));
	}
	
	/**
	 * 查询章节列表回调接口
	 */
	public class MyFindChapterCallback extends FindCallback<AVObject> {
		String mTag;
		public MyFindChapterCallback(String tag) {
			mTag = tag;
		}
		
		@Override
		public void done(List<AVObject> list, AVException e) {
			String[] extra = mTag.split(DoctorConst.SEPRATOR);
			String ownerId = extra[0];
			RequestCallback callback = getCallbacker(ownerId);
			if (callback == null) {
				QLog.i(TAG, "handle MyFindChapterCallback is error!!!");
				return;
			}
			
			String tag = extra[1];
		    long maxUpdateAt = Long.parseLong(extra[2]);
			if (e == null) {

				List<Chapter> ql = new ArrayList<Chapter>();
				for (AVObject avObject : list) {
					Chapter q = new Chapter();
					q.setCId(avObject.getInt(ChapterEntry.ChapterColumns.COLUMN_CID));
					q.setLevel(avObject.getInt(ChapterEntry.ChapterColumns.COLUMN_LEVEL));
					q.setDesc(avObject.getString(ChapterEntry.ChapterColumns.COLUMN_DESC));
					q.setOrder(avObject.getInt(ChapterEntry.ChapterColumns.COLUMN_ORDER));
					q.setQuestionCount(avObject.getInt(ChapterEntry.ChapterColumns.COLUMN_QUESTION_COUNT));
	                long updatedAt = avObject.getUpdatedAt().getTime();
					q.setUpdateAt(updatedAt);
					ql.add(q);
	                
					if (updatedAt > maxUpdateAt) {
						maxUpdateAt = updatedAt;
					}
	                QLog.i(TAG, "getChapters:" + q.toString());
				}

			    callback.onGetChapterSucceed(tag, maxUpdateAt, ql, DoctorConst.FROM_NETWORK);
			}
			else {
				e.printStackTrace();
				callback.onGetChapterFailed(ownerId, maxUpdateAt, DoctorConst.FROM_NETWORK);
			}
		}
	}
}


