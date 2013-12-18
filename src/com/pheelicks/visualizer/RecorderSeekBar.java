package com.pheelicks.visualizer;


import java.util.HashSet;
import java.util.Set;

import com.pheelicks.visualizer.renderer.PointRenderer;
import com.pheelicks.visualizer.renderer.Renderer;
import com.wearapp.util.ByteUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

public class RecorderSeekBar extends SeekBar {


	private static final String TAG = "RecorderSeekBar";

	  private byte[] mBytes;
	  private byte[] mFFTBytes;
	  private Rect mRect = new Rect();
	  private MediaRecorder mRecorder;


	  private Set<Renderer> mRenderers;

	  private Paint mFadePaint = new Paint();
	  private static boolean finish = false;
	  
	  

	  public RecorderSeekBar(Context context, AttributeSet attrs, int defStyle)
	  {
	    super(context, attrs);
	    init();
	  }

	  public RecorderSeekBar(Context context, AttributeSet attrs)
	  {
	    this(context, attrs, 0);
	  }

	  public RecorderSeekBar(Context context)
	  {
	    this(context, null, 0);
	  }

	  private void init() {
	    mBytes = null;
	    mFFTBytes = null;

	    mFadePaint.setColor(Color.argb(0, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
	    mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
	    mRenderers = new HashSet<Renderer>();
	    finish = false;
	    
	  }
	
	  public void setFinished(){
		  finish= true;
		  
		  
	  }

	  public void setUnFinished(){
		  finish= false;
		  
	  }
	  
	  public void setRenderWidth(float w){
		  if(this.mRenderers ==null ){return;}
		  for(Renderer renderer: this.mRenderers){
			  renderer.setWidth(w);
			  
		  }
	  }
	  
	  public void setRenderDuration(float d){
		  
		  for(Renderer renderer: this.mRenderers){
			  renderer.setDuration(d);
			  
		  }
	  }


	  public void link(MediaRecorder recorder){
		  mRecorder = recorder;
		  
	  }
	  
	  public void addRenderer(Renderer renderer)
	  {
	    if(renderer != null)
	    {
	      mRenderers.add(renderer);
	    }
	  }

	  public void clearRenderers()
	  {
	    mRenderers.clear();
	  }

	

	  Bitmap mCanvasBitmap;
	  Canvas mCanvas;


	  @Override
	  protected synchronized void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    Log.i(RecorderSeekBar.class.getSimpleName(), "In on draw "+getWidth());

	    int maxProgress = getMax();
	    
	    setRenderDuration(maxProgress);
	    setRenderWidth(getProgress());
	    
	    // Create canvas once we're ready to draw
	    mRect.set(0, 0, getWidth(), getHeight());
	     
	    if(mRecorder != null){ 
	    	int maxAmplitude = mRecorder.getMaxAmplitude();
	    	Log.i(TAG, maxAmplitude+"");
	    	
	    	mBytes = ByteUtils.int2byte(maxAmplitude);
	    }
	    

	    if(mCanvasBitmap == null)
	    {
	      mCanvasBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
	      
	    }
	    
	    if(mCanvas == null)
	    {
	      mCanvas = new Canvas(mCanvasBitmap);	      
	    }
	   
	   // mCanvas.drawPaint(mFadePaint);
	    
	    if (mBytes != null) {
	      // Render all audio renderers
	      AudioData audioData = new AudioData(mBytes);
	      for(Renderer r : mRenderers)
	      {
	        r.render(mCanvas, audioData, mRect);
	      }
	    }

	    if (mFFTBytes != null) {
	      // Render all FFT renderers
	      FFTData fftData = new FFTData(mFFTBytes);
	      for(Renderer r : mRenderers)
	      {
	        r.render(mCanvas, fftData, mRect);
	      }
	    }
	    
	    
	    // Fade out old contents
	    drawBackGround(mCanvas);
	    canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
	    if(finish){
	    	setRenderWidth(0);
	    	
	    	reDraw();
	    	
	    }
	  }
	  
	  public void reDraw(){
		    Log.i(RecorderSeekBar.class.getSimpleName(), "In redraw ");
		    Paint clearPaint = new Paint();
		    clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		    mCanvas.drawPaint(clearPaint);
		    drawBackGround(mCanvas);
		    mCanvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
		    
		    for(Renderer renderer: this.mRenderers){
	    		Log.w(TAG,renderer.getClass().toString() );
					 PointRenderer pointrenderer = (PointRenderer)renderer;
					 pointrenderer.reNewHistoryData(0,0);
			  }
		  
	  }
	  
	  
	  public BitmapDrawable getBitmap(){
		  
		  return new BitmapDrawable(getResources(), mCanvasBitmap);
		  
	  }
	  
	  public void drawBackGround(Canvas canvas){
		  Paint linePaint = new Paint();
		  float width = getWidth();
		  float height = getHeight();
		  linePaint.setStrokeWidth(3f);
		    linePaint.setAlpha(255);
		    linePaint.setColor(Color.WHITE);
		    /*draw horizental line on the top and the bottom*/
		    
		    linePaint.setStrokeWidth(2f);
		    canvas.drawLine(0, 10, width, 10, linePaint);
		    canvas.drawLine(0, height-10, width, height-10, linePaint);
		    canvas.drawLine(0, height/2, width, height/2, linePaint);
		    drawExtraLine(canvas);
		
	  }	  
	  
	  public void drawExtraLine(Canvas canvas){
		  Paint slinePaint = new Paint();
		  float width = getWidth();
		  float height = getHeight();
		   
		  /*draw vertical line in the background*/
		    slinePaint.setStrokeWidth(2f);
		   
		    slinePaint.setColor(Color.WHITE);
		    slinePaint.setAlpha(80);
		    int gap = 5;
		    for (int i = 0; i< gap;i++){
		    	float gapWidth = (width/gap) - 1; 
		    	float startX = gapWidth*(i+1);
		    	float stopX = startX; 
		    	float startY = 0;
		    	float stopY = 8;
		    	canvas.drawLine(startX, startY, stopX, stopY, slinePaint);
		    	
		    	
		    	/*draw small gap*/
		    	 int smallgap = 3;
		    	for(int j =1 ; j< smallgap; j++){
			    	 float smallgapWidth = (gapWidth/smallgap);
			    	 float smallstartX = startX-smallgapWidth*j;
			    	 float smallstopX = smallstartX; 
			    	 float smallstartY = 0;
			    	 float smallstopY = 8;
			    	canvas.drawLine(smallstartX, smallstartY, smallstopX, smallstopY, slinePaint);
		    	}	
		    }	  
	  } 
	  

	

}/*RecordSeekBar*/
