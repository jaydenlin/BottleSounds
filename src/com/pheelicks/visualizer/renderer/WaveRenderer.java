package com.pheelicks.visualizer.renderer;



import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pheelicks.visualizer.AudioData;
import com.pheelicks.visualizer.FFTData;


public class WaveRenderer extends Renderer{

	 private Paint mPaint;
	  private Paint mFlashPaint;
	  private boolean mCycleColor;
	  private float amplitude = 0;
	
	
	  public WaveRenderer(Paint paint, Paint flashPaint, boolean cycleColor ){
		  mPaint = paint;
		  mFlashPaint = flashPaint;
		  mCycleColor = cycleColor;
	
	
	  }
	
	
	@Override
	public void onRender(Canvas canvas, AudioData data, Rect rect) {
	    // Calculate points for line
	    for (int i = 0; i < data.bytes.length - 1; i++) {
	      mPoints[i * 4] = rect.width() * i / (data.bytes.length - 1);
	      mPoints[i * 4 + 1] =  rect.height() / 2
	          + ((byte) (data.bytes[i] + 128)) * (rect.height() / 3) / 128;
	      mPoints[i * 4 + 2] = rect.width() * (i + 1) / (data.bytes.length - 1);
	      mPoints[i * 4 + 3] = rect.height() / 2
	          + ((byte) (data.bytes[i + 1] + 128)) * (rect.height() / 3) / 128;
	    }

	    // Calc amplitude for this waveform
	    float accumulator = 0;
	    for (int i = 0; i < data.bytes.length - 1; i++) {
	      accumulator += Math.abs(data.bytes[i]);
	    }

	    float amp = accumulator/(128 * data.bytes.length);
	    canvas.drawLines(mPoints, mFlashPaint);
	    /*
	    if(amp > amplitude)
	    {
	      // Amplitude is bigger than normal, make a prominent line
	      amplitude = amp;
	      canvas.drawLines(mPoints, mFlashPaint);
	    }
	    else
	    {
	      // Amplitude is nothing special, reduce the amplitude
	      amplitude *= 0.99;
	      canvas.drawLines(mPoints, mPaint);
	    }*/		
	}

	@Override
	public void onRender(Canvas canvas, FFTData data, Rect rect) {
		// TODO Auto-generated method stub
		
	}

}
