package com.houfubao.doctor;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.Question;
import com.houfubao.doctor.logic.utils.QLog;

/**
 * ClassName: VinsonApplication <br/>
 * Function: 系统属性优化<br/>
 * date: 2015年4月8日 下午1:02:13 <br/>
 * 
 * @version
 * @since JDK 1.6
 */
public class DoctorApplication extends Application {

	private final static String TAG = "DoctorApplication";

	@Override
	public void onCreate() {
		super.onCreate();

		DoctorState.setApplicationContext(this);
		DoctorState.getInstance();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		QLog.w(TAG, "onTerminate");
		DoctorState.getInstance().onTerminate();
	}

	@Override
	public void onLowMemory() {
		QLog.w(TAG, "onLowMemory");
		super.onLowMemory();
	}

}
