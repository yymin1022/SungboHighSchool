package com.sungbospot.lunch;
import android.app.*;
import android.os.*;
import android.content.*;
import android.widget.*;
import android.graphics.drawable.*;

public class ActivitySplash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Handler hd = new Handler();
		hd.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	            finish();
	        }
	    }, 1500);
	}
}
