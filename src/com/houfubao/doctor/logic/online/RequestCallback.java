package com.houfubao.doctor.logic.online;

import java.util.List;

public abstract class RequestCallback {
	
	/**
	 * 获取题目列表的回调
	 */
	public void onGetQuestionsSucceed(String tag, int start, int count, List<Question> questions, int from) {}
	public void onGetQuestionsFailed(String tag, int start, int count) {}
	
	public void onGetQuestionCountSucceed(String tag, int chapter, int count, int from) {}
	public void onGetQuestionCountFailed(String tag,  int chapter) {}
	
}
