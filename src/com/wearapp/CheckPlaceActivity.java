package com.wearapp;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.wearapp.asyncTask.FacebookChatAsyncTask;
import com.wearapp.util.LocationUtil;

import de.measite.smack.Sasl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CheckPlaceActivity extends FragmentActivity {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////
	public static final String TAG = CheckPlaceActivity.class.getSimpleName();

	// /////////////////////////////////////////
	// UI
	// /////////////////////////////////////////
	private Button checkButton;

	// /////////////////////////////////////////
	// Initialize
	// /////////////////////////////////////////
	private static final int PLACE_ACTIVITY = 1;
	private LocationManager locationManager;
	private UiLifecycleHelper FBlifecycleHelper;
	private Location pickPlaceForLocationWhenSessionOpened = null;

	private void initView() {
		checkButton = (Button) findViewById(R.id.check_place);
		GlobalAction globalAction = (GlobalAction)this.getApplicationContext();
		globalAction.setActionBar(getActionBar());
	}

	private void setListener() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		checkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startPickPlaceActivity(LocationUtil
						.getLocation(locationManager));
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

		FBlifecycleHelper = new UiLifecycleHelper(this,
				new Session.StatusCallback() {
					@Override
					public void call(Session session, SessionState state,
							Exception exception) {
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
		
//		Session session = Session.getActiveSession();
//	    if (session.isOpened()) {
//	    	session.requestNewReadPermissions(new NewPermissionsRequest(this, Arrays.asList("xmpp_login")));
//	    	Toast.makeText(getApplicationContext(), session.getAccessToken(), Toast.LENGTH_LONG).show();
//	 	    String targetFacebookId = "1746264605";
//	 	    String title = "MESSAGE";
//	        String message = "test";
//	        new FacebookChatAsyncTask().execute(targetFacebookId,title,message);
//	        
//	    }
		///
	    
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
		Log.w(TAG, "onActivityResult");
		switch (requestCode) {
		case PLACE_ACTIVITY:
			sendToFriend();
			Log.w(TAG,"sendFriend");
			break;
		default:
			break;
		}
	}

	// /////////////////////////////////////////
	// Method
	// /////////////////////////////////////////

	private void onSessionStateChanged(Session session, SessionState state,
			Exception exception) {

		Log.w(TAG, "onSessionStateChanged");
		// 每次FB
		// Session變動時，都確認[Location抓到值]且[FBSession開啟]都完成,然後重新初始化FB選地點的Fragment(開啟PickPlaceActivity)
		if (pickPlaceForLocationWhenSessionOpened != null && state.isOpened()) {
			Location location = pickPlaceForLocationWhenSessionOpened;
			pickPlaceForLocationWhenSessionOpened = null;
			startPickPlaceActivity(location);
		}
		
	}

	private void startPickPlaceActivity(Location location) {

		Log.w(TAG, "OnstartPickPlaceActivity");
		if (ensureOpenFBSession()) {// 確認FBsession也完成
			Intent intent = new Intent(this, PickPlaceActivity.class);
			PickPlaceActivity.populateParameters(intent, location, null);
			startActivityForResult(intent, PLACE_ACTIVITY);
		} else {// 僅有Locattion完成
			pickPlaceForLocationWhenSessionOpened = location;// location done
		}
	}

	private boolean ensureOpenFBSession() {
		if (Session.getActiveSession() == null
				|| !Session.getActiveSession().isOpened()) {
			Session.openActiveSession(this, true, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					onSessionStateChanged(session, state, exception);
				}
			});
			return false;
		}
		return true;
	}

	private void displaySelectedPlace(int resultCode) {

	}

//	 private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
//	 List<String> friends = new ArrayList<String>();
//	 friends.add("4723146631600");
//	 String selectedPlaceID = LocationUtil.selectedlocation.getId();
//	 Log.w(TAG,selectedPlaceID);
//	 return new FacebookDialog.ShareDialogBuilder(this)
//	 .setName("Just a test")
//	 .setDescription("test")
//	 .setLink("http://developers.facebook.com/android")
//	 .setPlace("159310107428142");
//	 }

	private void sendToFriend() {
		
		Intent intent=new Intent(this,PickFriendsActivity.class);
		startActivity(intent);
		
		/* Using FacebookDialog */
//		 if(FacebookDialog.canPresentShareDialog(this,
//		 FacebookDialog.ShareDialogFeature.SHARE_DIALOG)){
//		 FacebookDialog shareDialog = createShareDialogBuilder().build();
//		 FBlifecycleHelper.trackPendingDialogCall(shareDialog.present());
//		 }
	           

//		/* Using WebDialog */
//		 Bundle params = new Bundle();
//		 params.putString("app_id", Integer.toString(R.string.fb_app_id));
//		 params.putString("title", "發給你們做測試");
//		 params.putString("message", "發給你們做測試");
//
////
//		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
//				CheckPlaceActivity.this, Session.getActiveSession(),params))
//				.setOnCompleteListener( new WebDialog.OnCompleteListener() {
//
//					@Override
//					public void onComplete(Bundle values,
//							FacebookException error) {
//						// TODO Auto-generated method stub
//						if (error != null) {
//							if (error instanceof FacebookOperationCanceledException) {
//								Log.w(TAG, "Request cancelled");
//							} else {
//								Log.w(TAG, "Network Error");
//							}
//						} else {
//							final String requestId = values
//									.getString("request");
//							if (requestId != null) {
//								Log.w(TAG, "Request sent");
//							} else {
//								Log.w(TAG, "Request cancel");
//							}
//						}
//					}
//				})
//				.build();
//		requestsDialog.show();
		
		

	}
	

}
