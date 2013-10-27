package com.wearapp;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class PickLocationActivity extends FragmentActivity{
	
	///////////////////////////////////////////
	// debug
	///////////////////////////////////////////		
	public static final String TAG = PickLocationActivity.class.getSimpleName();
	
	private UiLifecycleHelper FBLifecycleHelper;
	private PlacePickerFragment placePickerFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_location_activity);
		FBLifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onFBSessionStateChanged(session, state, exception);
			}
			
		});
		FBLifecycleHelper.onCreate(savedInstanceState);
	}
	
	private void setPlacePickerFragment(){
		FragmentManager fragmentManager = getSupportFragmentManager();
        placePickerFragment = (PlacePickerFragment) fragmentManager.findFragmentById(R.id.place_picker_fragment);
        placePickerFragment.setSettingsFromBundle(getIntent().getExtras());
        try {
            placePickerFragment.loadData(false);
        } catch (Exception ex) {
            Log.w(TAG,ex);
        }
        placePickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
            @Override
            public void onError(PickerFragment<?> fragment, FacebookException error) {
                Log.w(TAG,error);
            }
        });

        placePickerFragment.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(PickerFragment<?> fragment) {
                if (placePickerFragment.getSelection() != null) {
                    //finishActivity();
                }
            }
        });
        placePickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
            @Override
            public void onDoneButtonClicked(PickerFragment<?> fragment) {
                //finishActivity();
            }
        });
	}
	
	 private boolean ensureFBOpenSession() {
	        if (Session.getActiveSession() == null ||
	                !Session.getActiveSession().isOpened()) {
	            Session.openActiveSession(this, true, new Session.StatusCallback() {
	                @Override
	                public void call(Session session, SessionState state, Exception exception) {
	                    onFBSessionStateChanged(session, state, exception);
	                }
	            });
	            return false;
	        }
	        return true;
	    }
	
	 private void onFBSessionStateChanged(Session session, SessionState state, Exception exception) {
		 
		 if(session.isOpened()){
			 setPlacePickerFragment();
		 }
		 Log.w(TAG,exception);
	 }
	 
	@Override
	protected void onStart() {
		super.onStart();
		if(ensureFBOpenSession()&&placePickerFragment==null){
			setPlacePickerFragment();
			Log.w(TAG,"onStart loaddata");
		}
		Log.w(TAG,"onStart");
       
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		FBLifecycleHelper.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		FBLifecycleHelper.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		FBLifecycleHelper.onResume();
		Log.w(TAG, "onResume");
	}


	@Override
	protected void onStop() {
		super.onStop();
		FBLifecycleHelper.onStop();
	}
	
}

