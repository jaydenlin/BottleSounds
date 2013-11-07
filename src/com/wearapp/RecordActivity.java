package com.wearapp;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pheelicks.visualizer.VisualizerView;
import com.pheelicks.visualizer.renderer.LineRenderer;
import com.pheelicks.visualizer.renderer.WaveRenderer;
import com.wearapp.R;
import com.wearapp.R.id;
import com.wearapp.R.layout;
import com.wearapp.R.string;
import com.wearapp.asyncTask.UploadAsyncTask;
import com.wearapp.util.UploadUtil;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class RecordActivity extends Activity implements OnClickListener {

	// /////////////////////////////////////////
	// debug
	// /////////////////////////////////////////

	public static final String TAG = RecordActivity.class.getSimpleName();

	public static final boolean D = true;
	public static final boolean D_METHOD = D && true;

	public static final boolean D_SHOW_TAG = D && true;

	public float startTime;
	private String uploadURL = "http://jadyenlin.tw/savevoice.php";
	private File recordFile;
	DecimalFormat df2 = new DecimalFormat("00");
	
	// /////////////////////////////////////////
	// UI
	// /////////////////////////////////////////

	ImageButton imagebutton_record;
	ImageButton imagebutton_stop;
	ImageButton imagebutton_play;
	// Button button_play;
	Button button_confirm;
	TextView textview_status;
	TextView play_time_text;
	public static LayoutInflater mInflater;
	//SeekBar
	private SeekBar seekBar1;
	private SeekBar.OnSeekBarChangeListener seekBarOnSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
		
		
	
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			mPlayer.seekTo(seekBar1.getProgress());
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
		}
	};
	
	// /////////////////////////////////////////
	// handler
	// /////////////////////////////////////////

	UIHandler mUIHandler;

	class UIHandler extends Handler {
		public void handleMessage(Message msg) {

		}
	}

	// //////////////////////////////////////////
	// Media Controller //
	// //////////////////////////////////////////
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;
	private VisualizerView mVisualizerView;
	
	private String VOICE_FILE_PATH;
	private boolean isRecorded = false;
	private boolean isPlayState = false;

	protected Handler handler =new Handler();

	public void setVoiceFilePath(String filepath) {
		if (D_METHOD) {
			Log.w(TAG, "Set Voice File Path" + filepath);
		}
		this.VOICE_FILE_PATH = filepath;
	}

	public String getVoiceFilePath() {

		return this.VOICE_FILE_PATH;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.record_activity);

		initView();
		button_confirm.setOnClickListener(this);
		// button_play.setOnClickListener(this);
		imagebutton_record.setOnClickListener(this);
		imagebutton_stop.setOnClickListener(this);
		imagebutton_play.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imagebutton_record:
			try {
				startRecord();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			textview_status.setText(R.string.status_recording);
			button_confirm.setText(R.string.string_complete);
			imagebutton_record.setVisibility(View.INVISIBLE);
			imagebutton_record.setClickable(false);

			imagebutton_stop.bringToFront();
			imagebutton_stop.setVisibility(View.VISIBLE);
			imagebutton_stop.setClickable(true);
			

			return;

		case R.id.imagebutton_stop:
			if(isPlayState){
				stopPlay();
					
				return;
			}
			stopRecord();
			textview_status.setText(R.string.string_recordtext);
			imagebutton_stop.setVisibility(View.INVISIBLE);
			imagebutton_stop.setClickable(false);

			imagebutton_record.bringToFront();
			imagebutton_record.setVisibility(View.VISIBLE);
			imagebutton_record.setClickable(true);
			setIsRecorded();

			return;
		case R.id.button_confirm:
			if (isRecorded) {
				button_confirm.setText(R.string.string_play);
				setIsPlayState();
				defaultRecordState();
				
				return;
			}
			if(isPlayState){
				defaultPlayStat();
				startCheckPlaceActivity();
				uploadFile();
				
			}
			
			return;

		case R.id.imagebutton_play:
			if (isPlayState) {
				handler.post(playVoice);
				button_confirm.setText(R.string.string_confirm);
				
				imagebutton_play.setClickable(false);
				imagebutton_play.setVisibility(View.INVISIBLE);
				
				imagebutton_stop.bringToFront();
				imagebutton_stop.setVisibility(View.VISIBLE);
				imagebutton_stop.setClickable(true);		
			}
			
			//调用handler播放
			return;

		}

		return;
	}

	private void setIsRecorded() {
		isRecorded = true;
	}

	private void defaultRecordState(){
		isRecorded = false;
		
	}
	
	private void defaultPlayStat(){
		isPlayState = false;
		
	}
	
	private void setIsPlayState() {

		isPlayState = true;

		imagebutton_play.bringToFront();
		imagebutton_play.setVisibility(View.VISIBLE);
		imagebutton_play.setClickable(true);

		imagebutton_record.setVisibility(View.INVISIBLE);
		imagebutton_stop.setVisibility(View.INVISIBLE);

		imagebutton_record.setClickable(false);
		imagebutton_stop.setClickable(false);

		button_confirm.setClickable(false);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (D_METHOD) {
			Log.w(TAG, "onStart");
		}

	}

	public void hideImageButton(View view) {
		switch (view.getId()) {
		case R.id.imagebutton_record:
			imagebutton_record = (ImageButton) findViewById(R.id.imagebutton_record);

			break;

		}

		return;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (D_METHOD) {
			Log.w(TAG, "onResume");
		}
	}

	@Override
	protected void onPause() {
		if (D_METHOD) {
			Log.w(TAG, "onPause");
		}

		super.onPause();
	}

	@Override
	protected void onStop() {
		if (D_METHOD) {
			Log.w(TAG, "onStop");
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (D_METHOD) {
			Log.w(TAG, "onDestroy");
		}
		super.onDestroy();
	}

	public void initView() {
		imagebutton_record = (ImageButton) findViewById(R.id.imagebutton_record);
		imagebutton_stop = (ImageButton) findViewById(R.id.imagebutton_stop);
		imagebutton_stop.setVisibility(View.INVISIBLE);
		imagebutton_play = (ImageButton) findViewById(R.id.imagebutton_play);
		imagebutton_play.setVisibility(View.INVISIBLE);
		button_confirm = (Button) findViewById(R.id.button_confirm);
		// button_confirm = (Button) findViewById(R.id.button_confirm);
		textview_status = (TextView) findViewById(R.id.recordtext);
		imagebutton_record.bringToFront();
		
		
		
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_title));
		
		
		return;
	}

	public void startRecord() throws IOException {
		if (D_METHOD) {
			Log.w(TAG, "In startRecord");
		}

		String filename = getDate();
		File fExternalDataPath = Environment.getExternalStorageDirectory();
		File myDataPath = new File(fExternalDataPath.getAbsolutePath()
				+ "/record");
		if (!myDataPath.exists())
			myDataPath.mkdirs();
		recordFile = new File(fExternalDataPath.getAbsolutePath() + "/record/"
				+ filename);
		setVoiceFilePath(recordFile.getAbsolutePath());

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOutputFile(recordFile.getAbsolutePath());
		try {
			mRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mRecorder.start(); // Recording is now started
		

	}/* startRecord() */

	public void stopRecord() {
		if (mRecorder != null) {
			if (D_METHOD) {
				Log.w(TAG, "In stopRecord");
			}

			mRecorder.stop();
			// mRecorder.reset(); // You can reuse the object by going back to
			// setAudioSource() step
			mRecorder.release(); // Now the object cannot be reused
			mRecorder = null;
		}

	}
/*
	public void playVoice() {

		if (getVoiceFilePath() == null) {
			return;
		}

		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(getVoiceFilePath());
			mPlayer.prepare();
		} catch (IllegalArgumentException e) {
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}
		seekBar1.setOnSeekBarChangeListener(seekBarOnSeekBarChange);
		mPlayer.start();
		

        // Create the Visualizer object and attach it to our media player.
	    // We need to link the visualizer view to the media player so that
	    // it displays something
	    mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
	   
	    mVisualizerView.link(mPlayer);

	    // Start with just line renderer
	    addLineRenderer();
				
		if (D_METHOD) {
			Log.w(TAG, "In playVoice " + getVoiceFilePath());
		}
	}
	*/

	public String getDate() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyMMddHHmmss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		// System.out.println(strDate);
		return strDate;
	}/* getDate */

	private void uploadFile() {
		new UploadAsyncTask().execute(recordFile);
	}

	private void startCheckPlaceActivity() {
		Intent intent = new Intent(this, CheckPlaceActivity.class);
		startActivity(intent);
	}

	
	  private void addLineRenderer()
	  {
	    Paint linePaint = new Paint();
	    linePaint.setStrokeWidth(1f);
	    linePaint.setAntiAlias(true);
	    linePaint.setColor(Color.argb(88, 0, 128, 255));

	    Paint lineFlashPaint = new Paint();
	    lineFlashPaint.setStrokeWidth(5f);
	    lineFlashPaint.setAntiAlias(false);
	    lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
	    
	    WaveRenderer waveRenderer = new WaveRenderer(linePaint, lineFlashPaint, false);
	    mVisualizerView.addRenderer(waveRenderer);
	  }
	  

	    
	    Runnable playVoice = new Runnable(){
	 
			@Override
			public void run() {
				if (getVoiceFilePath() == null) {
					return;
				}

				button_confirm.setClickable(false);
				mPlayer = new MediaPlayer();
			    play_time_text = (TextView)findViewById(R.id.play_time_text);

				
				try {
					mPlayer.setDataSource(getVoiceFilePath());
					mPlayer.prepare();
				} catch (IllegalArgumentException e) {
				} catch (IllegalStateException e) {
				} catch (IOException e) {
				}
				int duration= mPlayer.getDuration();
		        //音乐文件持续时间
				seekBar1 = (SeekBar)findViewById(R.id.seekBar1);
				seekBar1.setMax(duration);
				seekBar1.setOnSeekBarChangeListener(seekBarOnSeekBarChange);
				
		        // Create the Visualizer object and attach it to our media player.
			    // We need to link the visualizer view to the media player so that
			    // it displays something
			    mVisualizerView = //new VisualizerView(RecordActivity.this);
			    (VisualizerView) findViewById(R.id.visualizerView);
			    mVisualizerView.link(mPlayer);
			    mVisualizerView.setRenderWidth(1);
			    mVisualizerView.setRenderDuration(duration);

			    // Start with just line renderer
			    addLineRenderer();
						
			    mPlayer.start();
			    
				if (D_METHOD) {
					Log.w(TAG, "In playVoice " + getVoiceFilePath());
					Log.w(TAG, "In playVoice - duration " + duration);
				}
	
				handler.post(updatesb);
				//用一个handler更新SeekBar
			}
	 
	    };
	    Runnable updatesb =new Runnable(){
	 
			@Override
			public void run() {
				// TODO Auto-generated method stub
				seekBar1.setProgress(mPlayer.getCurrentPosition());
				seekBar1.setBackground(mVisualizerView.getBitmap());
				mVisualizerView.setRenderWidth(seekBar1.getProgress());
				mVisualizerView.setRenderDuration(mPlayer.getDuration());
				handler.postDelayed(updatesb, 100);
				mPlayer.seekTo(seekBar1.getProgress());
				int time = mPlayer.getCurrentPosition()/1000;
				String sec = df2.format(time %60);
				String min = df2.format(time /60);
				play_time_text.setText(""+min+":"+sec);
				//每秒钟更新一次
				if(!mPlayer.isPlaying()){
					stopPlay();
					return;
				}
			}
	 
	    };
	    
	    public void stopPlay(){
	    	
	    	mPlayer.stop();
	    	
	    	Log.w(TAG, "In stop Play");
			imagebutton_stop.setVisibility(View.INVISIBLE);
			imagebutton_stop.setClickable(false);
			
			imagebutton_play.bringToFront();
			imagebutton_play.setVisibility(View.VISIBLE);
			imagebutton_play.setClickable(true);
			
			button_confirm.setClickable(true);
			return;
	    }

	    /*pause or stop ?*/
	    Runnable continuePlay = new Runnable(){
	   	 
			@Override
			public void run() {
				if (getVoiceFilePath() == null) {
					return;
				}

				button_confirm.setClickable(false);
				
			    mPlayer.seekTo(seekBar1.getProgress());
			    
				if (D_METHOD) {
					Log.w(TAG, "In playVoice " + getVoiceFilePath());
				}
	
				handler.post(updatesb);
				//用一个handler更新SeekBar
			}
	 
	    };
	  
	  
	
}/*RecordActivity*/
