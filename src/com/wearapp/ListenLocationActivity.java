package com.wearapp;

import com.facebook.widget.PlacePickerFragment;
import com.wearapp.util.LocationUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class ListenLocationActivity extends Activity{
	
	
	///////////////////////////////////////////
	// debug
	///////////////////////////////////////////	

	public static final String TAG = ListenActivity.class.getSimpleName();
	
	private Location lastKnownLocation;
	private LocationManager locationManager;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
		lastKnownLocation = LocationUtil.getLocation(locationManager, new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.w(TAG, "onStatusChanged");
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				Log.w(TAG, "onStatusChanged");
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				Log.w(TAG, "onProviderDisabled");
			}
			
			@Override
			public void onLocationChanged(Location location) {
				Log.w(TAG, "onLocationChanged");
			}
		});
     	startPickLocationActivity(lastKnownLocation);
	}
	
	private void startPickLocationActivity(Location lastKnownLocation) {
		Intent intent = new Intent(this,PickLocationActivity.class);
		intent.putExtra(PlacePickerFragment.LOCATION_BUNDLE_KEY, lastKnownLocation);
		//intent.putExtra(PlacePickerFragment.SEARCH_TEXT_BUNDLE_KEY, "芝山");
		startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}