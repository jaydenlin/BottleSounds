package com.wearapp;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.wearapp.util.FacebookOpenSessionDoneDelegate;
import com.wearapp.util.FacebookUtil;
import com.wearapp.util.LocateLocationDoneDelegate;
import com.wearapp.util.LocationUtil;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CheckPlaceActivity extends FragmentActivity {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////
	public static final String TAG = CheckPlaceActivity.class.getSimpleName();

	// /////////////////////////////////////////
	// UI
	// /////////////////////////////////////////

	// /////////////////////////////////////////
	// Initialize
	// /////////////////////////////////////////
	private static final int PLACE_ACTIVITY = 1;
	private UiLifecycleHelper FBlifecycleHelper;
	private Location lastKnoLocation;
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
				lastKnoLocation = location;
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
		startPickPlaceActivity(lastKnoLocation);
		
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
		case PLACE_ACTIVITY:
			sendToFriend();
			break;
		default:
			break;
		}
	}

	// /////////////////////////////////////////
	// Method
	// /////////////////////////////////////////

	private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
		Log.w(TAG, "onSessionStateChanged");
		//initFBSessionAndLocation();
		startPickPlaceActivity(lastKnoLocation);
	}

	private void startPickPlaceActivity(Location location) {

		Log.w(TAG, "OnstartPickPlaceActivity");
		if (isFBSessionDone && isLocationDone) {
			Intent intent = new Intent(this, PickPlaceActivity.class);
			PickPlaceActivity.populateParameters(intent, location, null);
			startActivityForResult(intent, PLACE_ACTIVITY);
			// reset
			isFBSessionDone = false;
			isLocationDone = false;
			lastKnoLocation = null;
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
		if (lastKnoLocation != null) {
			isLocationDone = true;
		} else {
			isLocationDone = false;
		}
	}

	private void sendToFriend() {
		Intent intent = new Intent(this, PickFriendsActivity.class);
		startActivity(intent);
	}

}
