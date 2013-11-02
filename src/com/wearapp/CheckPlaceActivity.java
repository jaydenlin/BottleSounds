package com.wearapp;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.wearapp.util.LocationUtil;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class CheckPlaceActivity extends FragmentActivity{
	
	///////////////////////////////////////////
	// debug
	///////////////////////////////////////////	
	public static final String TAG = CheckPlaceActivity.class.getSimpleName();
	
    
    ///////////////////////////////////////////
    // UI
    ///////////////////////////////////////////	
    private ImageButton btnButton;
    
    
    ///////////////////////////////////////////
    // Initialize
    ///////////////////////////////////////////	
    private static final int PLACE_ACTIVITY = 1;
    private LocationManager locationManager;
    private UiLifecycleHelper FBlifecycleHelper;
    private Location pickPlaceForLocationWhenSessionOpened = null;
    
    private void initView(){
    	btnButton = (ImageButton) findViewById(R.id.btnSpeak);
    }
    
    private void setListener(){
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	startPickPlaceActivity(LocationUtil.getLocation(locationManager));
            }
        });
    }
    
    ///////////////////////////////////////////
    // LifeCycle
    ///////////////////////////////////////////	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_place_activity);

        FBlifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChanged(session, state, exception);
            }
        });
        FBlifecycleHelper.onCreate(savedInstanceState);
        ensureOpenFBSession();
        initView();
        setListener();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //displaySelectedPlace(RESULT_OK);
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
        AppEventsLogger.activateApp(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FBlifecycleHelper.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG,"onActivityResult");
//        switch (requestCode) {
//            case PLACE_ACTIVITY:
//                displaySelectedPlace(resultCode);
//                break;
//            default:
//                break;
//        }
    }
    
    ///////////////////////////////////////////
    // Method
    ///////////////////////////////////////////	
    
    private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
        if (pickPlaceForLocationWhenSessionOpened != null && state.isOpened()) {
            Location location = pickPlaceForLocationWhenSessionOpened;
            pickPlaceForLocationWhenSessionOpened = null;
            startPickPlaceActivity(location);
        }else{
        	Log.w(TAG,"Session not open");
        }
    }

    private void displaySelectedPlace(int resultCode) {
//        String results = "";
//        PlacePickerApplication application = (PlacePickerApplication) getApplication();
//
//        GraphPlace selection = application.getSelectedPlace();
//        if (selection != null) {
//            GraphLocation location = selection.getLocation();
//
//            results = String.format("Name: %s\nCategory: %s\nLocation: (%f,%f)\nStreet: %s, %s, %s, %s, %s",
//                    selection.getName(), selection.getCategory(),
//                    location.getLatitude(), location.getLongitude(),
//                    location.getStreet(), location.getCity(), location.getState(), location.getZip(),
//                    location.getCountry());
//        } else {
//            results = "<No place selected>";
//        }
//
//        resultsTextView.setText(results);
    }


    private void startPickPlaceActivity(Location location) {
    	
    	Log.w(TAG,"OnstartPickPlaceActivity");
    	if (ensureOpenFBSession()) {
            Intent intent = new Intent(this, PickPlaceActivity.class);
            PickPlaceActivity.populateParameters(intent, location, null);
            startActivityForResult(intent, PLACE_ACTIVITY);
        } else {
            pickPlaceForLocationWhenSessionOpened = location;//location done
        }
    }
    
    private boolean ensureOpenFBSession() {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(this, true, new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    onSessionStateChanged(session, state, exception);
                }
            });
            return false;
        }
        return true;
    }


}
