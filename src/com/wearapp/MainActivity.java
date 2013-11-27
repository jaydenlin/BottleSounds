package com.wearapp;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_first);
		initButton();
		setListener();
	}

	
	
	// /////////////////////////////////////////
		// debug
		// /////////////////////////////////////////

		public static final String TAG = MainActivity.class.getSimpleName();

		public static final boolean D = true;
		public static final boolean D_METHOD = D && true;

		public static final boolean D_SHOW_TAG = D && true;

		public float startTime;

		// /////////////////////////////////////////
		// UI
		// /////////////////////////////////////////

		ImageButton button_facebook;
		Button button_direct;
		

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
			button_facebook = (ImageButton) findViewById(R.id.button_facebook);
			button_direct = (Button) findViewById(R.id.button_direct);
		}

		public void setListener() {
			Log.w(TAG, "initListener");

			button_facebook.setOnClickListener(mainlistener);
			button_direct.setOnClickListener(mainlistener);

		}

		public void startDirect() {
			Intent intent = new Intent(this, SecondMainActivity.class);
			startActivity(intent);
			return;
		}

		public void startFacebook() {
			Intent intent = new Intent(this, CheckPlaceActivity.class);
			startActivity(intent);
			return;
		}

		private ImageButton.OnClickListener mainlistener = new OnClickListener() {
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.button_facebook:
					startFacebook();
					return;

				case R.id.button_direct:
					startDirect();
					return;
				}

			}

		};
}
