package com.houfubao.doctor.logic.utils;

import com.houfubao.doctor.logic.main.DoctorConst;

import android.util.Log;

public class QLog {

	public static String TAG = "doctor";

	public static void i(String tag, String msg) {
		if (DoctorConst.DEBUG) {
			Log.i(TAG, tag + ": " + msg);
		}
	}

	public static void d(String tag, String msg) {
		if (DoctorConst.DEBUG) {
			Log.d(TAG,  tag + ": " + msg);
		}
	}

	public static void v(String tag, String msg) {
		if (DoctorConst.DEBUG) {
			Log.v(TAG,  tag + ": " + msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DoctorConst.DEBUG) {
			Log.w(TAG,  tag + ": " + msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DoctorConst.DEBUG) {
			Log.e(TAG,  tag + ": " + msg);
		}
	}
	
	public static void e(String tag, String msg, Exception e) {
		if (DoctorConst.DEBUG) {
			Log.d(TAG,  tag + ": " + msg,  e);
		}
	}

}