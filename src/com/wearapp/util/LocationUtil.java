package com.wearapp.util;

import com.wearapp.ListenActivity;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationUtil{
	
	
	///////////////////////////////////////////
	// debug
	///////////////////////////////////////////	
	public static final String TAG = LocationUtil.class.getSimpleName();
	
	
	public static Location getLocation(LocationManager locationManager,LocationListener locationListener) {
	    
	    Criteria criteria = new Criteria();
	    String bestProvider = locationManager.getBestProvider(criteria, false);
	    Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
	    locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
	    lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
	    try {
	    	if (lastKnownLocation != null) {

	            Double old_latitude=lastKnownLocation.getLatitude();
	            Double old_longitude=lastKnownLocation.getLongitude();
	            Log.d("old","lat :  "+old_latitude);
	            Log.d("old","long :  "+old_longitude);
	            locationListener.onLocationChanged(lastKnownLocation);
	        }   	
	       
	    } catch (NullPointerException e) {
	    	Log.w(TAG, e);
	    }
		return lastKnownLocation;
	}
}
