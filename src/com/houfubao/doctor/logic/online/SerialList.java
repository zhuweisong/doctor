package com.houfubao.doctor.logic.online;

import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.logic.utils.QLog;
import com.houfubao.doctor.logic.utils.VinsonAssertion;


public class SerialList {
	private static String TAG = "SerialList";
	private ArrayList<SingleList> mlist = new ArrayList<SerialList.SingleList>();
	private int mTotal =  Integer.MAX_VALUE; //初始为最大值
	
	private static class SingleList {
		public int startpos;
		public List<Question> appRsp;
		
		public SingleList(int startpos, List<Question> appRsp) {
			this.startpos = startpos;
			this.appRsp = appRsp;
		}
	}
	
	public int getITotal() {
		return mTotal;
	}
	
	public List<Question> index(int indexstart, int indexend) { 
		
		VinsonAssertion.Assert(indexstart <= mTotal, " para error 1");
		VinsonAssertion.Assert(indexend <= mTotal, " para error 2");
		
		List<Question> appRsp = null;
		for (SingleList single : mlist) {
			int start = single.startpos;
			int end = start + single.appRsp.size();
			
			if (start <= indexstart && indexend <= end) {
				appRsp = new ArrayList<Question>();
				
				int substart = indexstart - start;
				int subsize = indexend - indexstart;
				List<Question> sublist = single.appRsp.subList(substart, substart +subsize);
				appRsp.addAll(sublist);
				break;
			}
		}
		return appRsp;
	}
	
	public void clear() {
		mlist.clear();
		mTotal = Integer.MAX_VALUE;
	}
	
	public List<Question> getFirstCacheList() {
		ArrayList<Question> result = null;
		if (mlist.size()>0) {
			SingleList single = mlist.get(0);
			if (single.startpos == 0) {
				return mlist.get(0).appRsp;
			}
		}
		
		return result;
	}
	
	public void add(int total, List<Question> rsp, int rsp_startpos) { 
		
		if (rsp.size() <= 0) {
			QLog.e(TAG, "add error :"  + "|" + rsp.size());
			return;
		}
		
		mTotal = total;
		
		QLog.i(TAG, "add :" + rsp_startpos + "|" + rsp.size() + "|" + mlist.size());
		
		SingleList single;
		if (mlist.size()==0) {
			single = new SingleList(rsp_startpos, rsp);
			mlist.add(single);
			return;
		}
		for (SingleList s : mlist) {
			QLog.i(TAG, "add test :" + s.startpos + "|" + s.appRsp.size() + "|");
		}

		for (int index = mlist.size() - 1; index >= 0; index--) {
			SingleList org = mlist.get(index);
			int start = org.startpos;
			int end = org.startpos + org.appRsp.size();
			
			int rsp_end =  rsp_startpos + rsp.size();
			
			QLog.i(TAG, "for add index :" + index + " start:" + start + "|" + end + "rsp_end:" + rsp_end);
			
			if (rsp_startpos < start && index==0) { //
				single = new SingleList(rsp_startpos, rsp);
				mlist.add(index, single);
				//在当前段的后部分，删除重叠部分
				removeRemain(index);
				break;
			}
			else 
				if (rsp_startpos >= start && rsp_startpos < end) {
				
				int realstart = rsp_startpos - start;
				if (rsp_end < end) {
					QLog.i(TAG, "for add replace " + realstart);
					//从网络返回的数据，好位于当前一段的中间部分，直接替换
					replace(org.appRsp, realstart, rsp.size(), rsp);
					break;
				}
				else {
					QLog.i(TAG, "for add remove  realstart:" + realstart+ " end:" + org.appRsp.size());
					//网络返回数据，超过当前段部分，先移掉当前段的后面部分，再直接加上返回数据；
					remove(org.appRsp, realstart, org.appRsp.size());
					org.appRsp.addAll(rsp);
					//在当前段的后部分，删除重叠部分
					removeRemain(index);
					break;
				}
			}  else if (rsp_startpos == end) { 
				//和最后一个对接上
				org.appRsp.addAll(rsp);
				removeRemain(index);
				break;
			} else if (rsp_startpos > end) {
				single = new SingleList(rsp_startpos, rsp);
				mlist.add(index, single);
				break;
			}
		}
		dump();
	}
		
	private void replace(List<Question>  org, int start, int size, List<Question>  rsp) {
		for (int i = 0; i < size; i++) {
			Question rspInfo = rsp.get(i);
			org.set(i+start, rspInfo);
		}
	}
	
	private void remove(List<Question> org,  int start , int end) {
		VinsonAssertion.Assert(org != null, "org error");
		VinsonAssertion.Assert(start >=0 && start < org.size() , "start error:" + start + "|" + org.size());
		VinsonAssertion.Assert(end >0 && end <= org.size() && end>=start, "end error:" + start + "|" +  end + "|" + org.size());
		
		for (int i=end-1;i>=start;i--) {
			org.remove(i);
		}
	}

	private void removeRemain(int i) {
		SingleList newList = mlist.get(i);
		int new_start = newList.startpos;
		int new_end = new_start + newList.appRsp.size();
		
		for ( int index =mlist.size()-1; index >= i + 1 ; index--) {
			
			SingleList remain = mlist.get(index);
			int remain_start = remain.startpos;
			int remain_end = remain_start + remain.appRsp.size();	
			
			QLog.i(TAG, "removeRemain :" + i + "|" + index + "remain_start:" + remain_start + "remain_end:" + remain_end + "|" + new_end + "|");
			
			if (remain_end< new_end) { //the all  remain list is  covered
				mlist.remove(index);
			}
			else if (remain_start <= new_end){ //the part of remain list is covered, merge
				mlist.remove(index); 
				int mergestart = new_end - remain_start;
				int mergesize = remain.appRsp.size() - mergestart;
				List<Question> sublist = remain.appRsp.subList(mergestart, mergestart +mergesize);
				newList.appRsp.addAll(sublist);
			}
		}
	}
	
	public void dump() {
		QLog.i(TAG, "dump start");
		for (int index = mlist.size() - 1; index >= 0; index--) {
			SingleList org = mlist.get(index);
		}
		QLog.i(TAG, "dump end");
	}
}

