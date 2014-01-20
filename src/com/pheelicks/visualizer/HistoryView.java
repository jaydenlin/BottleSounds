package com.pheelicks.visualizer;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class HistoryView extends View{
	
	private static final String TAG = "HistoryView";

	private Paint mpaint = new Paint();
	private RectF mRect = new RectF();


	  public HistoryView(Context context, AttributeSet attrs, int defStyle)
	  {
	    super(context, attrs);
	    init();
	  }

	  public HistoryView(Context context, AttributeSet attrs)
	  {
	    this(context, attrs, 0);
	  }

	  public HistoryView(Context context)
	  {
	    this(context, null, 0);
	  }

	  private void init() {
	    
	  }
	  
	  Bitmap mCanvasBitmap;
	  Canvas mCanvas;

	  @Override
	  protected void onDraw(Canvas canvas) {
		    super.onDraw(canvas);
		    
		 // Create canvas once we're ready to draw
		    mRect.set(0, 0, getWidth(), getHeight()*2);

		    if(mCanvasBitmap == null)
		    {
		      mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
		      
		    }
		    if(mCanvas == null)
		    {
		      mCanvas = new Canvas(mCanvasBitmap);
		      
		    }
			  Paint slinePaint = new Paint();
			  Paint textPaint = new Paint();
			  float width = getWidth();
			  float height = getHeight();
			  float centerX = width/2;
			  float centerY = 9*(height/10);

			   
			  /*draw vertical line in the background*/
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
			    int band =110;
		
			    for (int i = 1; i< gap;i++){
	
			    canvas.drawCircle(centerX, centerY, band*i, slinePaint);
			    
			    }

	  
	  }//onDraw()
	  
	  private void drawIcon(Canvas canvas){
		  
		  Paint circlePaint = new Paint();
		  circlePaint.setColor(R.color.white);
		  circlePaint.setAntiAlias(true);
		  
		  
		  
	  }
	
	  
	  public class CircleIcon implements Runnable{
		  private Bitmap unit_bmp;
		  
		  public CircleIcon(Bitmap icon_pic){
			   //指定圖片來源
		        this.unit_bmp = unit_bmp;
		        
		        //此物件參數的初始設定
		        UnitInitial();      
		      
		        //CircleIcon 類別實作了 Runnable 介面
		        //仍需以 Thread 類別來建立執行緒，如下 :
		        new Thread(this).start();
		        
		        //將 AndroidUnit 類別本身(this)當做參數並實體化為執行緒
		        //啟動 .start() 方法會自動執行 run() 函式內的程式內容
		        //當 AndroidUnit 此類別 被實體化時，便會同時啟動此類別的執行緒
		        //例如 : AndroidUnit au = new AndroidUnit(bmp);
			  
		  }

		private void UnitInitial() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		  
	  }
	

}
