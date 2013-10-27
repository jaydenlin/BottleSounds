package com.wearapp;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ListenActivity extends Activity implements OnClickListener,LocationListener{

	
	///////////////////////////////////////////
	// debug
	///////////////////////////////////////////	
	
	public static final String TAG = ListenActivity.class.getSimpleName();

	public static final boolean D = true;
	public static final boolean D_METHOD = D && true;
	
	public static final boolean D_SHOW_TAG = D && true;
	
	public float startTime;
	private LatLng POS = new LatLng(25.033611, 121.565000);
	
	///////////////////////////////////////////
	// UI
	///////////////////////////////////////////		

	private GoogleMap map;
	private LocationManager lms;
	private boolean getGPSService;
	private String bestProvider = LocationManager.GPS_PROVIDER;
	
	///////////////////////////////////////////
	// handler
	///////////////////////////////////////////			

	UIHandler mUIHandler;

	
	
	class UIHandler extends Handler {
		public void handleMessage(Message msg) {

		}
	}
	
	
	 @SuppressLint("NewApi")
	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.listen_activity);

	        //取得系統定位服務
	  		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
	  		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	  			//如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
	  			locationServiceInitial();
	  		} else {
	  			Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
	  			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
	  		}
	        
	        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	  		//map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

	        Location loc1 = new Location("loc1");
	        loc1.setLatitude(POS.latitude);
	        loc1.setLongitude(POS.longitude);
	        
	        Location loc2 = new Location("loc2");
	        loc2.setLatitude(POS.latitude+0.001);
	        loc2.setLongitude(POS.longitude+0.001);
	        
	        ArrayList<Location> position_list = new ArrayList<Location>();
	        position_list.add(loc1);
	        position_list.add(loc2);
	        
	        for(int i=0;i<position_list.size();i++)
	        {
	        	MarkerOptions markerTest = new MarkerOptions().position(new LatLng(position_list.get(i).getLatitude(),position_list.get(i).getLongitude())).title("Jayden");
	        	markerTest.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
	        	markerTest.snippet("AA");
	        	markerTest.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation));
	            map.addMarker(markerTest);
	        }
	        
	     
	        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
	        //map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	  		
	        map.setMyLocationEnabled(true);
	            
	        
	        // Move the camera instantly to POS with a zoom of 16.
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(POS, 16));
	    }
	
	
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		if (D_METHOD) {
			Log.w(TAG, "onStart");
		}

	}
	@Override
	protected void onResume() {
		super.onResume();
		if (D_METHOD) {
			Log.w(TAG, "onResume");
		}
		if (getGPSService) {
			   // 服務提供者、更新頻率毫秒、最短距離、地點改變時呼叫物件
			   lms.requestLocationUpdates(bestProvider, 1000, 1, (LocationListener) this);
		}
	}

	@Override
	protected void onPause() {
		if (D_METHOD) {
			Log.w(TAG, "onPause");
		}
		if (getGPSService) {
		   lms.removeUpdates((LocationListener) this); // 離開頁面時停止更新
		   getGPSService = false;
		  }
		super.onPause();
	}
	
	
	@Override
	protected void onStop() {
		if (D_METHOD) {
			Log.w(TAG, "onStop");
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (D_METHOD) {
			Log.w(TAG, "onDestroy");
		}
		super.onDestroy();
	}


	@Override
	public void onLocationChanged(Location location) {
		getLocation(location);
		
	}


	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	private void locationServiceInitial() {
		/* 取得系統定位服務 */
		  LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));  
		
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 
		{
			getGPSService=true;
			lms = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
			Criteria criteria = new Criteria(); // 資訊提供者選取標準
			bestProvider = lms.getBestProvider(criteria, true);
			Location location = lms.getLastKnownLocation(bestProvider);	//使用GPS定位座標
			
			 if(location != null)
			 {
				 getLocation(location);
	         }
			 else
			 {
	             Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
	         }
		} 
		else 
		{
		   Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
		   // 開啟設定畫面
		   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		} 
	}
	
	
	private void getLocation(Location location) {	//將定位資訊顯示在畫面中
		
			//TextView longitude_txt = (TextView) findViewById(R.id.longitude);
			//TextView latitude_txt = (TextView) findViewById(R.id.latitude);
 
			Double latitude = location.getLatitude();	//取得緯度
			Double longitude = location.getLongitude();	//取得經度	
 
			System.out.println("longitude==============================="+longitude);
			System.out.println("latitude==============================="+latitude);
			
			//longitude_txt.setText(String.valueOf(longitude));
			//latitude_txt.setText(String.valueOf(latitude));
			
			POS = new LatLng(latitude, longitude);
	}
}
