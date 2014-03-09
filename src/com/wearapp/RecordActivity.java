package com.wearapp;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;




import com.facebook.Session;
import com.facebook.SessionState;
import com.parse.ParseFile;
import com.pheelicks.visualizer.MediaSeekBar;
import com.pheelicks.visualizer.RecorderSeekBar;
import com.pheelicks.visualizer.renderer.PointRenderer;
import com.pheelicks.visualizer.renderer.WaveRenderer;
import com.wearapp.exception.FacebookUtil.FacebookSessionNotActive;
import com.wearapp.parseAPI.ParseAPI;
import com.wearapp.util.DB;
import com.wearapp.util.FacebookOpenSessionDoneDelegate;
import com.wearapp.util.FacebookUtil;
import com.wearapp.util.UploadUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
	private File recordFile;

	DecimalFormat df2 = new DecimalFormat("00");

	private DB mDBHelper;
	private Cursor mCursor;
	
	// /////////////////////////////////////////
	// UI
	// /////////////////////////////////////////

	ImageButton imagebutton_record;
	ImageButton imagebutton_stop;
	ImageButton imagebutton_play;
	ImageButton imagebutton_pause;
	Button button_confirm;

	TextView play_time_text;

	public static LayoutInflater mInflater;


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
	private MediaSeekBar  mMediaSeekBar;
	private RecorderSeekBar mRecorderSeekBar;

	private MediaState mediaState;
	
	private String VOICE_FILE_PATH;
	
	protected Handler handler = new Handler();

	enum MediaState {
		Default, isStartPlayState, isPlayingState, isPlayStopState, isRecordingState, isRecordedState
	}

	public void setMediaState(MediaState mediastate) {
		this.mediaState = mediastate;

	}
	
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
	protected void onStart() {
		super.onStart();
		if (D_METHOD) {
			Log.w(TAG, "onStart");
		}

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.record_activity);

		initView();
		button_confirm.setOnClickListener(this);
		imagebutton_record.setOnClickListener(this);
		imagebutton_stop.setOnClickListener(this);
		imagebutton_play.setOnClickListener(this);
		setMediaState(MediaState.Default);
		button_confirm.setVisibility(View.INVISIBLE);
		button_confirm.setClickable(false);
		
		/*Start record directly */
		setMediaState(MediaState.isRecordingState);
		setButton(mediaState);
		
		/*Make the screen horizon*/
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		handler.post(recordVoiceAsync);
		
	}

	public void initView() {
		
		// We initial view members and hide unused view.
		imagebutton_record = (ImageButton) findViewById(R.id.imagebutton_record);
		imagebutton_stop = (ImageButton) findViewById(R.id.imagebutton_stop);
		imagebutton_stop.setVisibility(View.INVISIBLE);
		imagebutton_play = (ImageButton) findViewById(R.id.imagebutton_play);
		imagebutton_play.setVisibility(View.INVISIBLE);
		imagebutton_pause = (ImageButton)findViewById(R.id.imagebutton_pause);
		imagebutton_pause.setVisibility(View.INVISIBLE);
		
		button_confirm = (Button) findViewById(R.id.button_confirm);
		imagebutton_record.bringToFront();
		
		play_time_text = (TextView)findViewById(R.id.play_time_text);
		mMediaSeekBar = (MediaSeekBar)findViewById(R.id.mediaseekbar);
		mMediaSeekBar.setVisibility(View.INVISIBLE);
		mRecorderSeekBar = (RecorderSeekBar)findViewById(R.id.recorderseekbar);
		addLineRenderer();
		addPointRenderer();
		GlobalAction globalAction = (GlobalAction)this.getApplicationContext();

		globalAction.setActionBar(getActionBar());
		setTextView();
		return;
	}
	private void setTextView() {
		int id = -1;
		TextView textview;
		for (int i = 0; i < 5; i++) {
			String identifier = df2.format(i + 1);
			id = getResources().getIdentifier("text_time_sep_" + identifier,
					"id", RecordActivity.this.getPackageName());
			textview = (TextView) findViewById(id);
			textview.setText("00:" + i + "0");
		}

	}//setTextView()
	
	private void addLineRenderer() {
		Paint linePaint = initLinePaint();
		Paint lineFlashPaint = initLineFlashPaint();

		WaveRenderer waveRenderer = new WaveRenderer(linePaint, lineFlashPaint,
				false);
		mMediaSeekBar.addRenderer(waveRenderer);
	}

	private void addPointRenderer() {
		Paint linePaint = initLinePaint();
		Paint lineFlashPaint = initLineFlashPaint();
		PointRenderer pointRenderer = new PointRenderer(linePaint,
				lineFlashPaint, false);
		mRecorderSeekBar.addRenderer(pointRenderer);
	}

	private Paint initLinePaint() {
		Paint linePaint = new Paint();
		
		linePaint.setStrokeWidth(4f);
		linePaint.setStrokeJoin(Paint.Join.ROUND);
		linePaint.setStrokeCap(Paint.Cap.ROUND);
		linePaint.setAntiAlias(true);
		linePaint.setColor(Color.argb(88, 0, 128, 255));

		return linePaint;
	}

	private Paint initLineFlashPaint() {
		Paint lineFlashPaint = new Paint();

		lineFlashPaint.setStrokeWidth(2f);
		lineFlashPaint.setStrokeJoin(Paint.Join.ROUND);
		lineFlashPaint.setStrokeCap(Paint.Cap.ROUND);
		lineFlashPaint.setAntiAlias(true);
		lineFlashPaint.setColor(Color.argb(255, 255, 255, 255));

		return lineFlashPaint;

	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.imagebutton_record) {
			setButton(MediaState.isRecordingState);
			startRecord();
			setMediaState(MediaState.isRecordingState);
			
			return;
			
		} else if (id == R.id.imagebutton_stop) {
			if (mPlayer != null && mediaState == MediaState.isPlayingState) {
				setButton(MediaState.isPlayStopState);
				mPlayer.stop();
				mMediaSeekBar.getVisualizer().setEnabled(false);
				mMediaSeekBar.setFinished();
				setMediaState(MediaState.isPlayStopState);	
			}
			if (mRecorder != null) {
				setButton(MediaState.isStartPlayState);
				stopRecord();
				defaultRecorder();
				setMediaState(MediaState.isStartPlayState);
				
			}
			return;
		} else if (id == R.id.button_confirm) {

			if (mediaState == MediaState.isPlayStopState) {
				insert2DB();
				startCheckPlaceActivity();
				prepareUploadFile();
				setButton(MediaState.Default);
				defaultMediaPlayer();
				setMediaState(MediaState.Default);		
			}		
			return;
		
		} else if (id == R.id.imagebutton_play) {
			if ( mediaState == MediaState.isStartPlayState
				 || mediaState == MediaState.isPlayStopState) {
				setButton(MediaState.isPlayingState);
				setMediaPlayer();
				mPlayer.prepareAsync();
				setMediaState(MediaState.isPlayingState);
			}
			return;
		}

		return;
	}//onClick()
	
	
	public void startRecord() {
		try {
			recordFunction();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}/* startRecord() */
	
	
	Long recordtime;
	public void recordFunction() throws IOException{
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

		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
		}
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
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
		mRecorderSeekBar.link(mRecorder);
		mRecorderSeekBar.setMax(60000);
		mRecorderSeekBar.setProgress(0);
		mRecorderSeekBar.setUnFinished();
		mediaState = MediaState.isRecordingState;
		recordtime = System.currentTimeMillis();

		handler.post(updatesb);
	}/*recordFunction()*/
	
	public void stopRecord() {
		if (mRecorder != null) {
			if (D_METHOD) {
				Log.w(TAG, "In stopRecord");
			}

			mRecorder.stop();
			mRecorder.reset(); // You can reuse the object by going back to
			mRecorderSeekBar.reDraw();
			mRecorderSeekBar.setFinished();
			// setAudioSource() step
			// mRecorder.release(); // Now the object cannot be reused
			// mRecorder = null;
		}

	}

	private void defaultRecorder() {
		
		if (mRecorder != null){
			mRecorder.release();
		}
		mRecorder = null;

	}

	private void defaultMediaPlayer() {
		mMediaSeekBar.getVisualizer().setEnabled(false);
		mMediaSeekBar.setFinished();
		if (mPlayer != null) {
			mPlayer.release();
		}
		mPlayer = null;
	}

	public void insert2DB(){
		mDBHelper = new DB (RecordActivity.this);
		mDBHelper.open();
		mDBHelper.create(VOICE_FILE_PATH);//Data path
		mDBHelper.close();
	}

	public void setMediaPlayer() {
		if (getVoiceFilePath() == null) {
			return;
		}

		if (mPlayer == null) {
			mPlayer = new MediaPlayer();
			mPlayer.setOnPreparedListener(preparedListener);
			try {
				mPlayer.setDataSource(this.getVoiceFilePath());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}//setMediaPlayer()

	public void playVoice() {

		if (D_METHOD) {
			Log.w(TAG, "In playVoice " + getVoiceFilePath());
			Log.w(TAG, "In playVoice   duration " + mPlayer.getDuration());
		}
		// 音乐文件持续时间
		//mMediaSeekBar.setProgress(0);
		// mMediaSeekBar.setOnSeekBarChangeListener(seekBarOnSeekBarChange);

		// Create the Visualizer object and attach it to our media player.
		// We need to link the visualizer view to the media player so that
		// it displays something

		mMediaSeekBar.link(mPlayer);
		mPlayer.setOnCompletionListener(completelistener);
		mMediaSeekBar.setUnFinished();
	}

	public String getDate() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyMMddHHmmss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		// System.out.println(strDate);
		return strDate;
	}/* getDate */

	private OnPreparedListener preparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer player) {
			Log.i(TAG, "In Prepare  Async");
			playVoice();
			player.start();
			handler.postDelayed(updatesb, 10);
		}

	};

	MediaPlayer.OnCompletionListener completelistener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			Log.i(TAG, "In OnCompletionListener");
			mMediaSeekBar.reDraw();
			mMediaSeekBar.getVisualizer().setEnabled(false);
			mMediaSeekBar.setFinished();
			mediaPlayer.stop();
			setMediaState(MediaState.isPlayStopState);
			handler.postDelayed(updatesb, 10);
		}
	};

		//////////////////////////////////////////////////////////////
		//			Handler Area									//
		//														    //
		//////////////////////////////////////////////////////////////
		int maxProgress =0;

	Runnable updatesb = new Runnable() {
		@Override
		public void run() {
			//Log.w(TAG, "In updatesb()");
			if (mPlayer != null && mPlayer.isPlaying()) {

				int time = mPlayer.getCurrentPosition() / 1000;
				String sec = df2.format(time % 60);
				String min = df2.format(time / 60);
			    play_time_text.setText("" + min + ":" + sec);
				//play_time_text.setText(" " + mMediaSeekBar.getProgress()
				//		+ " Max:" + maxProgress);
				if (mMediaSeekBar.getProgress() > maxProgress) {
					maxProgress = mMediaSeekBar.getProgress();
				}
				
				handler.postDelayed(updatesb, 10);

			}
			
			if (mediaState == MediaState.isPlayStopState) {
				setButtonStopPlay();
			}

			if (mRecorder != null && mediaState == MediaState.isRecordingState) {
				Long timetmp = (System.currentTimeMillis() - recordtime) / 1000;
				String sectmp = df2.format(timetmp % 60);
				String mintmp = df2.format(timetmp / 60);
				//String amplitutde = df2.format(mRecorder.getMaxAmplitude());
				play_time_text.setText("" + mintmp + ":" + sectmp);
				int time_duration = (int) (System.currentTimeMillis() - recordtime);
				mRecorderSeekBar.setProgress(time_duration);
				handler.postDelayed(updatesb,10);
			}

		}
	};
	
	Runnable recordVoiceAsync = new Runnable() {
		@Override
		public void run() {
			Log.w(TAG, "In recordAsync()");		
			startRecord();
		}
	};


	private void prepareUploadFile() {
		
		UploadUtil.setUploadFile(recordFile);
	
		//new UploadAsyncTask().execute(recordFile);
	}

	private void startCheckPlaceActivity() {
		FacebookUtil.ensureThenOpenActiveSession(this, new FacebookOpenSessionDoneDelegate() {
			
			@Override
			public void postExec(Session session, SessionState state, Exception exception) {
				// TODO Auto-generated method stub
				
			}
		});
		Intent intent = new Intent(this, CheckVoiceActivity.class);
		startActivity(intent);
	}
	
	
	private void setButton(MediaState mediastate) {
		switch (mediastate) {
		case isRecordingState:
			setButtonStartRecord();
			break;

		case isPlayingState:
			setButtonPlaying();
			break;

		case isRecordedState:
			setButtonRecorded();
			break;

		case isPlayStopState:
			setButtonStopPlay();
			break;

		case Default:
			resetAll();
			break;

		case isStartPlayState:
			setButtonStartPlaying();
			break;
		default:
			break;
		}
	}

	public void setButtonStartRecord() {
		Log.w(TAG, "setButtonStartRecord");
		// textview_status.setText(R.string.status_recording);
		// button_confirm.setText(R.string.string_complete);
		// button_confirm.setClickable(false);
		imagebutton_record.setVisibility(View.INVISIBLE);
		imagebutton_record.setClickable(false);

		imagebutton_stop.bringToFront();
		imagebutton_stop.setVisibility(View.VISIBLE);
		imagebutton_stop.setClickable(true);

	}

	private void setButtonRecorded() {
		Log.w(TAG, "setButtonRecorded");
		// textview_status.setText(R.string.string_recordtext);
		imagebutton_stop.setVisibility(View.INVISIBLE);
		imagebutton_stop.setClickable(false);

		imagebutton_record.bringToFront();
		imagebutton_record.setVisibility(View.VISIBLE);
		imagebutton_record.setClickable(true);
		button_confirm.setVisibility(View.VISIBLE);
		button_confirm.setText(R.string.string_complete);
		button_confirm.setClickable(true);

	}

	/*This function will be called twice. 
	 * One is stopping during playing, 
	 * the other is playing at the end.*/
	public void setButtonStopPlay() {
		Log.w(TAG, "setButtonStopPlay");

		imagebutton_stop.setVisibility(View.INVISIBLE);
		imagebutton_stop.setClickable(false);
		
		imagebutton_play.bringToFront();
		imagebutton_play.setVisibility(View.VISIBLE);
		imagebutton_play.setClickable(true);
		
		button_confirm.setVisibility(View.VISIBLE);
		button_confirm.setText(R.string.string_complete);
		button_confirm.setClickable(true);
		return;
	}

	public void resetAll() {
		Log.w(TAG, "In resetAll");
		imagebutton_play.setVisibility(View.INVISIBLE);
		imagebutton_stop.setVisibility(View.INVISIBLE);
		imagebutton_record.setVisibility(View.VISIBLE);
		button_confirm.setText(R.string.string_start);
		imagebutton_record.bringToFront();

		imagebutton_record.setClickable(true);
		button_confirm.setClickable(false);

	}

	private void setButtonStartPlaying() {
		Log.w(TAG, "setButtonStartPlaying");
		mRecorderSeekBar.setVisibility(View.INVISIBLE);
		mMediaSeekBar.setVisibility(View.VISIBLE);
		
		imagebutton_play.bringToFront();
		imagebutton_play.setVisibility(View.VISIBLE);
		imagebutton_play.setClickable(true);

		imagebutton_record.setVisibility(View.INVISIBLE);
		imagebutton_stop.setVisibility(View.INVISIBLE);

		imagebutton_record.setClickable(false);
		imagebutton_stop.setClickable(false);

		button_confirm.setClickable(false);
		button_confirm.setText("");
	}

	private void setButtonPlaying() {
		Log.w(TAG, "setButtonPlaying");
		button_confirm.setText("");
		button_confirm.setClickable(false);
		// button_confirm.setText(R.string.string_confirm);

		imagebutton_play.setClickable(false);
		imagebutton_play.setVisibility(View.INVISIBLE);

		imagebutton_stop.bringToFront();
		imagebutton_stop.setVisibility(View.VISIBLE);
		imagebutton_stop.setClickable(true);

	}

	
	

	
	//////////////////////////////////////////////
	//  		Listener						//
	//											//
	//////////////////////////////////////////////
	
	
	// SeekBar
		private SeekBar.OnSeekBarChangeListener seekBarOnSeekBarChange = new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				// mPlayer.seekTo(seekBar1.getProgress());
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
	
}/* RecordActivity */
