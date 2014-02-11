package com.wearapp.parseAPI;

import java.util.HashMap;

import android.app.Activity;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;

public class ParseAPI {

	public static void start(Activity activity,String FBAccessToken) {
		Parse.initialize(activity, "Uez6r3nTiUAZ6EY5MHpCS39ePDPOxxgSatDEfPx1", "19SwAHhTNkF9ufEnEco9xmy7U7xw4a0GAcCAvNsR");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("access_token", FBAccessToken);
		ParseCloud.callFunctionInBackground("hello", params, new FunctionCallback<String>() {

			@Override
			public void done(String arg0, ParseException arg1) {
				// TODO Auto-generated method stub
				

			}
		});
	}

}
