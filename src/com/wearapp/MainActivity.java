package com.wearapp;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////

	public static final String TAG = MainActivity.class.getSimpleName();
	public static final boolean D = true;
	public static final boolean D_METHOD = D && true;
	public static final boolean D_SHOW_TAG = D && true;
	public float startTime;
	
	// /////////////////////////////////////////
	// Members
	// /////////////////////////////////////////
	private UiLifecycleHelper FBlifecycleHelper;
	
	
	// /////////////////////////////////////////
	// UI
	// /////////////////////////////////////////

	Button button_facebook;
	Button button_direct;

	public void initButton() {
		button_facebook = (Button) findViewById(R.id.button_facebook);
		button_direct = (Button) findViewById(R.id.button_direct);
	}

	// /////////////////////////////////////////
	// Listener
	// /////////////////////////////////////////
	private ImageButton.OnClickListener mainlistener = new OnClickListener() {
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.button_facebook:
				startFacebook();
				//startHistory();
				return;

			case R.id.button_direct:
				startDirectly();
				return;
			}
		}
	};

	public void setListener() {
		Log.w(TAG, "initListener");
		button_facebook.setOnClickListener(mainlistener);
		button_direct.setOnClickListener(mainlistener);
	}

	// /////////////////////////////////////////
	// Life Cycle
	// /////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_first);
		
		FBlifecycleHelper = new UiLifecycleHelper(this,
				new Session.StatusCallback() {
					@Override
					public void call(Session session, SessionState state,
							Exception exception) {
						// onSessionStateChanged(session, state, exception);
					}
				});
		FBlifecycleHelper.onCreate(savedInstanceState);
		
		initButton();
		setListener();
		/*Make the screen horizon*/
		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	
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
		FBlifecycleHelper.onResume();
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
		FBlifecycleHelper.onPause();
	}

	@Override
	protected void onStop() {
		if (D_METHOD) {
			Log.w(TAG, "onStop");
		}
		super.onStop();
		FBlifecycleHelper.onStop();
	}

	@Override
	protected void onDestroy() {
		if (D_METHOD) {
			Log.w(TAG, "onDestroy");
		}
		super.onDestroy();
		FBlifecycleHelper.onDestroy();
	}

	// /////////////////////////////////////////
	// Methods
	// /////////////////////////////////////////

	public void startDirectly() {
		Intent intent = new Intent(this, SecondMainActivity.class);
		startActivity(intent);
		return;
	}

	public void startFacebook() {
		ensureOpenFBSession();
		Intent intent = new Intent(this, CheckVoiceActivity.class);
		startActivity(intent);
		return;
	}
	
	public void startHistory(){
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
		return;	
	}
	
	private boolean ensureOpenFBSession() {
		if (Session.getActiveSession() == null
				|| !Session.getActiveSession().isOpened()) {
			Session.openActiveSession(this, true, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
//					Intent intent = new Intent(MainActivity.this, SecondMainActivity.class);
//					MainActivity.this.startActivity(intent);
				}
			});
			return false;
		}
		return true;
	}

}
