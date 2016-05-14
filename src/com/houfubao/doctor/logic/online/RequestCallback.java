package com.houfubao.doctor.logic.online;

import java.util.List;

public abstract class RequestCallback {
	
	/**
	 * 获取题目列表的回调
	 */
	public void onGetQuestionsSucceed(String tag, int start, int count, List<Question> questions, int from) {}
	public void onGetQuestionsFailed(String tag, int start, int count, int from) {}
	
	/**
	 * 章节回调
	 */
	public void onGetChapterSucceed(String tag, long updateAt, List<Chapter> chapters, int from){}
	public void onGetChapterFailed(String tag, long updateAt, int from){}
	
	
	public void onGetQuestionCountSucceed(String tag, int chapter, int count, int from) {}
	public void onGetQuestionCountFailed(String tag,  int chapter, int from) {}

}
