package com.wearapp;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

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

public class CheckPlaceActivity extends FragmentActivity implements LocationListener{
	
	
	public static final String TAG = CheckPlaceActivity.class.getSimpleName();
	
	private static final int PLACE_ACTIVITY = 1;

    private LocationManager locationManager;
    private Location lastKnownLocation;
    private UiLifecycleHelper lifecycleHelper;
    private Location pickPlaceForLocationWhenSessionOpened = null;
    private ImageButton btnButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello);

        lifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChanged(session, state, exception);
            }
        });
        lifecycleHelper.onCreate(savedInstanceState);

        ensureOpenSession();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btnButton = (ImageButton) findViewById(R.id.btnSpeak);
        
        btnButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
            	
            	onClickGPS();
            }
        });
    }

    private boolean ensureOpenSession() {
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

    @Override
    protected void onStart() {
        super.onStart();
        // Update the display every time we are started (this will be "no place selected" on first
        // run, or possibly details of a place if the activity is being re-created).
        //displaySelectedPlace(RESULT_OK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lifecycleHelper.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lifecycleHelper.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleHelper.onResume();
        Log.w(TAG,"onResume");
        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);
    }

    private void onError(Exception exception) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error").setMessage(exception.getMessage()).setPositiveButton("OK", null);
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lifecycleHelper.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG,"onActivityResult");
        finish();
//        switch (requestCode) {
//            case PLACE_ACTIVITY:
//                displaySelectedPlace(resultCode);
//                break;
//            default:
//                break;
//        }
    }

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

    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        Log.w(TAG,"on location changed");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private void startPickPlaceActivity(Location location) {
    	Log.w(TAG,"OnstartPickPlaceActivity");
        if (ensureOpenSession()) {
            //PlacePickerApplication application = (PlacePickerApplication) getApplication();
            //application.setSelectedPlace(null);

            Intent intent = new Intent(this, PickPlaceActivity.class);
            PickPlaceActivity.populateParameters(intent, location, null);

            startActivityForResult(intent, PLACE_ACTIVITY);
            //startActivity(intent);
        } else {
            pickPlaceForLocationWhenSessionOpened = location;//location done
        }
    }

    private void onClickGPS() {
        try {
            if (lastKnownLocation == null) {
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, false);
                if (bestProvider != null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
                }
            }
            startPickPlaceActivity(lastKnownLocation);
        } catch (Exception ex) {
            onError(ex);
        }
    }

}
