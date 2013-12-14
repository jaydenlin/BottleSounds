package com.wearapp.util;

import com.facebook.model.GraphPlace;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationUtil {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////
	public static final String TAG = LocationUtil.class.getSimpleName();

	// /////////////////////////////////////////
	// Member
	// /////////////////////////////////////////
	private static Location lastKnownLocation;
	public static GraphPlace selectedlocation;

	private static LocateLocationDoneDelegate locateLocationDoneDelegateForInteral;

	private static final Location DEFULT_LOCATION = new Location("") {
		{
			setLatitude(25.033611);
			setLongitude(121.565000);
		}
	};

	// /////////////////////////////////////////
	// Method
	// /////////////////////////////////////////

	/**
	 * A simple way to get user's location
	 * 
	 * @param locationManager
	 * @return location
	 */
	public static void locateCurrentLocation(Activity activity,
			LocateLocationDoneDelegate locateLocationDoneDelegate) {

		locateLocationDoneDelegateForInteral = locateLocationDoneDelegate;
		
		LocationManager locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
		locationManager.requestLocationUpdates(bestProvider, 0, 0,
				locationListener);
		lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);

		if (lastKnownLocation != null) {
			Double old_latitude = lastKnownLocation.getLatitude();
			Double old_longitude = lastKnownLocation.getLongitude();
			Log.d("old", "lat :  " + old_latitude);
			Log.d("old", "long :  " + old_longitude);
			locationListener.onLocationChanged(lastKnownLocation);
		} else {
			lastKnownLocation = DEFULT_LOCATION;
			Log.w(TAG, "lastKnownLocation is null");
		}
	}

	private static LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.w(TAG, "on location changed");
			lastKnownLocation = location;
			locateLocationDoneDelegateForInteral.postExec(location);
			
		}
	};

}