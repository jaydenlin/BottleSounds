package com.wearapp.parseAPI;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;

import com.facebook.model.GraphPlace;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;

public class ParseAPI {
	
	private final static String applicationId = "Uez6r3nTiUAZ6EY5MHpCS39ePDPOxxgSatDEfPx1";
	private final static String clientKey = "19SwAHhTNkF9ufEnEco9xmy7U7xw4a0GAcCAvNsR";
	
	public static void start(Activity activity,String FBAccessToken) {
		Parse.initialize(activity, applicationId , clientKey);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("access_token", FBAccessToken);
		ParseCloud.callFunctionInBackground("hello", params, new FunctionCallback<String>() {

			@Override
			public void done(String arg0, ParseException arg1) {
				// TODO Auto-generated method stub
				

			}
		});
	}
	
	public static void checkYourVoice(Activity activity,String FBAccessToken,GraphPlace selectedLocation, String message, List<String> toFriends) {
		Parse.initialize(activity, applicationId , clientKey);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("access_token", FBAccessToken);
		params.put("latitude", selectedLocation.getLocation().getLatitude());
		params.put("longitude", selectedLocation.getLocation().getLongitude());
		params.put("message", message);
		params.put("placeName", selectedLocation.getName());
		params.put("toFriends", toFriends);
		
		ParseCloud.callFunctionInBackground("checkYourVoice", params, new FunctionCallback<String>() {

			@Override
			public void done(String arg0, ParseException arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
