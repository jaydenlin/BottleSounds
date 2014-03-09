package com.pheelicks.visualizer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
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
import com.wearapp.HistoryActivity.UserData;
import com.wearapp.R;

public class HistoryView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	private static final String TAG = "HistoryView";

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
	

	private HashMap<Integer,UserData> mUserIdMap ;

	private Builder mAlertDialogBuilder; 

	public HistoryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init();
	}

	public HistoryView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HistoryView(Context context,HistoryActivity activity) {
		super(context);
		setWillNotDraw(false);
		Log.w(TAG, "On Create HistoryView");
		getHolder().addCallback(this);
		holder = getHolder();
		res = getResources();
		mContext = context;
		// 初始設定
		InitialSet();
		
		// 建立執行緒
		db_thread = new Thread(this);
		mActivity = activity;
	}

	// ==== 初始設定 ====
	public void InitialSet() {
		Log.w(TAG, "In InitialSet");
		// 建立 AndroidUnit 物件陣列實體
		iconList = new ArrayList<CircleIcon>();
		iconList.clear(); // 先清除 Au 物件陣列
	}

	private void init() {

	}

	@Override
	public void onDraw(Canvas canvas) {
		Log.w(TAG, "in onDraw()");

	}

	private void drawBackground(Canvas canvas) {
		//Log.w(TAG, "in drawBackground()");
		// Create canvas once we're ready to draw
		Log.w(TAG, getWidth()+" "+getHeight());
		mRect.set(0, 0, getWidth(), getHeight() * 2);
		 mWidth = getWidth();
			mHeight = getHeight();
			mCenterX = mWidth / 2;
			mCenterY = 9 * (mHeight / 10);
			//Log.w(TAG, mWidth +" "+ mHeight +" "+ mCenterX + " "+mCenterY);

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
		int band = 100;

		for (int i = 1; i < gap; i++) {
			
			canvas.drawCircle(mCenterX, mCenterY, band * (i+1), slinePaint);

		}

	}
	
	 //==== 加入觸碰事件方法 ====
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();
            
            //巡覽 Au 物件陣列一遍，逐一比對是否碰觸到物件圖片
            for (CircleIcon a: iconList) {
            	
                a.IsTouch(x, y);
            }
        }
        return true;
    }

    public HashMap<Integer, UserData> getmUserIdMap() {
		return mUserIdMap;
	}

	public void setmUserIdMap(HashMap<Integer, UserData> mUserIdMap) {
		Log.w(TAG, "in setmUserIdMap");
		this.mUserIdMap = mUserIdMap;
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

	@Override
	public void run() {
	
		
		while (flag) {
			Log.w(TAG, "In HistoryView run + iconList size = "+iconList.size());
			
			// 當 Au 物件陣列沒有任何物件存在時，結束執行緒運作
			/*
			if (iconList.isEmpty() ) {
				Log.w(TAG, "iconList is empty! Stop trhead ");
				flag = false; // 停止執行緒
				System.exit(0); // 直接結束程式
			}*/
			
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
				refresh();
				
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
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} // while

	}
	
	private boolean refresh = true;
	private void refresh() {
		Log.w(TAG, "in refresh");
		if(!refresh){
			return;
		}
		
		
		if(mUserIdMap != null){
			traverseMap(mUserIdMap);
			refresh = false; 
		}		
	}

	public  void traverseMap(HashMap<Integer,UserData> mp) {
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
	    	Log.w(TAG, "random x, y "+x+ " "+ y);
	    	iconList.add(new CircleIcon(userdata.getUserPic() , x, y, userdata));
	    	
	    }
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
	

	public class CircleIcon implements Runnable {
		private Bitmap unit_bmp;
		Rect unit_rect = new Rect(); 
	    private AlertDialog mAlertDialog;
	    private float x ;
	    private float y ;
	    private UserData userData ;
	    private int index;
		int unit_Width ;
		int unit_Height;

	    
	    private void setUserData(UserData usd){
	    	
	    	userData = usd;
	    }
 
		public void setX(float _x) {
			x = _x;
		}

		public void setY(float _y) {
			y = _y;
		}
		
		public void setindex(int i){
			
			index =i;
		}
		public int getIndex(){
			return index; 
		}
	    
		public CircleIcon(Bitmap icon_pic , float _x, float _y, UserData userdata) {
			Log.w(TAG,"In CircleIcon");
			
			this.unit_bmp = icon_pic;
			unit_Width = unit_bmp.getWidth();
			unit_Height = unit_bmp.getHeight();


			x= _x;
			y = _y;
			userData = userdata;
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
			float circleX =    x- unit_Width/2;
			float circleY = y - unit_Height/2;
			Bitmap newBitmap = getCroppedBitmap(unit_bmp);
			canvas.drawBitmap(newBitmap,circleX , circleY , null);
			unit_rect.set((int)x ,(int)y , (int)x + unit_Width,(int)y+ unit_Height) ;
			//Log.w(TAG, unit_Width+" "+unit_Height);
			
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
	        	Log.w("CirecleIcon", "Is touched"+touch_x+" "+touch_y+" "+unit_rect.bottom+" "+unit_rect.left+" "+unit_rect.right+" "+unit_rect.top);

	        	//canvas.drawColor(R.color.com_facebook_blue);
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
	    }

	}

}
