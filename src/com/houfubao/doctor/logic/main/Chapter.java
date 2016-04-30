package com.houfubao.doctor.logic.main;

import java.util.Date;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

@AVClassName("Chapter")
public class Chapter extends AVObject {
	//此处为我们的默认实现，当然你也可以自行实现
	  public static final Creator CREATOR = AVObjectCreator.instance;
	  
	  public static final String QID = "cid";
	  public static final String TITLE = "title";
	  public static final String OPTION = "option";

	  public static final String UPDATE_AT = "updatedAt";
	  
	  public Chapter() {
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

	  
	  public Date getUpdateAt() {
		   return getDate(UPDATE_AT);
	  }
}
