package com.houfubao.doctor.online;

import com.houfubao.doctor.logic.main.BaseCallBack;
import com.houfubao.doctor.logic.main.DataResultCallbackBase;
import com.houfubao.doctor.logic.main.DoctorState.NetworkStateChanged;



public class onlineDataManager extends BaseCallBack<onlineDataManager.OnlineDataResultCallback> 
	implements NetworkStateChanged {

	
	/**
	 * 从后台服务器获取信息时的回调 
	 * @author sevenzhu
	 */
    public static abstract class OnlineDataResultCallback extends DataResultCallbackBase {
    	void onGetQuestion(){}
    	
    }

    public void getQuestion(OnlineDataResultCallback callback, int from, int to) {
    	
    }
    
    
    
	@Override
	public void onNetworkStateChanged(boolean isConnected) {
		// TODO Auto-generated method stub
		
	}

}
    	