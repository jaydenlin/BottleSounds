package com.wearapp;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.wearapp.resultcode.ResultCode;
import com.wearapp.util.FacebookOpenSessionDoneDelegate;
import com.wearapp.util.FacebookUtil;
import com.wearapp.util.LocateLocationDoneDelegate;
import com.wearapp.util.LocationUtil;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class CheckVoiceActivity extends FragmentActivity {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////
	public static final String TAG = CheckVoiceActivity.class.getSimpleName();

	// /////////////////////////////////////////
	// UI
	// /////////////////////////////////////////

	// /////////////////////////////////////////
	// Initialize
	// /////////////////////////////////////////
	private UiLifecycleHelper FBlifecycleHelper;
	private Location lastKnownLocation;
	private boolean isLocationDone = false;
	private boolean isFBSessionDone = false;

	private void initView() {
		GlobalAction globalAction = (GlobalAction) this.getApplicationContext();
		globalAction.setActionBar(getActionBar());
	}
	
	private void initFBSessionAndLocation(){
		initIsFBSessionDone();
		initIsLocationDone();

		FacebookUtil.ensureThenOpenActiveSession(this, new FacebookOpenSessionDoneDelegate() {
			@Override
			public void postExec(Session session, SessionState state, Exception exception) {
				// TODO Auto-generated method stub
				Log.w(this.getClass().getSimpleName(), "facebook session postExec");
				isFBSessionDone = true;
			}
		});

		LocationUtil.locateCurrentLocation(this, new LocateLocationDoneDelegate() {
			@Override
			public void postExec(Location location) {
				// TODO Auto-generated method stub
				Log.w(this.getClass().getSimpleName(), "location session postExec");
				isLocationDone = true;
				lastKnownLocation = location;
			}
		});
	}

	// /////////////////////////////////////////
	// LifeCycle
	// /////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_place_activity);
		initView();

		FBlifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				onSessionStateChanged(session, state, exception);
			}
		});
		FBlifecycleHelper.onCreate(savedInstanceState);
		initFBSessionAndLocation();
		startPickPlaceActivityForResult(lastKnownLocation);
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		initFBSessionAndLocation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		FBlifecycleHelper.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		FBlifecycleHelper.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		FBlifecycleHelper.onResume();
		//AppEventsLogger.activateApp(this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		FBlifecycleHelper.onActivityResult(requestCode, resultCode, data);
	
		Log.w(TAG, "onActivityResult");
		
		switch (requestCode) {
		case ResultCode.PickPlaceActivity:
			if(data!=null){
				startPickFriendsActivityForResult();
			}else{
				startMainActivity();
			}
			break;
		case ResultCode.PickFriendsActivity:
			startPickPlaceActivityForResult(lastKnownLocation);
			break;	
		default:
			startMainActivity();
			break;
		}
	}

	// /////////////////////////////////////////
	// Method
	// /////////////////////////////////////////

	private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
		Log.w(TAG, "onSessionStateChanged");
		//initFBSessionAndLocation();
		startPickPlaceActivityForResult(lastKnownLocation);
	}

	private void startPickPlaceActivityForResult(Location location) {

		Log.w(TAG, "OnstartPickPlaceActivity");
		if (isFBSessionDone && isLocationDone) {
			Intent intent = new Intent(this, PickPlaceActivity.class);
			PickPlaceActivity.populateParameters(intent, location, null);
			startActivityForResult(intent, ResultCode.PickPlaceActivity);
			// reset
			isFBSessionDone = false;
			isLocationDone = false;
			lastKnownLocation = null;
		}

	}

	private void initIsFBSessionDone() {
		if (FacebookUtil.isActiveSessionOpen()) {
			isFBSessionDone = true;
		} else {
			isFBSessionDone = false;
		}
	}

	private void initIsLocationDone() {
		if (lastKnownLocation != null) {
			isLocationDone = true;
		} else {
			isLocationDone = false;
		}
	}

	private void startPickFriendsActivityForResult() {
		Intent intent = new Intent(this, PickFriendsActivity.class);
		startActivityForResult(intent, ResultCode.PickFriendsActivity);
	}
	
	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}
