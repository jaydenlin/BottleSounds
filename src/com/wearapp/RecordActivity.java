package com.wearapp;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class RecordActivity extends Activity implements OnClickListener{

	///////////////////////////////////////////
	// debug
	///////////////////////////////////////////	
	
	public static final String TAG = RecordActivity.class.getSimpleName();

	public static final boolean D = true;
	public static final boolean D_METHOD = D && true;
	
	public static final boolean D_SHOW_TAG = D && true;
	
	public float startTime;
	
	///////////////////////////////////////////
	// UI
	///////////////////////////////////////////		

	ImageButton imagebutton_record;
	ImageButton imagebutton_stop;
	Button button_record_again;
	Button button_confirm;

	///////////////////////////////////////////
	// handler
	///////////////////////////////////////////			

	UIHandler mUIHandler;

	class UIHandler extends Handler {
		public void handleMessage(Message msg) {

		}
	}
	
	////////////////////////////////////////////
	//          Media Controller              //
	////////////////////////////////////////////
	private  MediaRecorder mRecorder;
	private  MediaPlayer   mPlayer ;
	private  String VOICE_FILE_PATH;
	
	public void setVoiceFilePath(String filepath){
        if(D_METHOD){
            Log.w(TAG, "Set Voice File Path"+ filepath);
        }
        this.VOICE_FILE_PATH = filepath;
    }

    public String getVoiceFilePath(){

        return this.VOICE_FILE_PATH;
    }

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		
		setContentView(R.layout.record_activity);

		initView();
		button_confirm.setOnClickListener(this);
		button_record_again.setOnClickListener(this);
		imagebutton_record.setOnClickListener(this);
		imagebutton_stop.setOnClickListener(this);
	}
	
	
	
	@Override
	public void onClick(View view) {
		switch (view.getId()){
		case R.id.imagebutton_record:
			try {
				startRecord();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imagebutton_stop.bringToFront();
			imagebutton_stop.setVisibility(View.VISIBLE);
			imagebutton_stop.setClickable(true);;
			
			imagebutton_record.setVisibility(View.INVISIBLE);
			imagebutton_record.setClickable(false);
			return;
			
		case R.id.imagebutton_stop:
			stopRecord();
			imagebutton_stop.setVisibility(View.INVISIBLE);
			imagebutton_stop.setClickable(false);
			
			imagebutton_record.bringToFront();
			imagebutton_record.setVisibility(View.VISIBLE);
			imagebutton_record.setClickable(true);
			
			return;
		case R.id.button_record_again:
			
			return;
		
		case R.id.button_confirm:
			
			return;
		}
		
		
		return;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (D_METHOD) {
			Log.w(TAG, "onStart");
		}

	}
	
	
	public void hideImageButton(View view){
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

	public void initView(){
		imagebutton_record = (ImageButton) findViewById(R.id.imagebutton_record);
		imagebutton_stop = (ImageButton) findViewById(R.id.imagebutton_stop);
		button_record_again = (Button) findViewById(R.id.button_record_again);
		button_confirm = (Button) findViewById(R.id.button_confirm);
		
		imagebutton_record.bringToFront();
		
		return;
	}
	
    public void startRecord() throws IOException {
        if(D_METHOD){
            Log.w(TAG, "In startRecord");
        }

        String filename  =  getDate();
        File fExternalDataPath = Environment.getExternalStorageDirectory();
        File myDataPath = new File( fExternalDataPath.getAbsolutePath() + "/record" );
        if( !myDataPath.exists() ) myDataPath.mkdirs();
        File recordFile = new File(fExternalDataPath.getAbsolutePath() + "/record/"+filename);
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
        mRecorder.start();   // Recording is now started



    }/*startRecord()*/

    public void stopRecord(){
        if(mRecorder!=null){
            if(D_METHOD){
                Log.w(TAG, "In stopRecord");
            }

        mRecorder.stop();
        //mRecorder.reset();   // You can reuse the object by going back to setAudioSource() step
        mRecorder.release(); // Now the object cannot be reused
        mRecorder = null;
        }

    }

    public void playVoice(){

    if(getVoiceFilePath()==null){ return;}

        mPlayer  = new MediaPlayer();
        try {
            mPlayer.setDataSource(getVoiceFilePath());
            mPlayer.prepare();
        } catch (IllegalArgumentException e) {
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }

        mPlayer.start();


        if(D_METHOD){
            Log.w(TAG, "In playVoice"+getVoiceFilePath());
        }
    }


    public String getDate(){
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String strDate = sdFormat.format(date);
        //System.out.println(strDate);
        return strDate;
    }/*getDate*/
	
	
}
