package com.houfubao.doctor.logic.main;

/**
 *  for the subSrciber ID
 * @author sevenzhu
 */

public abstract class DataResultCallbackBase {
	public String getOwnerId() {
		return String.valueOf(hashCode());
	}
}
