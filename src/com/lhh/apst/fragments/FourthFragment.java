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
public class FourthFragment extends Fragment {

    public static FourthFragment instance() {
        FourthFragment view = new FourthFragment();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fourth_fragment, null);
        return view;
    }
}