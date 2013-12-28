package com.pheelicks.visualizer.renderer;




import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.pheelicks.visualizer.AudioData;
import com.pheelicks.visualizer.FFTData;
import com.wearapp.util.ByteUtils;


public class PointRenderer extends Renderer{

	  private Paint mPaint;
	  private Paint mFlashPaint;
	  public int[] historyData={0,0};
	  private float height = 0; 
	
	
	  public PointRenderer(Paint paint, Paint flashPaint, boolean cycleColor ){
		  mPaint = paint;
		  mFlashPaint = flashPaint;
	  }
	

	
	@Override
	public void onRender(Canvas canvas, AudioData data, Rect rect) {
	    // Calculate points for line
		int amp = ByteUtils.bytes2int(data.bytes);
		
		height = rect.height();
		
		mPoints[0] = (int) historyData[0];
		mPoints[1] = historyData[1];
		mPoints[2] = rect.width()*(width/duration);
		mPoints[3] = sinWave(amp, mPoints[2])+rect.height()/2;
		
		 //Log.w("onRender", mPoints[0]+" "+mPoints[1]+" "+mPoints[2]+" "+mPoints[3]);
	    
	    canvas.drawLines(mPoints, mFlashPaint);
	    reNewHistoryData(mPoints[2],mPoints[3]);
	}

	@Override
	public void onRender(Canvas canvas, FFTData data, Rect rect) {
		// TODO Auto-generated method stub
		
	}
	
	public void reNewHistoryData(float pointX, float pointY){
		 historyData[0] = (int) pointX;
		 historyData[1] = (int) pointY;
		 //Log.w("HistoryData", historyData[0]+" "+historyData[1]);
		
	}/*reNewHistoryData()*/

	private float period  =0;
	private float maxAmp = 0;
	private float preY;
	public float sinWave(float amp, float axisX ){
		float axisY = (float)(Math.sin(axisX/2));
		//Log.i("In sinWave", "X:"+axisX+" Y:"+axisY+" AMP:"+ maxAmp+" P:"+period);
		if(period == 0){
			if(amp == 0) { amp = height/5;}
			maxAmp = amp/45;
			period =2;
		}
		
		if ( (axisY -preY) < 0 && axisY < 0 && preY > 0 ){
			period--;
		} else if ((axisY -preY) > 0 && axisY > 0 && preY < 0){
			
			period--;
		}
		preY = axisY;
		axisY = maxAmp * axisY;
		
		return axisY;	
		
	}

}/*PointRenderer*/
