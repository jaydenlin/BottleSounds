package com.wearapp;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.wearapp.util.LocationUtil;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CheckPlaceActivity extends FragmentActivity{
	
	///////////////////////////////////////////
	// debug
	///////////////////////////////////////////	
	public static final String TAG = CheckPlaceActivity.class.getSimpleName();
	
    ///////////////////////////////////////////
    // UI
    ///////////////////////////////////////////	
    private Button checkButton;
    
    ///////////////////////////////////////////
    // Initialize
    ///////////////////////////////////////////	
    private static final int PLACE_ACTIVITY = 1;
    private LocationManager locationManager;
    private UiLifecycleHelper FBlifecycleHelper;
    private Location pickPlaceForLocationWhenSessionOpened = null;
    
    private void initView(){
    	checkButton = (Button) findViewById(R.id.check_place);
    }
    
    private void setListener(){
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	checkButton.setOnClickListener(new View.OnClickListener() {
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
        switch (requestCode) {
            case PLACE_ACTIVITY:
            	sendToFriend();
                break;
            default:
                break;
        }
    }
    
    ///////////////////////////////////////////
    // Method
    ///////////////////////////////////////////	
    
    private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
    	
    	Log.w(TAG,"onSessionStateChanged");
    	//每次FB Session變動時，都確認[Location抓到值]且[FBSession開啟]都完成,然後重新初始化FB選地點的Fragment(開啟PickPlaceActivity)
        if (pickPlaceForLocationWhenSessionOpened != null && state.isOpened()) {
            Location location = pickPlaceForLocationWhenSessionOpened;
            pickPlaceForLocationWhenSessionOpened = null;
            startPickPlaceActivity(location);
        }
    }

    private void startPickPlaceActivity(Location location) {
    	
    	Log.w(TAG,"OnstartPickPlaceActivity");
    	if (ensureOpenFBSession()) {//確認FBsession也完成
            Intent intent = new Intent(this, PickPlaceActivity.class);
            PickPlaceActivity.populateParameters(intent, location, null);
            startActivityForResult(intent, PLACE_ACTIVITY);
        } else {//僅有Locattion完成
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
    
    private void displaySelectedPlace(int resultCode) {

  }
    
//    private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
//    	
//    	String selectedPlaceID = LocationUtil.selectedlocation.getId();  
//    	Log.w(TAG,selectedPlaceID);
//        return new FacebookDialog.ShareDialogBuilder(this)
//                .setName("Just a test")
//                .setDescription("test")
//                .setLink("http://developers.facebook.com/android")
//                .setPlace(selectedPlaceID);
//    }  
    
    private void sendToFriend() {

/*Using FacebookDialog*/    	
//    	if(FacebookDialog.canPresentShareDialog(this,
//      FacebookDialog.ShareDialogFeature.SHARE_DIALOG)){
//		FacebookDialog shareDialog = createShareDialogBuilder().build();
//		FBlifecycleHelper.trackPendingDialogCall(shareDialog.present());
//		}

/*Using WebDialog*/     	
//    	Bundle params = new Bundle();
//    	  params.putString("app_id", Integer.toString(R.string.fb_app_id));
//    	  params.putString("title", "發給你們做測試");
//        params.putString("message", "發給你們做測試");
//        params.putString("place", LocationUtil.selectedlocation.getId());
//        params.putString("name", "An example parameter");
//        params.putString("link", "https://www.example.com/");
        
        WebDialog requestsDialog = (
            new WebDialog.RequestsDialogBuilder(CheckPlaceActivity.this,
                Session.getActiveSession()))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {
					
					@Override
					public void onComplete(Bundle values, FacebookException error) {
						// TODO Auto-generated method stub
						if (error != null) {
	                        if (error instanceof FacebookOperationCanceledException) {
	                               Log.w(TAG, "Request cancelled"); 
	                        } else {
	                        	   Log.w(TAG,"Network Error");
	                        }
	                    } else {
	                        final String requestId = values.getString("request");
	                        if (requestId != null) {
	                        	Log.w(TAG, "Request sent");
	                        } else {
	                        	Log.w(TAG, "Request cancel");
	                        }
	                    }   
					}
				})
                .setMessage("hello world啦 送出去啦拜託")
                .setTitle("發給你們做測試")
                .build();
        requestsDialog.show();

    }


}
