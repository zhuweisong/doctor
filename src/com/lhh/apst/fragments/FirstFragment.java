package com.lhh.apst.fragments;

import com.houfubao.doctor.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by linhonghong on 2015/8/11.
 */
public class FirstFragment extends Fragment{

    public static FirstFragment instance() {
        FirstFragment view = new FirstFragment();
		return view;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, null);
        return view;
    }
}
