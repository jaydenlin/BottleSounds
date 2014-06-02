package com.wearapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Cap;
import android.graphics.PorterDuff.Mode;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

public class HistoryActivity extends Activity {
	
	private DB mDBHelper;
	private Cursor mCursor;
	HistoryView background;
    private ProgressDialog progressDialog;
	Paint iconPaint;
	AsyncTask<UserData, Integer, Integer> getPicture;

	DecimalFormat df2 = new DecimalFormat("00");
	
	public void doMySearch(String query){}
	
	@Override
	protected void onStop(){
		super.onStop();	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the options menu from XML
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
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
	    progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading message. Please wait...");
		progressDialog.setIndeterminate(false);

		iconPaint = new Paint();
		iconPaint.setStrokeWidth(4f);
		iconPaint.setColor(Color.WHITE);
		//iconPaint.setAlpha(200);
		iconPaint.setStyle(Paint.Style.STROKE);
		iconPaint.setStrokeCap(Cap.ROUND);
		iconPaint.setAntiAlias(true);
		
	    getItemListFromWeb();
	    Log.w("HistoryActivity", "After get getItemListFromWeb");
	    
	}
	
	//////////////////////////////////////////////
	//											//
	//		QUERY DATA FROM Parse				//
	//											//
	//////////////////////////////////////////////
	
  // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<UserData> userList;
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_OWNER = "owner";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PLACENAME = "placeName";
    private static final String TAG_TO_FRIENDS = "toFriends";
    private static final String TAG_PIC = "ownerPic";
    
 
    // products JSONArray
    JSONArray products = null;
    private void getItemListFromWeb(){
         userList = background.getUserList();
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
        private ArrayList<Bitmap> bitMapList;
        int bitMapCount = 1;
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
    		progressDialog.show();
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
        	    	userdata = new UserData(Long.parseLong(userId),null,null);
        	    	userdata.setisOwner(true);
        	    	userList.add( userdata);
        	    	getPicture = new DownloadPictureTask();
        	    	getPicture.execute(userdata);
        	    	for(ParseObject parseObject : parseObjectList){
        	    		Log.w("ParseObject", "UserData((Long)parseObject.get(TAG_OWNER) = "+ (String)parseObject.get(TAG_OWNER));
        	    		userdata = new UserData(Long.parseLong((String)parseObject.get(TAG_OWNER)),(String)parseObject.getString(TAG_PLACENAME), null);
        	    		userdata.setLatitude((Double)parseObject.get(TAG_LATITUDE));
        	    		userdata.setLongitude((Double)parseObject.get(TAG_LONGITUDE));
        	    		userdata.setMessage((String)parseObject.get(TAG_MESSAGE));
        	    		ParseFile imageFile = (ParseFile)parseObject.get(TAG_PIC);
        	    		if(imageFile == null) {
        	    			continue;
        	    			
        	    		}
        	    		imageFile.getDataInBackground(new GetDataCallback() {
        	    		  public void done(byte[] data, ParseException e) {
        	    		    if (e == null) {
        	    		    	Log.w("LoadAllMessage getDataInBackground", "get image successfully");
                	    		Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
                	    		userList.get(bitMapCount++).setUserPic(bitmap);
        	    		    } else {
        	    		    	Log.e("LoadAllMessage afterGetListDone", "Cannot get parseFile image ");
        	    		    }
        	    		  }
        	    		});

        	    		userList.add( userdata);

        	    		Log.w("HistoryActivity","In LoadAllMessage doInBackGround" +(String)parseObject.get(TAG_OWNER)+" "+(String) parseObject.getString(TAG_PLACENAME) );
        	    	}
        	    	
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
            // updating UI from Background Thread
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    /**
//                     * Updating parsed JSON data into ListView
//                     * TODO 
//                     * */
//                }
//            });
        	
        	progressDialog.dismiss();
 
        }//onPostExecute()
   }//LoadAllMessages()
    
    
       

	
    public class UserData implements Runnable {
    	private Long ownerUID;
    	private boolean isOwner = false;
    	private Long UID;
		private String placeName;
		private double longitude;
		private double latitude;
		private String message;
		
		private String link;
		

		private Bitmap unit_bmp;
		Rect unit_rect = new Rect(); 
	    private float circleX ;
	    private float circleY ;
	    private int index;
		int unit_Width ;
		int unit_Height;
		
		
		
    	public UserData(Long uid , String placename, Bitmap bitmap){
    		UID = uid;
    		placeName = placename;
    		unit_bmp = bitmap;
    		link= "";

    	}
    	
    	public boolean isOwner(){
//    		if(ownerUID == UID){
//    			isOwner = true;
//    		}
    		return isOwner;
    	}
    	
    	public void setOwenrUID(long uid ){
    		ownerUID = uid;
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
    	
    	public Long getUID() {
			return UID;
		}

		public void setUID(Long uID) {
			UID = uID;
		}

		public String getPlaceName() {
			return placeName;
		}

		public void setPlaceName(String placeName) {
			this.placeName = placeName;
		}

		public Bitmap getUserPic() {
			return unit_bmp;
		}

		public void setUserPic(Bitmap userPic) {
			this.unit_bmp = userPic;
		}

		
		public void setUserLink(String link){
			this.link = link;
		}
		
		public String getUserLink(){
			return this.link;
		}
    
 
		public void setX(float _x) {
			circleX = _x;
		}

		public void setY(float _y) {
			circleY = _y;
		}
		
		public void setindex(int i){
			
			index =i;
		}
		public int getIndex(){
			return index; 
		}
	    
		public void setsCircleIcon(float _x, float _y ) {
			
			unit_Width = unit_bmp.getWidth();
			unit_Height = unit_bmp.getHeight();
			circleX= _x;
			circleY = _y;
			// CircleIcon 類別實作了 Runnable 介面
			// 仍需以 Thread 類別來建立執行緒，如下 :
			new Thread(this).start();

			// 將 AndroidUnit 類別本身(this)當做參數並實體化為執行緒
			// 啟動 .start() 方法會自動執行 run() 函式內的程式內容
			// 當 AndroidUnit 此類別 被實體化時，便會同時啟動此類別的執行緒
			// 例如 : AndroidUnit au = new AndroidUnit(bmp);

		}

		@Override 
		public void run() {}

		// ==== 將圖 PO 到 canvas(畫布)上 ====
		public void PostUnit(Canvas canvas) {
			// 在 canvas 上繪出物件本體
			float X =    circleX- unit_Width/2;
			float Y = circleY - unit_Height/2;
			float raidus = getRadius(unit_Width/2,unit_Height/2);
			Bitmap newBitmap = getCroppedBitmap(unit_bmp);
			canvas.drawBitmap(newBitmap,X, Y , null);
			
			canvas.drawCircle(circleX, circleY, raidus, iconPaint);
			unit_rect.set((int)X ,(int)Y , (int)X + unit_Width,(int)Y+ unit_Width) ;
			
		}
		
		public float getRadius(float width, float height){
			return (width > height)? height:width;		
		}
		
		 public Bitmap getCroppedBitmap(Bitmap bitmap) {
		    	
		    	
		        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
		                bitmap.getHeight(), Config.ARGB_8888);
		        Canvas canvas = new Canvas(output);

		        final int color = 0xff424242;
		        final Paint paint = new Paint();
		        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		        paint.setAntiAlias(true);
		        canvas.drawARGB(0, 0, 0, 0);
		        paint.setColor(color);
		        int diameter  = (bitmap.getWidth() > bitmap.getHeight())? bitmap.getHeight() : bitmap.getWidth();
		        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
		        		diameter / 2, paint);
		        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		        canvas.drawBitmap(bitmap, rect, rect, paint);

		        return output;
		    }
		
		//==== 檢查是否被碰觸到 ====
	    public void IsTouch(int touch_x, int touch_y) {

	        //將觸碰點的座標 touch_x 與 touch_y 傳入到
	        //矩形框類別變數 unit_rect 的 contains(x, y) 方法中去判別
	        //如果觸碰點的座標位於矩形框範圍內則contains(x, y)方法會傳回 true
	        //否則傳回 false
        	 if(isOwner()){		 
        		 return;
        	 }

	        if (unit_rect.contains(touch_x, touch_y)) {
	        		Log.w("CirecleIcon", "Is touched"+touch_x+" "+touch_y+" "+unit_rect.bottom+" "+unit_rect.left+" "+unit_rect.right+" "+unit_rect.top);
	  	        	showCustomizedDialog();

	        }
	    }
	    
	    private void showCustomizedDialog(){
        	final Dialog dialog = new Dialog(HistoryActivity.this, android.R.style.Theme_Translucent);
        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        	dialog.setCancelable(true);
        	dialog.setContentView(R.layout.dialog);
        	Button btnComplete = (Button) dialog.findViewById(R.id.button_complete);
        	TextView text_placename = (TextView) dialog.findViewById(R.id.text_place_name);
        	text_placename.setText(getPlaceName());
        	//TextView text_name = (TextView) dialog.findViewById(R.id.text_name);
        	ImageView image_icon = (ImageView) dialog.findViewById(R.id.image_icon);
        	image_icon.setImageBitmap(getUserPic());
        	btnComplete.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
        		
        	});
        	dialog.show();
	    }


    }//UserData
    

    public class DownloadPictureTask extends AsyncTask<UserData, Integer, Integer> {

		@Override
		protected Integer doInBackground(UserData... params) {
			int count = params.length;
			Log.w("HistoryActivity", "in DownloadPictureTask count = "+count);

			for (int i = 0; i < count; i++) {
               
            	Bitmap bitmap = getFacebookProfilePic(params[i], params[i].getUID());
            	params[i].setUserPic(bitmap);
            	
                if (isCancelled()) break;
            }
            return  userList.size();
		}
		
		
	  
        protected void onProgressUpdate(Integer... progress) {
        }

        public void onPostExecute(Integer result) {
        	Log.w("Finished Dowload Picture Task",  userList.size()+"");

        }
    
        private Bitmap getFacebookProfilePic(UserData userdata, Long long1){
        	Log.w("HistoryActivity", "start getFacebookProfilePic "+ long1);

        	 URL img_value = null;
        	 Bitmap mIcon = null;
        	 try {
        		 
    			img_value = new URL("https://graph.facebook.com/"+String.valueOf(long1)+"/picture?type=large");
        		 //img_value = new URL(userdata.getUserLink());
        		 Log.w("HistoryActivity", img_value.toString());
    			mIcon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
    			if(mIcon == null ) {
    				Log.e("HistoryActivity", "get null picutre");
    			}
    			
    			
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	Log.w("HistoryActivity", " finish getFacebookProfilePic "+ long1 );
        	return mIcon;
        }
     

    }//DownloadPictureTask

    
    
}//HistiryActivity
