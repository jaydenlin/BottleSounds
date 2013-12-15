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
	
	
	  public PointRenderer(Paint paint, Paint flashPaint, boolean cycleColor ){
		  mPaint = paint;
		  mFlashPaint = flashPaint;
	  }
	

	
	@Override
	public void onRender(Canvas canvas, AudioData data, Rect rect) {
	    // Calculate points for line
		int amp = ByteUtils.bytes2int(data.bytes);
		

		
		mPoints[0] = (int) historyData[0];
		mPoints[1] = historyData[1];
		mPoints[2] = rect.width()*(width/duration);
		mPoints[3] = amp/100+rect.height()/3;
		 Log.w("onRender", mPoints[0]+" "+mPoints[1]+" "+mPoints[2]+" "+mPoints[3]);
	    
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
		 Log.w("HistoryData", historyData[0]+" "+historyData[1]);
		
	}/*reNewHistoryData()*/

	

}/*PointRenderer*/
