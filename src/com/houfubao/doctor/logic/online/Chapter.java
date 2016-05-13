package com.houfubao.doctor.logic.online;


public class Chapter {

	private int cid;
	private int level;
	private String desc;
	private int order;
	private int questionCount;
	private long updatedAt;

	public Chapter(){
		
	}
	
	public Chapter(Chapter chapter) {
		this.cid = chapter.cid;
		this.level = chapter.level;
		this.desc = chapter.desc;
		this.order = chapter.order;
		this.questionCount = chapter.questionCount;
		this.updatedAt = chapter.updatedAt;
	}
	
	@Override
	public String toString() {
		return cid + "|" + cid + "|" 
				+ desc + "|" + order + "|" 
				+ questionCount + "|" + updatedAt;
	}
	
	public Chapter setCId(int cid){this.cid = cid; return this;}
	public Chapter setLevel(int level){this.level = level; return this;}
	public Chapter setDesc(String desc){this.desc = desc; return this;}
	public Chapter setOrder(int order){this.order = order; return this;}
	public Chapter setQuestionCount(int questionCount){this.questionCount = questionCount; return this;}
	public Chapter setUpdateAt(long updatedAt){this.updatedAt = updatedAt; return this;} 
	
	public int getCId() {return cid;}
	public int getLevel() {return level;}
	public String getDesc() {return desc;}
	public int getOrder() {return order;}
	public int getQuestionCount() {return questionCount;}
	public long getUpdateAt() {return updatedAt;}
	
}
