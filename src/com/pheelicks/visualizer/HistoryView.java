package com.pheelicks.visualizer;

import java.util.ArrayList;

import com.facebook.widget.ProfilePictureView;
import com.wearapp.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Paint.Cap;
import android.graphics.PorterDuff.Mode;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class HistoryView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	private static final String TAG = "HistoryView";

	private RectF mRect = new RectF();
	private Resources res;
	private Bitmap bmp;
	private boolean flag = true;
	private Canvas canvas = null;
	private SurfaceHolder holder;
	private ArrayList<CircleIcon> iconList; // ICON 類別型態的物件陣列
	private Thread db_thread;
	float mCenterX; 
	float mCenterY; 
	float mWidth;
	float mHeight;
	private ArrayList<Integer> mUserIdList = new ArrayList<Integer>(); 

	public HistoryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init();
	}

	public HistoryView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HistoryView(Context context) {
		super(context);
		setWillNotDraw(false);
		Log.w(TAG, "On Create HistoryView");

		getHolder().addCallback(this);
		holder = getHolder();

		// 指定圖片來源
		res = getResources();
		bmp = BitmapFactory.decodeResource(res, R.drawable.head_icon);
	   
		// 初始設定
		InitialSet();
		
		// 建立執行緒
		db_thread = new Thread(this);
	}

	// ==== 初始設定 ====
	public void InitialSet() {
		Log.w(TAG, "In InitialSet");
		// 建立 AndroidUnit 物件陣列實體
		iconList = new ArrayList<CircleIcon>();
		iconList.clear(); // 先清除 Au 物件陣列

		// 建立 AndroidUnit 物件 10 隻
		for (int i = 0; i < 10; i++) {
			// 產生 AndroidUnit 實體 au
			
			CircleIcon au = new CircleIcon(bmp);
			// 陸續將 au 放入 Au 物件陣列中
			iconList.add(au);
		}
	}

	private void init() {

	}

	@Override
	public void onDraw(Canvas canvas) {
		Log.w(TAG, "in onDraw()");
		// drawBackground(canvas);

	}

	private void drawBackground(Canvas canvas) {
		Log.w(TAG, "in drawBackground()");
		// Create canvas once we're ready to draw
		mRect.set(0, 0, getWidth(), getHeight() * 2);
		 mWidth = getWidth();
			mHeight = getHeight();
			mCenterX = mWidth / 2;
			mCenterY = 9 * (mHeight / 10);
			Log.w(TAG, mWidth +" "+ mHeight +" "+ mCenterX + " "+mCenterY);

		/*
		 * if (mCanvasBitmap == null) { mCanvasBitmap =
		 * Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(),
		 * Config.ARGB_8888);
		 * 
		 * 
		 * } if (canvas == null) { canvas = new Canvas(mCanvasBitmap);
		 * 
		 * }
		 */
		Paint slinePaint = new Paint();
		Paint textPaint = new Paint();
		

		Bitmap resizedBitmap = getResizedBitmap(
				BitmapFactory.decodeResource(res, R.drawable.background_record),
				getHeight(), getWidth());

		canvas.drawBitmap(resizedBitmap, 0, 0, null);

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

		int gap = 6;
		int band = 110;

		for (int i = 1; i < gap; i++) {

			canvas.drawCircle(mCenterX, mCenterY, band * i, slinePaint);

		}

	}

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

	
	public void setUserIdList(ArrayList<Integer> list){
		mUserIdList = list;
		
	}


	public class CircleIcon implements Runnable {
		private Bitmap unit_bmp;
		//TODO private ProfilePictureView profilePic = new ProfilePictureView(Context context);

		public CircleIcon(Bitmap icon_pic) {
			// 指定圖片來源
			this.unit_bmp = icon_pic;
			//profilePic.setProfileId("1082621562");
			
			// 此物件參數的初始設定
			UnitInitial();

			// CircleIcon 類別實作了 Runnable 介面
			// 仍需以 Thread 類別來建立執行緒，如下 :
			new Thread(this).start();

			// 將 AndroidUnit 類別本身(this)當做參數並實體化為執行緒
			// 啟動 .start() 方法會自動執行 run() 函式內的程式內容
			// 當 AndroidUnit 此類別 被實體化時，便會同時啟動此類別的執行緒
			// 例如 : AndroidUnit au = new AndroidUnit(bmp);

		}

		private void UnitInitial() {
			// TODO Auto-generated method stub

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

		// ==== 將圖 PO 到 canvas(畫布)上 ====
		protected void PostUnit(Canvas canvas) {
			// 在 canvas 上繪出物件本體
			float circleX =  mCenterX - unit_bmp.getWidth()/2;
			float circleY = mCenterY - unit_bmp.getHeight()/2;
			canvas.drawBitmap(getCroppedBitmap(unit_bmp),circleX , circleY , null);
			//TODO profilePic.buildLayer();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.w(TAG, "In HistoryView run");
		while (flag) {
			// 當 Au 物件陣列沒有任何物件存在時，結束執行緒運作
			if (iconList.isEmpty()) {
				Log.w(TAG, "iconList is empty! Stop trhead ");
				flag = false; // 停止執行緒
				System.exit(0); // 直接結束程式
			}

			// 將物件顯示到螢幕上
			try {

				// 取得並鎖住畫布(canvas)
				canvas = holder.lockCanvas();

				// 以黑色當背景 (清除畫面)
				// canvas.drawColor(Color.BLACK);
				drawBackground(canvas);

				// 巡覽 Au 物件陣列中的所有物件

				for (CircleIcon icon : iconList) {
					// 若該物件還活著，則呼叫 AndroidUnit 物件的 PostUnit() 方法
					// 將物件圖片繪至 canvas 上
					icon.PostUnit(canvas);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null) {
					// 解鎖畫布(canvas)並顯示到螢幕上
					holder.unlockCanvasAndPost(canvas);
				}
			}

			// 暫停 0.05 秒(每隔 0.05 秒更新畫面一次)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} // while

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
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
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

}
