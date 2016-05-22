package com.houfubao.doctor.logic.online;

import java.util.List;

import android.util.SparseArray;

import com.houfubao.doctor.logic.main.BaseCallBack;
import com.houfubao.doctor.logic.main.DataResultCallbackBase;

public class QuestionManager extends BaseCallBack<QuestionManager.QuestionResultCallback> {
	
	/**
	 * 从获取信息时的回调
	 */
    public static abstract class QuestionResultCallback extends DataResultCallbackBase {
    	//@override
    	public void onGetQuestionSucceed(int from, int count, List<Question> list){}
    	public void onGetQuestionFailed(int from, int count){}
    	
    	//@override
    	public void onGetQuestionSucceed(int pos, Question q){}
    	public void onGetQuestionFailed(int pos){}
    	
    	public void onGetChapterSucceed(List<Chapter> list){}
      	public void onGetChapterFailed(){}
    	
    	//@override
    	public void onGetQuestionCountSucceed(int chapterId, int count) {}
    	public void onGetQuestionCountFailed(int chapterId) {}
    }

    /**
     * 分页获取当前题目
     */
    public void getQuestion(QuestionResultCallback callback, int from, int count) { }

    
    /**
     * 获取指定位置的题目
     */
    public void getQuestion(QuestionResultCallback callback, int pos) { }
    
    
    /**
     * 获取当前章节所有题目个数,-1表示全部
     */
    public void getQuestionCount(QuestionResultCallback callback, int chapterId) {}
    
    /**
     * 获取当前章节所有题目个数,-1表示全部
     */
    public void getChapterInfo(QuestionResultCallback callback) {}
    


    
    public static int calcQuestionCountByChapter(List<Chapter> chapters) {
    	if (chapters == null || chapters.size()==0) {
    		return 0;
    	}
    	int total = 0;
		for (Chapter chapter : chapters) {
			if (chapter.getLevel() == 1){
				total += chapter.getQuestionCount();
			}
		}
		return total;
    }
}
    	