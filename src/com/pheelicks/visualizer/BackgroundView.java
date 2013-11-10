package com.pheelicks.visualizer;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.View;


public class BackgroundView extends View{
	
	private static final String TAG = "VisualizerView";

	private Paint mpaint = new Paint();
	private Rect mRect = new Rect();


	  public BackgroundView(Context context, AttributeSet attrs, int defStyle)
	  {
	    super(context, attrs);
	    init();
	  }

	  public BackgroundView(Context context, AttributeSet attrs)
	  {
	    this(context, attrs, 0);
	  }

	  public BackgroundView(Context context)
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
		    mRect.set(0, 0, getWidth(), getHeight());

		    if(mCanvasBitmap == null)
		    {
		      mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
		      
		    }
		    if(mCanvas == null)
		    {
		      mCanvas = new Canvas(mCanvasBitmap);
		      
		    }
		    Paint linePaint = new Paint();
			  Paint slinePaint = new Paint();
			  float width = getWidth();
			  float height = getHeight();
			   
			  /*draw vertical line in the background*/
			    slinePaint.setStrokeWidth(2f);
			   
			    slinePaint.setColor(Color.WHITE);
			    slinePaint.setAlpha(80);
			    int gap = 5;
			    for (int i = 0; i< gap;i++){
			    	float gapWidth = (width/gap)-1;
			    	float startX = gapWidth*(i+1);
			    	float stopX = startX; 
			    	float startY = 10;
			    	float stopY = height-1;
			    	canvas.drawLine(startX, startY, stopX, stopY, slinePaint);
			    	
			    	
			    	/*draw small gap*/
			    	 int smallgap = 3;
			    	for(int j =1 ; j< smallgap; j++){
				    	 float smallgapWidth = (gapWidth/smallgap);
				    	 float smallstartX = startX-smallgapWidth*j;
				    	 float smallstopX = smallstartX; 
				    	 float smallstartY = height-10;
				    	 float smallstopY = height-1;
				    	canvas.drawLine(smallstartX, smallstartY, smallstopX, smallstopY, slinePaint);
			    	}
			    	
			    	
			    }

	  
	  }
}
