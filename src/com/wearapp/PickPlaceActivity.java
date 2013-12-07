package com.wearapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
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

	private void setPickPlaceFragment() {
		FragmentManager fm = getSupportFragmentManager();
		placePickerFragment = (PlacePickerFragment) fm
				.findFragmentById(R.id.place_picker_fragment);

		placePickerFragment.setSettingsFromBundle(getIntent().getExtras());

		placePickerFragment
				.setOnErrorListener(new PickerFragment.OnErrorListener() {
					@Override
					public void onError(PickerFragment<?> fragment,
							FacebookException error) {
						PickPlaceActivity.this.onError(error);
					}
				});

		placePickerFragment
				.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
					@Override
					public void onSelectionChanged(PickerFragment<?> fragment) {
						if (placePickerFragment.getSelection() != null) {
							LocationUtil.selectedlocation = placePickerFragment
									.getSelection();
							finishActivity();
						}
					}
				});
		placePickerFragment
				.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
					@Override
					public void onDoneButtonClicked(PickerFragment<?> fragment) {
						finishActivity();
					}
				});
	}

	@SuppressLint("NewApi")
	private void setGoogleMap() {
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				userLocation.getLatitude(), userLocation.getLongitude()), 16));
		
		UiSettings uisettings = map.getUiSettings();
		uisettings.setZoomControlsEnabled(false);
		
	}

	public static void populateParameters(Intent intent, Location location,
			String searchText) {
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

		if (savedInstanceState == null) {
			setPickPlaceFragment();
		}

		GlobalAction globalAction = (GlobalAction)this.getApplicationContext();
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
		setResult(RESULT_OK,null);
		finish();
	}

	private void onError(Exception error) {
		String text = getString(0, error.getMessage());
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.show();
	}

}
