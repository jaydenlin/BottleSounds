package com.wearapp;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.wearapp.resultcode.ResultCode;
import com.wearapp.util.LocationUtil;

public class PickPlaceActivity extends FragmentActivity {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////
	public static final String TAG = PickPlaceActivity.class.getSimpleName();

	// /////////////////////////////////////////
	// Initialize
	// /////////////////////////////////////////
	private GoogleMap map;
	private static Location userLocation;
	PlacePickerFragment placePickerFragment;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.w(TAG, "IN createing OptionsMenu");
	    // Inflate the options menu from XML
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    
	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName("com.wearapp","com.wearapp.HistoryActivity")));
	    //searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	     
	    return true;
	}

	private void setPickPlaceFragment() {
		FragmentManager fm = getSupportFragmentManager();
		placePickerFragment = (PlacePickerFragment) fm.findFragmentById(R.id.place_picker_fragment);

		placePickerFragment.setSettingsFromBundle(getIntent().getExtras());

		placePickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
			@Override
			public void onError(PickerFragment<?> fragment, FacebookException error) {
				PickPlaceActivity.this.onError(error);
			}
		});

		placePickerFragment.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
			@Override
			public void onSelectionChanged(PickerFragment<?> fragment) {
				if (placePickerFragment.getSelection() != null) {
					LocationUtil.selectedlocation = placePickerFragment.getSelection();
					finishActivity();
				}
			}
		});
		placePickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked(PickerFragment<?> fragment) {
				finishActivity();
			}
		});
	}

	@SuppressLint("NewApi")
	private void setGoogleMap() {
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 16));
		UiSettings uisettings = map.getUiSettings();
		uisettings.setZoomControlsEnabled(false);
	}

	public static void populateParameters(Intent intent, Location location, String searchText) {
		intent.putExtra(PlacePickerFragment.LOCATION_BUNDLE_KEY, location);
		intent.putExtra(PlacePickerFragment.SEARCH_TEXT_BUNDLE_KEY, searchText);
		userLocation = location;
	}

	// /////////////////////////////////////////
	// LifeCycle
	// /////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_place_activity);
		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (savedInstanceState == null) {
			setPickPlaceFragment();
		}

		GlobalAction globalAction = (GlobalAction) this.getApplicationContext();
		globalAction.setActionBar(getActionBar());
		setGoogleMap();

	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.w(TAG, "onStart");
		try {
			// Load data, unless a query has already taken place.
			placePickerFragment.loadData(false);
			Log.w(TAG, "placePickerFragment onLoadData");
		} catch (Exception ex) {
			onError(ex);
		}
	}

	// /////////////////////////////////////////
	// Method
	// /////////////////////////////////////////

	private void finishActivity() {
		Intent intent = new Intent();
		intent.putExtra("Place", placePickerFragment.getSelection().getName());
		setResult(ResultCode.PickPlaceActivity, intent);
		finish();
		
	}

	private void onError(Exception error) {
		
		Toast toast = Toast.makeText(this, "Error", Toast.LENGTH_SHORT);
		toast.show();
	}

}
