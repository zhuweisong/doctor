package com.houfubao.doctor.activity;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;

import com.houfubao.doctor.R;
import com.houfubao.doctor.R.id;
import com.houfubao.doctor.R.layout;
import com.houfubao.doctor.R.mipmap;
import com.houfubao.doctor.R.string;
import com.houfubao.doctor.logic.main.DoctorConst;
import com.houfubao.doctor.logic.main.DoctorState;
import com.houfubao.doctor.logic.online.QuestionManager;
import com.houfubao.doctor.logic.utils.PreferencesUtils;
import com.houfubao.doctor.view.tabpager.APSTSViewPager;
import com.houfubao.doctor.view.tabpager.AdvancedPagerSlidingTabStrip;

/**
 * Created by sevenzhu on 2016/5/18.
 */
public class MainTabActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener{
	private static final String TAG = "MainTabActivity";

	
    public AdvancedPagerSlidingTabStrip mAPSTS;
    public APSTSViewPager mVP;
    ActionBar mactionBar;
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_tab);
 
		
        initActionBar();
        initView();
    }

    private void initView(){
        mAPSTS = (AdvancedPagerSlidingTabStrip)findViewById(R.id.tabs);
        mVP = (APSTSViewPager)findViewById(R.id.vp_main);
        

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        mVP.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        mVP.setOffscreenPageLimit(adapter.getCount());
        mAPSTS.setViewPager(mVP);
        mAPSTS.setOnPageChangeListener(this);
        mVP.setCurrentItem(0);
//        mAPSTS.showDot(VIEW_FIRST,"99+");
        adapter.notifyDataSetChanged();
    }
    
    private void initActionBar() {
    	mactionBar = getSupportActionBar();
        int flags = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME ;
        
        mactionBar.setDisplayOptions(0, flags);
        mactionBar.setTitle(R.string.app_name);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //----------------------------------------------------------//
    //------------------------Fragment Adapter----------------//
    
    private static class MyTAB {
    	public MyTAB(Class<? extends Fragment> cls, int nameid, int pageicon, int pageSelectIcon) {
    		this.fragmentClass = cls;
    		this.titleId = nameid;
    		this.pageIcon = pageicon;
    		this.pageSelectIcon = pageSelectIcon;
    	}
    	public int titleId;
    	public int pageIcon;
    	public int pageSelectIcon;
    	public Class<? extends Fragment> fragmentClass;
    };
    
	private static final List<MyTAB> TABS = new ArrayList<MyTAB>();
	private SparseArray<Fragment> mFragments = new SparseArray<Fragment>();
	
	static {
		TABS.add(new MyTAB(FirstFragment.class, R.string.main_tab_examine,R.mipmap.home_main_icon_n, R.mipmap.home_main_icon_f_n));
		TABS.add(new MyTAB(SecondFragment.class, R.string.main_tab_examine, R.mipmap.home_categry_icon_n, R.mipmap.home_categry_icon_f_n));
		TABS.add(new MyTAB(ThirdFragment.class, R.string.main_tab_examine, R.mipmap.home_live_icon_n, R.mipmap.home_live_icon_f_n));
		TABS.add(new MyTAB(FourthFragment.class, R.string.main_tab_mine, R.mipmap.home_mine_icon_n, R.mipmap.home_mine_icon_f_n));
	}
	
    public class FragmentAdapter extends FragmentStatePagerAdapter implements AdvancedPagerSlidingTabStrip.IconTabProvider{

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	Fragment fragment = null;
            if(position >= 0 && position < TABS.size()){
            	fragment = mFragments.get(position);
            	if (fragment == null) {
            		try {
            			fragment = TABS.get(position).fragmentClass.newInstance();
            			mFragments.put(position, fragment);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TABS.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position >= 0 && position < TABS.size()){
            	return getResources().getString(TABS.get(position).titleId);  
            }
            return null;
        }

        @Override
        public Integer getPageIcon(int index) {
            if(index >= 0 && index < getCount()){
            	return TABS.get(index).pageIcon;
            }
            return 0;
        }

        @Override
        public Integer getPageSelectIcon(int index) {
            if(index >= 0 && index < getCount()){
            	return TABS.get(index).pageSelectIcon;
            }
            return 0;
        }

        @Override
        public Rect getPageIconBounds(int position) {
            return null;
        }
    }
}
