package com.pheelicks.visualizer;

import java.util.HashSet;
import java.util.Set;

import com.pheelicks.visualizer.renderer.Renderer;

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
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

public class MediaSeekBar extends SeekBar {


	private static final String TAG = "MediaSeekBar";

	  private byte[] mBytes;
	  private byte[] mFFTBytes;
	  private Rect mRect = new Rect();
	  private Visualizer mVisualizer;
	  private MediaPlayer mPlayer;
	  private int mProgress;
	  private int maxProgress;

	  private Set<Renderer> mRenderers;

	  private Paint mFadePaint = new Paint();
	  private static boolean finish = false;
	  
	  

	  public MediaSeekBar(Context context, AttributeSet attrs, int defStyle)
	  {
	    super(context, attrs);
	    init();
	  }

	  public MediaSeekBar(Context context, AttributeSet attrs)
	  {
	    this(context, attrs, 0);
	  }

	  public MediaSeekBar(Context context)
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
	  
	  public Visualizer getVisualizer(){
		  
		  return mVisualizer;
	  }
	  
	  public void releaseVisulizer(){
		  
		  mVisualizer.release();
		  mVisualizer = null;
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
	  /*
	  public View testInit(){
		  
		    View view = RecordActivity.mInflater.inflate(R.layout.sekbarview, null);
		  return view;
	  }*/

	  /**
	   * Links the visualizer to a player
	   * @param player - MediaPlayer instance to link to
	   */
	  public void link(MediaPlayer player)
	  {
		  
		  maxProgress = 0;
		  mProgress =0;
	    if(player == null)
	    {
	      throw new NullPointerException("Cannot link to null MediaPlayer");
	    }
	    if(mVisualizer != null){ releaseVisulizer();}
	    // Create the Visualizer object and attach it to our media player.
	    mVisualizer = new Visualizer(player.getAudioSessionId());
	    mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
	    mPlayer = player;

	    // Pass through Visualizer data to MediaSeekBar
	    Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
	    {
	      @Override
	      public synchronized void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
	          int samplingRate)
	      {
	        updateVisualizer(bytes);
	      }

	      
	      @Override
	      public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
	          int samplingRate)
	      {
	       //updateVisualizerFFT(bytes);
	      //TODO
	      }
	    };

	    mVisualizer.setDataCaptureListener(captureListener,
	        Visualizer.getMaxCaptureRate() / 2, true, true);

	    // Enabled Visualizer and disable when we're done with the stream
	    mVisualizer.setEnabled(true);
	    this.setMax(player.getDuration());
	    

	    

	  }/*link()*/

	  
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

	  /**
	   * Call to release the resources used by MediaSeekBar. Like with the
	   * MediaPlayer it is good practice to call this method
	   */
	  public void release()
	  {
	    mVisualizer.release();
	  }

	  /**
	   * Pass data to the visualizer. Typically this will be obtained from the
	   * Android Visualizer.OnDataCaptureListener call back. See
	   * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
	   * @param bytes
	   */
	  public synchronized void updateVisualizer(byte[] bytes) {
	    mBytes = bytes;
	    mProgress = mPlayer.getCurrentPosition();
	    if(maxProgress < mProgress){
	    	
	    	maxProgress = mProgress;
	    }
	    
	    setRenderWidth(maxProgress);
	    this.setProgress(maxProgress);
	    Log.i(TAG, mProgress+" "+System.currentTimeMillis());
	    invalidate();
	  }

	  /**
	   * Pass FFT data to the visualizer. Typically this will be obtained from the
	   * Android Visualizer.OnDataCaptureListener call back. See
	   * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
	   * @param bytes
	   */
	  public synchronized void updateVisualizerFFT(byte[] bytes) {
	    mFFTBytes = bytes;
	   // mProgress = mPlayer.getCurrentPosition();
	   // setRenderWidth(mProgress);
	   // this.setProgress(mProgress);
	    invalidate();
	  }

	  boolean mFlash = false;

	  /**
	   * Call this to make the visualizer flash. Useful for flashing at the start
	   * of a song/loop etc...
	   */
	  public void flash() {
	    mFlash = false;
	    invalidate();
	  }

	  Bitmap mCanvasBitmap;
	  Canvas mCanvas;


	  @Override
	  protected synchronized void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    Log.i(MediaSeekBar.class.getSimpleName(), "In on draw "+getWidth());

	    int maxProgress = getMax();
	    
	    setRenderDuration(maxProgress);
	    
	    // Create canvas once we're ready to draw
	    mRect.set(0, 0, getWidth(), getHeight());

	    if(mCanvasBitmap == null)
	    {
	      mCanvasBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
	      
	    }
	    if(mCanvas == null)
	    {
	      mCanvas = new Canvas(mCanvasBitmap);	      
	    }
	   
	    mCanvas.drawPaint(mFadePaint);
	    
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
	    if(finish){reDraw();}
	  }
	  
	  public void reDraw(){
		    Log.i(MediaSeekBar.class.getSimpleName(), "In redraw ");
		    Paint clearPaint = new Paint();
		    clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		    mCanvas.drawPaint(clearPaint);
		   drawBackGround(mCanvas);
		   mCanvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
		  
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
		    canvas.drawLine(0, 5, width, 5, linePaint);
		    canvas.drawLine(0, height-5, width, height-5, linePaint);
		    canvas.drawLine(0, height/2, width, height/2, linePaint);
		
	  }	  

}
