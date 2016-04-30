package com.houfubao.doctor.logic.online;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.utils.VinsonAssertion;

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
	public void getQuestions(RequestCallback callback, String tag, int from, int count) {
		
	    AVQuery<AVObject> query = new AVQuery<AVObject>("question");
	        query.whereGreaterThanOrEqualTo(QuestionColumns.ORDER, from);
	        query.whereLessThan(QuestionColumns.ORDER, from + count);
	        query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> list, AVException arg1) {	
					for(AVObject avObject : list) {
						// 获取三个特殊属性
						
						int order = avObject.getInt(QuestionColumns.ORDER);
						String title = avObject.getString("title");
		                String objectId = avObject.getObjectId();
		                Date updatedAt = avObject.getUpdatedAt();
		                Date createdAt = avObject.getCreatedAt();
		                
		                Log.i(TAG, "getQuestions:" + order + "|" + title + "|" + objectId + "|" + updatedAt + "|" + createdAt);
					}
				}
	        });
		
	}
	
	
	
	
}


