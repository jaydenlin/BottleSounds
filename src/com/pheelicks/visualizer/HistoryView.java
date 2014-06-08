package com.pheelicks.visualizer;


import java.util.ArrayList;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wearapp.HistoryActivity;
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
	private Thread db_thread;
	float mCenterX; 
	float mCenterY; 
	float mWidth;
	float mHeight;
	private Context mContext;
	private HistoryActivity mActivity;
	private ArrayList<HistoryActivity.UserData> userList ;
	private Builder mAlertDialogBuilder; 
 
  
	Paint slinePaint;
	Paint textPaint;
	

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
		userList = new ArrayList<HistoryActivity.UserData>();
		mActivity = activity;
		mContext = context;
		res = getResources();
		setWillNotDraw(false);
		getHolder().addCallback(this);
		holder = getHolder();
		slinePaint = new Paint();
		textPaint = new Paint();
		
		InitialSetting();
		
		db_thread = new Thread(this);
		
	}

	public void InitialSetting() {
		if(D_METHOD){
			Log.w(TAG, "In InitialSet");
		}
	
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
		

	}
	
	@Override
	public void run() {
		while (flag) {
			if(D_SHOW_TAG){
				Log.w(TAG, "In HistoryView run ");
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
	
	public ArrayList<HistoryActivity.UserData> getUserList(){

		
		return this.userList;
	}

	public void drawIcons() {
		Log.w(TAG, "In drawIcons()");
		
		if(refetchingUserMap){
			refetchUserMap();
			
		}
		
		for (UserData user : userList){
			if(user.getUserPic()!= null){
				user.PostUnit(canvas);
			}
		}
	}	
	
	private boolean refetchingUserMap = true;
	private void refetchUserMap() {
		if(D_METHOD){
			Log.w(TAG, "in refresh");
		}
		if(userList != null){
			refetchingUserMap = traverseList(userList);
		}		
	}

	public boolean traverseList(ArrayList<HistoryActivity.UserData> list) {
		int count = 0;
		Log.w(TAG, "in Traverse Map");
		float width = getWidth();
		float height = getHeight();
		for (UserData userData : list) {
			Log.w(TAG, "UserId = " + userData.getUID());
			if (userData.getUserPic() == null) {
				Log.w(TAG, userData.getUID() + " still not get profile picture");
				continue;
			}
			count++;
			if (userData.isOwner()) {
				userData.setsCircleIcon(this.mCenterX, this.mCenterY);
				continue;
			}

			float x = (float) (Math.random() * (width - userData.getUserPic()
					.getWidth() * 2)) + userData.getUserPic().getWidth();
			float y = (float) (Math.random()
					* (height - userData.getUserPic().getHeight() * 2) + userData
					.getUserPic().getHeight());
			Log.w(TAG, "random x, y " + x + " " + y);

			userData.setsCircleIcon(x, y);

		}

		return (list.size() == count);

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
		//Log.w(TAG, "in onDraw()");

	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();
            for (UserData a: userList) {
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
	


}//HistoryView
