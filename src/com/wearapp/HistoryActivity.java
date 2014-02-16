package com.wearapp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pheelicks.visualizer.HistoryView;
import com.wearapp.util.DB;
import com.wearapp.util.JSONParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SurfaceHolder;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class HistoryActivity extends Activity {
	
	private DB mDBHelper;
	private Cursor mCursor;
	HistoryView background;
	
	DecimalFormat df2 = new DecimalFormat("00");
	
	

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

		setContentView( background = new HistoryView(this));
		//background.setUserIdList(list);
		background.InitialSet();
		// Get the intent, verify the action and get the query
		
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }
		
	    
	    //setAdapter();
 	    
	    getItemListFromWeb();
	    
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

	}
	

	public void doMySearch(String query){}
	
	
	//////////////////////////////////////////////
	//											//
	//QUERY DATA FROM MYSQL						//
	//											//
	//////////////////////////////////////////////
	
	// Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> productsList;
 
    // url to get all products list
    private static String url_all_products = "http://54.251.252.71/heare/get_all_products.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "voice_id";
    private static final String TAG_NAME = "datetime";
 
    // products JSONArray
    JSONArray products = null;
    private void getItemListFromWeb(){
        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();
 
        // Loading products in Background Thread
        new LoadAllProducts().execute();
 
        /*
        // Get listview
        ListView lv = getListView();
 
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                /*
                 * TODO edit new product
                 
            	String pid = ( (TextView) view.findViewById(R.id.pid) ).getText().toString();
 
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditProductActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_PID, pid);
 
                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
                
                */
        /*
            }
        });
        */
    	
    }
    
    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HistoryActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
 
                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                } else {
                	/*
                	 * TODO id there is no Item, we shuold new an activity.
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            HistoryActivity.this, productsList,
                            R.layout.list_item, new String[] { TAG_PID,
                                    TAG_NAME},
                            new int[] { R.id.pid, R.id.name });
                    // updating listview
                   // setListAdapter(adapter);
                }
            });
 
        }//onPostExecute()
 
    }//LoadAllProducts()

    
    

}
