package com.wearapp;


import android.app.ActionBar;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class GlobalAction extends Application{

	
	public void setActionBar(ActionBar actionBar){
		 
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_title));
		actionBar.setDisplayShowHomeEnabled(false);
		
	}
}
