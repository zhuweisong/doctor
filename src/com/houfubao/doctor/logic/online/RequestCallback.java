package com.houfubao.doctor.logic.online;

import java.util.List;

public abstract class RequestCallback {
	
	/**
	 * 获取题目列表的回调
	 */
	public void onGetQuestionsSucceed(String ownerId, int start, int count, List<Question> questions, int from) {}
	public void onGetQuestionsFailed(String ownerId, int start, int count) {}
	
	
	
}
