package com.wearapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import com.parse.ParseObject;
import com.pheelicks.visualizer.HistoryView;
import com.wearapp.exception.FacebookUtil.FacebookSessionNotActive;
import com.wearapp.parseAPI.ParseAPI;
import com.wearapp.parseAPI.ParseGetDataDoneCallback;
import com.wearapp.util.DB;
import com.wearapp.util.FacebookUtil;
import com.wearapp.util.JSONParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

public class HistoryActivity extends Activity {
	
	private DB mDBHelper;
	private Cursor mCursor;
	HistoryView background;
	
	DecimalFormat df2 = new DecimalFormat("00");
	

	
	@Override
	protected void onStop(){
		super.onStop();
		
		
	}
	
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
		/*Make the screen horizon*/
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView( background = new HistoryView(this, HistoryActivity.this));
		// Get the intent, verify the action and get the query
		
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }
		
	    
	    //setAdapter();
 	    
	    getItemListFromWeb();
	    
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
 
    ArrayList<UserData> userDataList;
    HashMap<Integer, UserData> userMap;
 
    
    
    
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_OWNER = "owner";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PLACENAME = "placeName";
    private static final String TAG_TO_FRIENDS = "toFriends";
 
    // products JSONArray
    JSONArray products = null;
    private void getItemListFromWeb(){
        // Hashmap for ListView
    	userDataList = new ArrayList<UserData>();
         userMap = new HashMap<Integer, UserData>();
 
        // Loading userIdList in Background Thread
        new LoadAllMessage().execute();
    	
    }

 
    
    
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    public class LoadAllMessage extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HistoryActivity.this);
            pDialog.setMessage("Loading message. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        
        /**
         * getting All messages from parse
         * */
        protected String doInBackground(String... args) {
        	
        	try {
        	    ParseAPI.readVoiceListForYou(HistoryActivity.this,FacebookUtil.getAccessToken(), new ParseGetDataDoneCallback() {       

        	    @Override
        	    public void afterGetListDone(ArrayList<ParseObject> parseObjectList) {
        	           // 在這裡讀取物件
        	    	ArrayList<String> toWhomList = (ArrayList<String>)  parseObjectList.get(0).get(TAG_TO_FRIENDS);
        	    	UserData userdata;
        	    	String userId = toWhomList.get(0);
        	    	userdata = new UserData(Integer.parseInt(userId),null,null);
        	    	userdata.setisOwner(true);
        	    	userDataList.add(userdata);
        	    	userMap.put(userdata.getUID(), userdata);
        	    	new DownloadPictureTask().execute(userdata);
        	    	for(ParseObject parseObject : parseObjectList){
        	    		userdata = new UserData((Integer)parseObject.get(TAG_OWNER),(String)parseObject.getString(TAG_PLACENAME), null);
        	    		userdata.setLatitude((Double)parseObject.get(TAG_LATITUDE));
        	    		userdata.setLongitude((Double)parseObject.get(TAG_LONGITUDE));
        	    		userdata.setMessage((String)parseObject.get(TAG_MESSAGE));
        	    		userDataList.add( userdata);
        	    		userMap.put(userdata.getUID(), userdata);
        	    		new DownloadPictureTask().execute(userdata);
        	    		
        	    		Log.w("InBackGround",(Integer)parseObject.get(TAG_OWNER)+" "+(String) parseObject.getString(TAG_PLACENAME) );
        	    	}
        	    	Log.w("HistoryActivity", "userdatalist size = "+userDataList.size());
        	    	while(true){
        	    		int cnt = 0;
        	    		for(UserData user : userDataList){
        	    			if(user.getUserPic() == null){
        	    				cnt++;
        	    			}
        	    			
        	    		}
        	    		if(cnt == 0){
        	    			background.setmUserIdMap( userMap);
        	    			break;
        	    		}
        	    	}//while()
        	    	
        	    }//afterGetListDone()

        	        @Override
        	    public void afterGetObjectDone(ParseObject parseObject) {
        	      //目前這裡還用不到            
        	    }
        	   });
        	} catch (FacebookSessionNotActive e) {
        	    e.printStackTrace();
        	}
 
            return "";
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
                     * TODO 
                     * */
                }
            });
 
        }//onPostExecute()
        
        
        

        
 
    }//LoadAllMessages()
    
    private class DownloadPictureTask extends AsyncTask<UserData, Integer, Integer> {
        
    	
    	protected Integer doInBackground(UserData... urls) {
            int count = urls.length;
            for (int i = 0; i < count; i++) {
               
            	Bitmap bitmap = getFacebookProfilePic(urls[i].getUID());
            	 userMap.get(urls[i].getUID()).setUserPic(bitmap);
            	
                if (isCancelled()) break;
            }
            return  userMap.size();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Integer result) {
        	Log.w("Finished Dowload Picture Task",  userMap.size()+"");

        }
    
        private Bitmap getFacebookProfilePic(Integer userid){
        	Log.w("HistoryActivity", "getFacebookProfilePic "+ userid);

        	 URL img_value = null;
        	 Bitmap mIcon = null;
        	 try {
    			img_value = new URL("http://graph.facebook.com/"+String.valueOf(userid)+"/picture?type=large");
    			mIcon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
    			
    			
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	 
        	return mIcon;
        }
        
        
        
    
    }
   

	
    public class UserData  {
    	private boolean isOwner = false;
    	private Integer UID;
		private String placeName;
		private double longitude;
		private double latitude;
		private String message;
		
		private Bitmap userPic;
		
    	public UserData(Integer uid , String placename, Bitmap bitmap){
    		UID = uid;
    		placeName = placename;
    		userPic = bitmap;
    	}
    	
    	public boolean isOwner(){
    		return isOwner;
    	}
    	
    	public void setisOwner(boolean isowner){
    		isOwner = isowner;
    		
    	}
    	
    	public void setLongitude(double l ){
    		longitude = l;
    		
    	}
    	
    	public double getLongitude(){
    		return longitude ;
    		
    	}
    	
    	public void setLatitude(double l ){
    		latitude = l;
    		
    	}
    	
    	public double getLatitude(){
    		return latitude ;
    		
    	}
    	
    	public void setMessage(String m ){
    		message = m;
    		
    	}
    	
    	public String getMessage(){
    		return message ;
    		
    	}
    	
    	public Integer getUID() {
			return UID;
		}

		public void setUID(Integer uID) {
			UID = uID;
		}

		public String getPlaceName() {
			return placeName;
		}

		public void setPlaceName(String placeName) {
			this.placeName = placeName;
		}

		public Bitmap getUserPic() {
			return userPic;
		}

		public void setUserPic(Bitmap userPic) {
			this.userPic = userPic;
		}

    }
    
    
    
}
