package com.wearapp;

import com.wearapp.util.DB;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

public class HistoryActivity extends ListActivity {
	
	private DB mDBHelper;
	private Cursor mCursor;
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the options menu from XML
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    
	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    Log.w("In create search bar", ""+getComponentName());
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	     
	    return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_history);
		
		
		// Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }
		
	    
	    setAdapter();
	}

	@SuppressWarnings("deprecation")
	private void setAdapter() {
		/*Open a database and create one if it doesn't exists.*/
		mDBHelper = new DB(this);
		mDBHelper.open();
		
		mCursor = mDBHelper.getAll();
		startManagingCursor(mCursor);
		
		String[] from_column = new String[]{DB.KEY_RECORD,DB.KEY_CREATED};
		int[] to_layout = new int[]{android.R.id.text1,android.R.id.text2  };
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
											mCursor, from_column, to_layout, 0	);
		setListAdapter(adapter);

	}

	public void doMySearch(String query){}

}
