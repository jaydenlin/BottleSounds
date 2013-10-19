package com.wearapp;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

public class ListenActivity extends Activity implements OnClickListener{

	
	///////////////////////////////////////////
	// debug
	///////////////////////////////////////////	
	
	public static final String TAG = ListenActivity.class.getSimpleName();

	public static final boolean D = true;
	public static final boolean D_METHOD = D && true;
	
	public static final boolean D_SHOW_TAG = D && true;
	
	public float startTime;
	
	///////////////////////////////////////////
	// UI
	///////////////////////////////////////////		


	///////////////////////////////////////////
	// handler
	///////////////////////////////////////////			

	UIHandler mUIHandler;

	class UIHandler extends Handler {
		public void handleMessage(Message msg) {

		}
	}
	
	
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		if (D_METHOD) {
			Log.w(TAG, "onStart");
		}

	}
	@Override
	protected void onResume() {
		super.onResume();
		if (D_METHOD) {
			Log.w(TAG, "onResume");
		}
	}

	@Override
	protected void onPause() {
		if (D_METHOD) {
			Log.w(TAG, "onPause");
		}
		
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (D_METHOD) {
			Log.w(TAG, "onStop");
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (D_METHOD) {
			Log.w(TAG, "onDestroy");
		}
		super.onDestroy();
	}
}
