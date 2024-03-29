package com.wearapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class SecondMainActivity extends Activity {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////

	public static final String TAG = SecondMainActivity.class.getSimpleName();

	public static final boolean D = true;
	public static final boolean D_METHOD = D && true;

	public static final boolean D_SHOW_TAG = D && true;

	public float startTime;

	// /////////////////////////////////////////
	// UI
	// /////////////////////////////////////////

	Button button_listen;
	Button button_record;

	// /////////////////////////////////////////
	// handler
	// /////////////////////////////////////////

	UIHandler mUIHandler;

	class UIHandler extends Handler {
		public void handleMessage(Message msg) {

		}
	}

	public boolean onCreateOptionMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initButton();
		setListener();

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

	public void initButton() {
		button_listen = (Button) findViewById(R.id.button_message);
		button_record = (Button) findViewById(R.id.button_record_again);
	}

	public void setListener() {
		Log.w(TAG, "initListener");

		button_listen.setOnClickListener(mainlistener);
		button_record.setOnClickListener(mainlistener);

	}

	public void startRecord() {
		Intent intent = new Intent(this, RecordActivity.class);
		startActivity(intent);
		return;
	}

	public void startListen() {
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
		return;
	}
	
	
	
	

	private ImageButton.OnClickListener mainlistener = new OnClickListener() {
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.button_message:
				//startListen();
				startListen();
				return;

			case R.id.button_record_again:
				startRecord();
				return;
			}

		}

	};

}
