package com.houfubao.doctor.logic.utils;

import com.houfubao.doctor.logic.main.DoctorConst;


public class VinsonAssertion { 

	public static void Assert(boolean expression, String why){ 
		if (DoctorConst.DEBUG && !expression){ 
			QLog.e("VinsonAssertion", why);
			throw new RuntimeException(why);
		}
	}
}
 