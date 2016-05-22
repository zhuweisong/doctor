package com.houfubao.doctor.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ViewPagerFixedSpeedScoller extends Scroller {

	 private int mDuration = 1500; // default time is 1500ms  
	  
	    public ViewPagerFixedSpeedScoller(Context context) {  
	    	super(context);  
	    }
	  
	    public ViewPagerFixedSpeedScoller(Context context, Interpolator interpolator) {  
	    	super(context, interpolator);  
	    }  
	  
	    @Override  
	    public void startScroll(int startX, int startY, int dx, int dy, int duration) {  
	    	// Ignore received duration, use fixed one instead  
	    	super.startScroll(startX, startY, dx, dy, mDuration);  
	    }  
	  
	    @Override  
	    public void startScroll(int startX, int startY, int dx, int dy) {  
	    	// Ignore received duration, use fixed one instead  
	    	super.startScroll(startX, startY, dx, dy, mDuration);  
	    }
	    
	    
	  
	    @Override
		public void fling(int startX, int startY, int velocityX, int velocityY,
				int minX, int maxX, int minY, int maxY) {
			// TODO Auto-generated method stub
			super.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
		}

		/** 
	     * set animation time 
	     *  
	     * @param time 
	     */  
	    public void setmDuration(int time) {  
	    	mDuration = time;  
	    }  
	  
	    /** 
	     * get current animation time 
	     *  
	     * @return 
	     */  
	    public int getmDuration() {  
	    	return mDuration;  
	    }  
	
}
