package com.houfubao.doctor.activity;

import com.houfubao.doctor.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by linhonghong on 2015/8/11.
 */
public class SecondFragment extends Fragment {

    public static SecondFragment instance() {
        SecondFragment view = new SecondFragment();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second_fragment, null);
        return view;
    }
}