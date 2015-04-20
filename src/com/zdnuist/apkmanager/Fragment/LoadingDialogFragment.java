package com.zdnuist.apkmanager.Fragment;

import com.zdnuist.apkmanager.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class LoadingDialogFragment extends DialogFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.loading_dialog, container, false);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//配置Dialog全屏  
        setStyle(DialogFragment.STYLE_NORMAL,  
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);  
	}
}
