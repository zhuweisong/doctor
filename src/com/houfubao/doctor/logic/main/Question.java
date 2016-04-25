package com.houfubao.doctor.logic.main;

import java.util.Date;

import com.avos.avoscloud.AVObject;

public class Question extends AVObject {
	  public static final String QID = "qid";
	  public static final String TITLE = "title";
	  public static final String OPTION = "option";
	  public static final String ANSWER = "sAnswer";
	  public static final String ATTR = "lAttr";
	  public static final String CHAPTER = "iChapter";
	  public static final String PICTURE = "sPicture";
	  public static final String DETAIL_ANALYSIS = "sDetailAnalysis";
	  public static final String UPDATE_AT = "updatedAt";
	  
	  public Question() {
	  }
	  
	  public int getQId() {
		  return getInt(QID);
	  }
	  
	  public String getTitle() {
		  return getString(TITLE);
	  }
	  
	  public String getOption() {
		  return getString(OPTION);
	  }
	  
	  public String getAnswer() {
		  return getString(ANSWER);
	  }
	  
	  public int getAttr() {
		  return getInt(ATTR);
	  }
	  
	  public String getChapter() {
		  return getString(CHAPTER);
	  }
	  
	  public String getPicture() {
		  return getString(PICTURE);
	  }
	  
	  public String getAnalysis() {
		  return getString(DETAIL_ANALYSIS);
	  }
	  
	  public Date getUpdateAt() {
		   return getDate(UPDATE_AT);
	  }
}
