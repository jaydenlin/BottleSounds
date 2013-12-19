package com.wearapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.support.v4.app.NavUtils;

public class HistoryActivity extends ListActivity {
	
	static final String[] records=new String[]{
		"20","21","22","23","24","25"
		
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_history);
		// Show the Up button in the action bar.
		setupActionBar();
		setAdapter();
		
	}

	private void setAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1 , records);
		
		setListAdapter(adapter);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}


}
