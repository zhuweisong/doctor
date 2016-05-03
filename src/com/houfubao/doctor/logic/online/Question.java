package com.houfubao.doctor.logic.online;

public class Question {
	@Override
	public String toString() {
		return pos + "|" + title + "|" 
				+ option + "|" + answer + "|" 
				+ chapter + "|" + analysis;
	}
	
	private int id;
	private int attr;	
	private String title;
	private String option;
	private String answer;
	private int chapter;
	private String picture;
	private String analysis;
	private long updateAt;
	private int pos;
	
	  public Question() {}

	  public Question(Question question) {
		  this.id = question.id;
		  this.attr = question.attr;
		  this.title = question.title;
		  this.option = question.option;
		  this.answer = question.answer;
		  this.chapter = question.chapter;
		  this.picture = question.picture;
		  this.analysis = question.analysis;
		  this.updateAt = question.updateAt;
	  }

	  
	  public Question setQId(int qid) { this.id = qid; return this; }
	  public Question setTitle(String title) {this.title = title; return this; }
	  public Question setOption(String option) {this.option = option; return this;} 
	  public Question setAnswer(String answer) {this.answer = answer; return this; }
	  public Question setAttr(int attr) {this.attr = attr; return this;}	  
	  public Question setChapter(int chapter) {this.chapter = chapter; return this;}
	  public Question setPicture(String picture) {this.picture = picture; return this;}
	  public Question setAnalysis(String analysis) {this.analysis = analysis; return this;}
	  public Question setUpdateAt(long updateAt) {this.updateAt = updateAt; return this; }
	  public Question setPos(int pos) {this.pos = pos;return this;}
	  
	  public int getQId() { return id; }
	  public String getTitle() { return title; }
	  public String getOption() {return option;} 
	  public String getAnswer() {return answer; }
	  public int getAttr() { return attr;}	  
	  public int getChapter() {return chapter;}
	  public String getPicture() {return picture;}
	  public String getAnalysis() {return analysis;}
	  public long getUpdateAt() { return updateAt; }
	  public int getOrder() {return pos;}
}
