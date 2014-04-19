package com.pheelicks.visualizer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wearapp.HistoryActivity;
import com.wearapp.ListenActivity;
import com.wearapp.HistoryActivity.UserData;
import com.wearapp.R;

public class HistoryView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////

	public static final String TAG = HistoryView.class.getSimpleName();

	public static final boolean D = true;
	public static final boolean D_METHOD = D && true;

	public static final boolean D_SHOW_TAG = D && true;
	
	private RectF mRect = new RectF();
	private Resources res;
	private Bitmap userBmp;
	private boolean flag = true;
	private Canvas canvas = null;
	private SurfaceHolder holder;
	private ArrayList<CircleIcon> iconList; // ICON 類別型態的物件陣列
	private Thread db_thread;
	float mCenterX; 
	float mCenterY; 
	float mWidth;
	float mHeight;
	private Context mContext;
	private HistoryActivity mActivity;
	private HashMap<Long,UserData> mUserIdMap ;
	private Builder mAlertDialogBuilder; 
    private ProgressDialog progressDialog;
 
  
	Paint slinePaint;
	Paint textPaint;
	Paint iconPaint;
	

	public HistoryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
	}

	public HistoryView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HistoryView(Context context,HistoryActivity activity) {
		super(context);
		if(D_METHOD){
			Log.w(TAG, "On Create HistoryView");
		}
		mActivity = activity;
		mContext = context;
		res = getResources();
		setWillNotDraw(false);
		getHolder().addCallback(this);
		holder = getHolder();
			
		iconList = new ArrayList<CircleIcon>();
		slinePaint = new Paint();
		textPaint = new Paint();
		iconPaint = new Paint();
		
		InitialSetting();
		
		db_thread = new Thread(this);
		
	}

	public void InitialSetting() {
		if(D_METHOD){
			Log.w(TAG, "In InitialSet");
		}
		iconList.clear(); 

		progressDialog = new ProgressDialog(mActivity);
		progressDialog.setMessage("Loading message. Please wait...");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	/* draw vertical line in the background */
		slinePaint.setStrokeWidth(3f);
		slinePaint.setColor(Color.WHITE);
		slinePaint.setAlpha(80);
		slinePaint.setStyle(Paint.Style.STROKE);
		slinePaint.setStrokeCap(Cap.ROUND);
		slinePaint.setAntiAlias(true);

		textPaint.setStrokeWidth(2f);
		textPaint.setColor(Color.WHITE);
		textPaint.setAlpha(80);
		textPaint.setStyle(Paint.Style.STROKE);
		
		iconPaint.setStrokeWidth(4f);
		iconPaint.setColor(Color.WHITE);
		//iconPaint.setAlpha(200);
		iconPaint.setStyle(Paint.Style.STROKE);
		iconPaint.setStrokeCap(Cap.ROUND);
		iconPaint.setAntiAlias(true);
	}
	
	@Override
	public void run() {
		while (flag) {
			if(D_SHOW_TAG){
				Log.w(TAG, "In HistoryView run + iconList size = "+iconList.size());
			}			
			lockCanvasAndDrawHistoryView();
			sleepThread();
		} // while

	}
	
	private void lockCanvasAndDrawHistoryView(){
		try {
			canvas = holder.lockCanvas();
			drawBackground(canvas);

			drawIcons();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (canvas != null) {
				// 解鎖畫布(canvas)並顯示到螢幕上
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	private void drawBackground(Canvas canvas) {
		if(D_METHOD){
			Log.w(TAG, "in drawBackground()");
			Log.w(TAG, getWidth() + " " + getHeight());
		}
		
		mRect.set(0, 0, getWidth(), getHeight() * 2);
		mWidth = getWidth();
		mHeight = getHeight();
		mCenterX = mWidth / 2;
		mCenterY = 9 * (mHeight / 10);
		// Log.w(TAG, mWidth +" "+ mHeight +" "+ mCenterX + " "+mCenterY);

		if (canvas == null) {
			if(D_SHOW_TAG){
				Log.w(TAG, "drawBackground canvas is null! Stop trhead ");
			}
			flag = false; // 停止執行緒
			return;
		}

		Bitmap resizedBitmap = getResizedBitmap(
				BitmapFactory.decodeResource(res, R.drawable.background_record),
				getHeight(), getWidth());

		canvas.drawBitmap(resizedBitmap, 0, 0, null);

		int gap = 4;
		int band = 180;
		
		for (int i = 0; i < gap; i++) {
			canvas.drawCircle(mCenterX, mCenterY, band * (i + 1), slinePaint);
		}

	}//drawBackground()
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();

		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		bm.recycle();
		return resizedBitmap;

	}
	

	public void drawIcons() {
		for (CircleIcon icon : iconList) {
			icon.PostUnit(canvas);
		}
		refetchUserMap();
	}	
	
	private boolean refetchingUserMap = true;
	private void refetchUserMap() {
		if(D_METHOD){
			Log.w(TAG, "in refresh");
		}
		if(!refetchingUserMap){
			return;
		}
		
		if(mUserIdMap != null){
			traverseMap(mUserIdMap);
			refetchingUserMap = false; 
            progressDialog.dismiss();
		}		
	}

	public  void traverseMap(HashMap<Long,UserData> mp) {
		Log.w(TAG, "in Traverse Map");
	    Iterator it = mp.entrySet().iterator();
	    float width  = getWidth();
	    float height = getHeight();
	    while (it.hasNext()) {
	    	HashMap.Entry pairs = (HashMap.Entry)it.next();
	    	UserData userdata = (UserData)pairs.getValue();
	    	if(userdata.isOwner()){
	    		//TODO not a good way, need to modify
	    		iconList.add(new CircleIcon(userdata.getUserPic() , this.mCenterX, this.mCenterY, userdata));
	    		continue;
	    	}
	    	
	    	float x = (float) (Math.random() * (width - userdata.getUserPic().getWidth()*2))+userdata.getUserPic().getWidth();
	        float y = (float) (Math.random() * ( height -  userdata.getUserPic().getHeight()*2 ) + userdata.getUserPic().getHeight());
	    	if(D_SHOW_TAG){
	    		Log.w(TAG, "random x, y "+x+ " "+ y);
	    	}
	    	iconList.add(new CircleIcon(userdata.getUserPic() , x, y, userdata));
	    	
	    }
	}
	
	private void sleepThread(){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		Log.w(TAG, "in onDraw()");

	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();
            for (CircleIcon a: iconList) {
                a.IsTouch(x, y);
            }
        }
        return true;
    }

	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.w(TAG, "In surfaceChanged()");
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.w(TAG, "In surfaceCreated()");
		db_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.w(TAG, "In surfaceDestroyed()");
	}
	


	public HashMap<Long, UserData> getmUserIdMap() {
		return mUserIdMap;
	}


	public void setmUserIdMap(HashMap<Long, UserData> mUserIdMap) {
		Log.w(TAG, "in setmUserIdMap");
		this.mUserIdMap = mUserIdMap;
	}
	


	public class CircleIcon implements Runnable {
		private Bitmap unit_bmp;
		Rect unit_rect = new Rect(); 
	    private AlertDialog mAlertDialog;
	    private float circleX ;
	    private float circleY ;
	    private UserData userData ;
	    private int index;
		int unit_Width ;
		int unit_Height;

	    
	    private void setUserData(UserData usd){
	    	
	    	userData = usd;
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
	    
		public CircleIcon(Bitmap icon_pic , float _x, float _y, UserData userdata) {
			if(D_METHOD){
				Log.w(TAG,"In CircleIcon");
			}
			this.unit_bmp = icon_pic;
			unit_Width = unit_bmp.getWidth();
			unit_Height = unit_bmp.getHeight();
			circleX= _x;
			circleY = _y;
			userData = userdata;
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
		protected void PostUnit(Canvas canvas) {
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
	    protected void IsTouch(int touch_x, int touch_y) {

	        //將觸碰點的座標 touch_x 與 touch_y 傳入到
	        //矩形框類別變數 unit_rect 的 contains(x, y) 方法中去判別
	        //如果觸碰點的座標位於矩形框範圍內則contains(x, y)方法會傳回 true
	        //否則傳回 false
        	 if( userData == null || userData.isOwner()){
        		 
        		 return;
        	 }

	        if (unit_rect.contains(touch_x, touch_y)) {
	        	if(D_SHOW_TAG){
	        		Log.w("CirecleIcon", "Is touched"+touch_x+" "+touch_y+" "+unit_rect.bottom+" "+unit_rect.left+" "+unit_rect.right+" "+unit_rect.top);
	        	}

	        	showCustomizedDialog();

	        }
	    }
	    
	    private void showCustomizedDialog(){
        	final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent);
        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        	dialog.setCancelable(true);
        	dialog.setContentView(R.layout.dialog);
        	Button btnComplete = (Button) dialog.findViewById(R.id.button_complete);
        	TextView text_placename = (TextView) dialog.findViewById(R.id.text_place_name);
        	text_placename.setText(userData.getPlaceName());
        	//TextView text_name = (TextView) dialog.findViewById(R.id.text_name);
        	ImageView image_icon = (ImageView) dialog.findViewById(R.id.image_icon);
        	image_icon.setImageBitmap(userData.getUserPic());
        	btnComplete.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
        		
        	});
        	dialog.show();
	    }

	}//CircleIcon()

}//HistoryView
