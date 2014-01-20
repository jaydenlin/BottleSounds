package com.wearapp;



import android.app.ActionBar;
import android.app.Application;



public class GlobalAction extends Application{

	public void setActionBar(ActionBar actionBar){
		 
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_title));
		actionBar.setDisplayShowHomeEnabled(false);
		
	}
	

}
