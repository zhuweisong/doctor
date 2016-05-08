package com.houfubao.doctor.logic.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class DoctorUtil {
	
	  public static final int dpToPx(float dp, Resources res) {
		    return (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_DIP,
		        dp,
		        res.getDisplayMetrics());
		  }
}
